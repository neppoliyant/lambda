#!/usr/bin/env bash

deleteExistingExport() {
    rm -f /data/export/api.jar
}

generateApiJar() {
    cd /data
    echo "calling generator: java -cp ${LAMBDA_JAR}:/data/lambda-generator/target/lambda-generator-1.0-SNAPSHOT.jar com.digitalsanctum.lambda.generator.Generator ${LAMBDA_JAR} ${LAMBDA_HANDLER} ${LAMBDA_RESOURCE_PATH} ${LAMBDA_HTTP_METHOD} ${LAMBDA_TIMEOUT}"
    java -cp ${LAMBDA_JAR}:/data/lambda-generator-1.0-SNAPSHOT.jar com.digitalsanctum.lambda.generator.Generator ${LAMBDA_JAR} ${LAMBDA_HANDLER} ${LAMBDA_RESOURCE_PATH} ${LAMBDA_HTTP_METHOD} ${LAMBDA_TIMEOUT}
    echo "generation complete!"
}

deleteExistingExport && generateApiJar