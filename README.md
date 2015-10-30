
# lambda

A proof of concept self-hosted micro architecture heavily inspired by [AWS Lambda](https://aws.amazon.com/lambda/).

## Features
- Compatible with AWS Lambda - the same artifacts will work
- Meant to be self-hosted

## Lambda Variables

- LAMBDA_JAR - path to the lambda jar.
- LAMBDA_HANDLER - the lambda class which implements RequestHandler<I,O>
- LAMBDA_HTTP_METHOD - only GET or POST is currently supported. default is POST
- LAMBDA_RESOURCE - the http path to map to the handler (for example "/hello")
- LAMBDA_TIMEOUT - the maximum amount of time to wait for a call to finish



## TODO
- easy deploy to public clouds
    - start with AWS EC2 and Digital Ocean
    - jclouds
- generator
    - lambda documentation?
    - HTTP client
- console/meta
    - with persistence to map functions to endpoints
    - manage lifecycle of lambdas
    - deploy to public clouds (parameterize Terraform?)
    - paste Java code for the lambda and use a container to compile
- metrics
- scheduler
- events
    - S3
    - Github webhook

## Half-baked ideas
- configuration for function and how/where it's hosted
- Reference code in github.com for a lambda
- Use lambdas to generate other lambdas
- chaining and/or side car lambdas
- marketplace


## Inspiration
- http://tech.adroll.com/blog/data/2015/09/22/data-pipelines-docker.html
- Nashorn javascript engine


    curl -X POST -d 'shane' 'http://localhost:8080/hello'


    docker run -d -e "LAMBDA_TIMEOUT=3" -e "LAMBDA_HANDLER=com.digitalsanctum.lambda.samples.HelloPojo" --name api -p 8080:8080 digitalsanctum/lambda-api

    curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Shane", "lastName":"Witbeck"}' 'http://localhost:8080/hello'
    curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Shane", "lastName":"Witbeck"}' 'http://162.243.187.105:8080/hello'
    curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Shane", "lastName":"Witbeck"}' 'http://lambda-api-4f03eb87.digitalsanctum.svc.tutum.io:8080/hello'


## Uber Config

### Generator

Generates export/api.jar

- LAMBDA_JAR - path of shaded jar containing lambda class
- LAMBDA_HANDLER - fully qualified lambda handler class
- LAMBDA_RESOURCE_PATH - request context of lambda function
- LAMBDA_HTTP_METHOD - HTTP method of lambda handler
- LAMBDA_TIMEOUT - execution timeout before lambda function is terminated
- LAMBDA_MAX_MEMORY - value for Xmx to run lambda api app (should be less than DO_SIZE)
- LAMBDA_API_JAR - path of exported api gateway jar (export/api.jar)


### Provisioner

Creates a droplet, pulls the lambda-api docker image and runs the docker container.

- LAMBDA_DOCKER_EMAIL
- LAMBDA_DOCKER_USERNAME
- LAMBDA_DOCKER_PASSWORD
- LAMBDA_DOCKER_IMAGE
- LAMBDA_DO_TOKEN - digitalocean token
- LAMBDA_DO_SIZE - memory size of droplet. For example, "512mb"
- LAMBDA_DO_REGION - geographic region slug. For example, "sfo1"
- LAMBDA_PORT - optional; defaults to 8080



