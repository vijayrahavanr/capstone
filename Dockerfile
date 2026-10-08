FROM eclipse-temurin:26-jdk

ENV TOMCAT_VERSION=9.0.122

RUN apt-get update && apt-get install -y curl maven && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /opt/tomcat && \
    curl -fL https://dlcdn.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar -xz -C /opt/tomcat --strip-components=1

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests && \
    rm -rf /opt/tomcat/webapps/ROOT && \
    mkdir -p /opt/tomcat/webapps/ROOT && \
    cd /opt/tomcat/webapps/ROOT && \
    jar -xf /app/target/VRMart.war

RUN chmod +x /opt/tomcat/bin/*.sh

EXPOSE 8080

CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-8080}\\\"/\" /opt/tomcat/conf/server.xml && exec /opt/tomcat/bin/catalina.sh run"]