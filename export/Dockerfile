#
# Lambda API Gateway Dockerfile
#
FROM jeanblanchard/busybox-java:jdk8
MAINTAINER Shane Witbeck <shane@digitalsanctum.com>

ENV API_GATEWAY_ARTIFACT api.jar
ADD $API_GATEWAY_ARTIFACT /data/
WORKDIR /data
CMD java -Xmx256m -jar /data/$API_GATEWAY_ARTIFACT
