#!/usr/bin/env bash

generateApiJar() {
    cd /data
    pwd
    ls -la
    echo "calling generator: java -cp ${LAMBDA_JAR}:/data/lambda-generator/target/lambda-generator-1.0-SNAPSHOT.jar com.digitalsanctum.lambda.generator.Generator ${LAMBDA_JAR} ${LAMBDA_HANDLER} ${LAMBDA_RESOURCE_PATH} ${LAMBDA_HTTP_METHOD} ${LAMBDA_TIMEOUT}"
    java -cp ${LAMBDA_JAR}:/data/lambda-generator-1.0-SNAPSHOT.jar com.digitalsanctum.lambda.generator.Generator ${LAMBDA_JAR} ${LAMBDA_HANDLER} ${LAMBDA_RESOURCE_PATH} ${LAMBDA_HTTP_METHOD} ${LAMBDA_TIMEOUT}
    echo "generation complete!"
    ls -la ~/.m2
}

exportApiJar() {
    echo "copying api.jar..."
    cp /data/template/target/lambda-api-gateway-1.0-SNAPSHOT.jar /data/export/api.jar
    echo "done copying"
}

env && generateApiJar && exportApiJar
