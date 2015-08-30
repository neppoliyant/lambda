#
# Lambda Builder Dockerfile
#
FROM digitalsanctum/maven-jdk8-debian

ADD . /data/
WORKDIR /data

# lambda defaults
ENV LAMBDA_HTTP_METHOD=POST LAMBDA_TIMEOUT=5

CMD ["/data/build.sh"]