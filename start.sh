#!/usr/bin/env bash

cd export
docker build -t digitalsanctum/lambda-api .
docker run -d -p 8080:8080 --name api digitalsanctum/lambda-api

