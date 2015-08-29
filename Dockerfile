#
# Lambda Builder Dockerfile
#
FROM centos:centos6

RUN yum --enablerepo=centosplus install -y git tar wget

# install Java (for Java 8, -e="JDK_VERSION=8u25" -e="JDK_BUILD_VERSION=b17")
ENV JDK_VERSION 8u45
ENV JDK_BUILD_VERSION b14
RUN curl -LO "http://download.oracle.com/otn-pub/java/jdk/$JDK_VERSION-$JDK_BUILD_VERSION/jdk-$JDK_VERSION-linux-x64.rpm" -H 'Cookie: oraclelicense=accept-securebackup-cookie' && rpm -i jdk-$JDK_VERSION-linux-x64.rpm; rm -f jdk-$JDK_VERSION-linux-x64.rpm; yum clean all
ENV JAVA_HOME /usr/java/default

# install Maven
ENV MAVEN_VERSION 3.3.3
RUN cd /tmp && wget -nv http://apache.mirrors.lucidnetworks.net/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz
RUN tar -xzf /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz -C /usr/local
RUN ln -s /usr/local/apache-maven-$MAVEN_VERSION /usr/local/maven
RUN rm /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz
ENV M2_HOME /usr/local/maven
ENV MAVEN_HOME /usr/local/maven
ENV PATH $M2_HOME/bin:$PATH

ADD . /data/
WORKDIR /data

# lambda defaults
ENV LAMBDA_HTTP_METHOD POST
ENV LAMBDA_TIMEOUT 5

CMD ["/data/build.sh"]