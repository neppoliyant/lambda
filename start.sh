#!/usr/bin/env bash

docker kill api
docker rm api
cd export
docker build -t digitalsanctum/lambda-api .
docker run -d \
    -e "LAMBDA_TIMEOUT=3" \
    -e "LAMBDA_HANDLER=com.digitalsanctum.lambda.samples.HelloPojo" \
    --name api -p 8080:8080 digitalsanctum/lambda-api
docker logs -f api

