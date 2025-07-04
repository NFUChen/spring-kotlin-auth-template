# Variables
IMAGE_NAME = softlancer
CURRENT_GIT_HASH = $(shell git rev-parse --short HEAD)
PREVIOUS_GIT_HASH = $(shell git rev-parse --short HEAD~1)
DOCKER_TAG = $(IMAGE_NAME):$(CURRENT_GIT_HASH)

.PHONY: all test clean dev
dev:
	./gradlew bootRun

test:
	./gradlew test

docker-run:
	docker run --rm -p 8080:8080 -v $(shell pwd)/src/main/resources:/app/resources $(DOCKER_TAG)


docker-build:
	docker buildx build --platform linux/arm64 -t $(DOCKER_TAG) --load .




# Push the multi-arch image to the registry
docker-push:
	docker buildx build --platform linux/amd64,linux/arm64 -t $(DOCKER_TAG) --push .

verify:
	docker buildx imagetools inspect $(DOCKER_TAG)