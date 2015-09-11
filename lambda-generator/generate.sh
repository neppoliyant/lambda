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

exportApiJar() {
    echo "copying api.jar..."
#    cp /data/template/target/lambda-api-gateway-1.0-SNAPSHOT.jar /data/export/api.jar
    cp /data/template/target/lambda-api-gateway-min-1.0-SNAPSHOT.jar /data/export/api.jar
    echo "done copying"
}

deleteExistingExport && generateApiJar && exportApiJar
