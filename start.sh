#!/usr/bin/env bash

cd export
docker build -t lambda/api .
docker run -d -p 8080:8080 --name api lambda/api

