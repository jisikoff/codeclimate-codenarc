#
# Build stage
#
FROM groovy:jre8-alpine as builder
MAINTAINER "Jeremy Isikoff <jeremy+codeclimate-codenarc@cinchfinancial.com>"

USER root

RUN apk update && \
    apk add ca-certificates wget curl jq && \
    update-ca-certificates

COPY . /usr/src/app

WORKDIR /usr/src/app

RUN ./gradlew clean compileGroovy infra test --info

#
# Runtime
#
FROM groovy:jre8-alpine
MAINTAINER "Jeremy Isikoff <jeremy+codeclimate-codenarc@cinchfinancial.com>"

USER root

WORKDIR /usr/src/app

# Test and debug depedencies
COPY --from=builder /usr/src/app/Dockerfile /usr/src/app/Dockerfile
COPY --from=builder /usr/src/app/engine.json /usr/src/app/engine.json
COPY --from=builder /usr/src/app/build.gradle /usr/src/app/build.gradle
COPY --from=builder /usr/src/app/gradle /usr/src/app/gradle
COPY --from=builder /usr/src/app/gradlew /usr/src/app/gradlew
COPY --from=builder /usr/src/app/fixtures /usr/src/app/fixtures

# Runtime dependencies
COPY --from=builder /usr/src/app/src /usr/src/app/src
COPY --from=builder /usr/src/app/lib /usr/src/app/lib
COPY --from=builder /usr/src/app/codenarc /usr/src/app/codenarc


RUN adduser -u 9000 -D app

VOLUME /code
WORKDIR /code

RUN chown -R app:app /usr/src/app

USER app

CMD ["/usr/src/app/codenarc", "--codeFolder=/code", "--configFile=/config.json"]
