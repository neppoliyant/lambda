#!/usr/bin/env bash

# Given lambda parameters, create an api gateway
gen() {

    env

    echo "installing lambda-core..."
    cd lambda-core
    mvn clean install -DskipTests

    echo "calling generator..."
    cd ..
    java -cp ${LAMBDA_JAR}:lambda-generator/target/lambda-generator-1.0-SNAPSHOT.jar com.digitalsanctum.lambda.generator.Generator ${LAMBDA_JAR} ${LAMBDA_HANDLER} ${LAMBDA_RESOURCE_PATH} ${LAMBDA_TIMEOUT}

    echo "generation complete!"
}

# copy the generated api.jar to the export dir
exportApi() {
    cp lambda-api-gateway/target/lambda-api-gateway-1.0-SNAPSHOT.jar /data/export/api.jar
}

gen;
exportApi;




