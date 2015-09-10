#!/usr/bin/env bash

buildLambda() {
    mvn -f lambda-core clean install -DskipTests
    mvn -f lambda-generator clean install -DskipTests
}

buildLambdaDockerImage() {
    docker build -f lambda-generator/Dockerfile -t digitalsanctum/lambda-builder .
}

buildLambda && buildLambdaDockerImage

