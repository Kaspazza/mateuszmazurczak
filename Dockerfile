FROM --platform=linux/amd64 hephaistox/cc_image:latest
WORKDIR /usr/app

# Copy this first help to build only dependencies in this layer, code modification will skip until next block
COPY deps.edn .
RUN clj -P
COPY package-lock.json .
COPY package.json .
COPY shadow-cljs.edn .
RUN npm update

# Source code update starts here
COPY build/ /usr/app/build/
COPY env/ /usr/app/env/
COPY lib /usr/app/lib/
COPY resources /usr/app/resources/
COPY src/ /usr/app/src/
COPY bb.edn /usr/app/
COPY tailwind.config.js /usr/app/
COPY version.edn /usr/app/

# Copy automaton code to allow automaton cljs compiling
WORKDIR /usr/
RUN git clone https://github.com/hephaistox/automaton.git

WORKDIR /usr/app

# Build and run
RUN bb build-cljs
CMD bb run-clj
