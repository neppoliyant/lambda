#!/usr/bin/env bash

buildSamples() {
    mvn clean install -DskipTests
}

copySamplesJar() {
    mkdir -p ../import
    cp target/lambda-samples-1.0-SNAPSHOT.jar ../import/lambda.jar
}

buildSamples && copySamplesJar

