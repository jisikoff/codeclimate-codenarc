.PHONY: image test

IMAGE_NAME ?= codeclimate/codeclimate-codenarc

image:
	./gradlew infra
	docker build --rm -t $(IMAGE_NAME) .

test: image
		docker run --rm -ti -w /usr/src/app -u root $(IMAGE_NAME) ./gradlew clean test --info
