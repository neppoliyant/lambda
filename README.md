
# lambda

A proof of concept self-hosted micro architecture heavily inspired by [AWS Lambda](https://aws.amazon.com/lambda/).

## Features
- Compatible with AWS Lambda - the same artifacts will work
- Meant to be self-hosted

## Lambda Variables

LAMBDA_JAR - path to the lambda jar.
LAMBDA_HANDLER - the lambda class which implements RequestHandler<I,O>
LAMBDA_HTTP_METHOD - only GET or POST is currently supported. default is POST
LAMBDA_RESOURCE - the http path to map to the handler (for example "/hello")
LAMBDA_TIMEOUT - the maximum amount of time to wait for a call to finish


## Usage

Build the container that will be responsible for composing and building the api gateway jar (a Spring Boot application)

    docker build -t digitalsanctum/lambda-builder .

Run the lambda/builder container and pass the required environment variables:

Using a fresh builder image (no maven dependencies downloaded)

    docker run -d \
        -e "LAMBDA_JAR=/data/import/lambda.jar" \
        -e "LAMBDA_HANDLER=com.digitalsanctum.lambda.samples.HelloWorld" \
        -e "LAMBDA_RESOURCE_PATH=/hello" \
        -v ~/projects/lambda/import:/data/import \
        -v ~/projects/lambda/export:/data/export \
        --name builder digitalsanctum/lambda-builder:primed /data/build.sh

Or, use the primed image (most maven dependencies already downloaded)

    docker run -d \
    -e "LAMBDA_JAR=/data/import/lambda.jar" \
    -e "LAMBDA_HANDLER=com.digitalsanctum.lambda.samples.HelloWorld" \
    -e "LAMBDA_RESOURCE_PATH=/hello" \
    -v ~/projects/lambda/import:/data/import \
    -v ~/projects/lambda/export:/data/export \
    --name builder digitalsanctum/lambda-builder


Optionally, build and run a container to run the exported api.jar:

    cd export
    docker build -t digitalsanctum/lambda-api .
    docker run -d -p 8080:8080 --name api digitalsanctum/lambda-api


## Priming the Builder Container

First, run interactively:

    docker run -it --rm \
        -e "LAMBDA_JAR=/data/import/lambda.jar" \
        -e "LAMBDA_HANDLER=com.digitalsanctum.lambda.samples.HelloWorld" \
        -e "LAMBDA_RESOURCE_PATH=/hello" \
        -v ~/projects/lambda/import:/data/import \
        -v ~/projects/lambda/export:/data/export \
        --name builder digitalsanctum/lambda-builder /bin/bash

Second, from inside the builder container run /data/build.sh

Last, from the Docker host commit the container.

    docker commit builder digitalsanctum/lambda-builder:primed



## TODO
- easy deploy to public clouds (Terraform?)
    - start with AWS EC2 and Digital Ocean
- generator
    - lambda documentation?
    - HTTP client
- console/meta
    - with persistence to map functions to endpoints
    - manage lifecycle of lambdas
    - deploy to public clouds (parameterize Terraform?)
    - Angular? Spring Boot/Jersey or Dropwizard?
- metrics
- scheduler
- events
    - S3
    - Github webhook

## Half-baked ideas
- Use lambdas to generate other lambdas
- chaining and/or side car lambdas
- marketplace


curl -X POST -d 'shane' 'http://localhost:8080/hello'

