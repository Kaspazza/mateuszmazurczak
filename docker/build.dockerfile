# === Stage 1: Node.js source ===
FROM node:20.19.0-bullseye-slim AS node

# === Stage 2: Babashka source ===

FROM babashka/babashka:1.12.208-alpine AS babashka 

# === Stage 3: Build stage ===
FROM clojure:temurin-21-tools-deps-bullseye-slim AS build

WORKDIR /build

# Copy Node.js and npm from official Node image
COPY --from=node /usr/local/bin/node /usr/local/bin/
COPY --from=node /usr/local/lib/node_modules /usr/local/lib/node_modules
RUN ln -s /usr/local/lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm && \
    ln -s /usr/local/lib/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx

# Copy babashka from official babashka image
COPY --from=babashka /bin/bb /usr/local/bin/bb

# Install build dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libgomp1 && \
    npm install -g shadow-cljs && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy dependency manifests first for better caching
COPY deps.edn package.json package-lock.json bb.edn project.edn shadow-cljs.edn ./
RUN npm ci

# Copy source and config 
COPY env env
COPY src src
COPY resources resources
COPY articles.edn articles.edn

# Build the application
RUN bb build-jar

# === Stage 4: Runtime image ===
FROM eclipse-temurin:21-jre-jammy

# Install runtime dependencies (libgomp1 needed for datalevin runtime)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libgomp1 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /build/target/prod/mateuszmazurczak.jar .

# Expose application port
EXPOSE 8080

# Use container-aware JVM settings (allow ENV override)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:MaxMetaspaceSize=384M -XX:ReservedCodeCacheSize=240M -Xss1M"

# Run app (configure secrets via ENV variables at runtime)
CMD sh -c "java $JAVA_OPTS -Dconf-var=config.edn -cp mateuszmazurczak.jar clojure.main -m mateuszmazurczak.core"

