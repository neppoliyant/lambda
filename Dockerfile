#
# Lambda Builder Dockerfile
#
FROM debian:8.1
RUN apt-get update -y && apt-get install -y curl tar wget

# install Java
ENV JDK_MAJOR_VERSION=1.8 JDK_MINOR_VERSION=0 JDK_UPDATE_VERSION=60 JDK_VERSION=8u60 JDK_BUILD_VERSION=b27
ENV JAVA_HOME /opt/jdk/jdk${JDK_MAJOR_VERSION}.${JDK_MINOR_VERSION}_${JDK_UPDATE_VERSION}
RUN curl -LO "http://download.oracle.com/otn-pub/java/jdk/$JDK_VERSION-$JDK_BUILD_VERSION/jdk-$JDK_VERSION-linux-x64.tar.gz" -H 'Cookie: oraclelicense=accept-securebackup-cookie' \
    && mkdir /opt/jdk \
    && tar -zxf jdk-$JDK_VERSION-linux-x64.tar.gz -C /opt/jdk \
    && update-alternatives --install /usr/bin/java java ${JAVA_HOME}/bin/java 100 \
    && update-alternatives --install /usr/bin/javac javac ${JAVA_HOME}/bin/javac 100 \
    && rm jdk-$JDK_VERSION-linux-x64.tar.gz ${JAVA_HOME}/src.zip ${JAVA_HOME}/javafx-src.zip

# install Maven
ENV MAVEN_VERSION 3.3.3
RUN cd /tmp && wget -nv http://apache.mirrors.lucidnetworks.net/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    && tar -xzf /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz -C /usr/local \
    && ln -s /usr/local/apache-maven-$MAVEN_VERSION /usr/local/maven \
    && rm /tmp/apache-maven-$MAVEN_VERSION-bin.tar.gz
ENV M2_HOME=/usr/local/maven MAVEN_HOME=/usr/local/maven
ENV PATH=$M2_HOME/bin:$PATH

ADD . /data/
WORKDIR /data

# lambda defaults
ENV LAMBDA_HTTP_METHOD=POST LAMBDA_TIMEOUT=5

CMD ["/data/build.sh"]