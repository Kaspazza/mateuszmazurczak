FROM clojure:tools-deps as build

WORKDIR /build

RUN apt update -y && apt upgrade -y
RUN apt install -y --no-install-recommends npm
RUN npm install -g shadow-cljs

RUN bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)

COPY env env
COPY src src
COPY resources resources
COPY bb.edn bb.edn
COPY deps.edn deps.edn
COPY package-lock.json package-lock.json
COPY project.edn project.edn
COPY shadow-cljs.edn shadow-cljs.edn

RUN bb build-jar

FROM amazoncorretto:21 as run

WORKDIR /app

COPY --from=build "/build/target/prod/mateuszmazurczak.jar" .

ENV JAVA_OPTS="-XX:MaxDirectMemorySize=10M -XX:MaxMetaspaceSize=384M -XX:ReservedCodeCacheSize=240M -Xss1M -Xmx3438447K"

CMD java -server ${JAVA_OPTS} -cp mateuszmazurczak.jar clojure.main -m mateuszmazurczak.core

EXPOSE 8080
