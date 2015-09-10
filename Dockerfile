#
# Lambda Builder Dockerfile
#
FROM digitalsanctum/maven-jdk8-debian

ADD . /data/
WORKDIR /data

***REMOVED*** defaults
ENV LAMBDA_HTTP_METHOD=POST LAMBDA_TIMEOUT=5

RUN chmod a+x generate.sh prime.sh