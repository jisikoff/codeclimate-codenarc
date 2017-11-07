FROM groovy:jre8-alpine

MAINTAINER "Jeremy Isikoff <jeremy+codeclimate-codenarc@cinchfinancial.com>"

USER root

RUN apk update && \
    apk add ca-certificates wget curl jq && \
    update-ca-certificates

RUN adduser -u 9000 -D app

VOLUME /code
WORKDIR /code
COPY . /usr/src/app
RUN chown -R app:app /usr/src/app

USER app

CMD ["/usr/src/app/codenarc", "--codeFolder=/code", "--configFile=/config.json"]
