FROM clojure:tools-deps AS build

WORKDIR /build

RUN apt update -y && apt upgrade -y
RUN apt install -y --no-install-recommends npm curl
RUN npm install -g shadow-cljs

RUN curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
RUN chmod +x install
RUN ./install

COPY env env
COPY src src
COPY resources resources
COPY bb.edn bb.edn
COPY deps.edn deps.edn
COPY package.json package.json
COPY package-lock.json package-lock.json
COPY project.edn project.edn
COPY shadow-cljs.edn shadow-cljs.edn

RUN bb build-jar

FROM amazoncorretto:21 AS run

WORKDIR /app

COPY --from=build "/build/target/prod/mateuszmazurczak.jar" .

CMD ["java", "-XX:MaxDirectMemorySize=10M", "-XX:MaxMetaspaceSize=384M", "-XX:ReservedCodeCacheSize=240M", "-Xss1M", "-Xmx3438447K", "-Dconf-var=config.edn", "-cp", "mateuszmazurczak.jar", "clojure.main", "-m", "mateuszmazurczak.core"]

EXPOSE 8080
