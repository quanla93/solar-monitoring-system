FROM openjdk:17-jdk-slim

LABEL maintainer="Solar Monitoring Team"

VOLUME /tmp

ARG JAR_FILE=target/*.jar
ARG CONFIG_FILE=src/main/resources/application.yml

# copy p12 and cer into respective places.
#COPY ./cert/tls/vms.p12 /etc/ssl/certs/vms.p12
#COPY ./cert/tls/cert.cer $JAVA_HOME/lib/security
## command to export cer from p12.
## import cer into java cacerts
#RUN cd $JAVA_HOME/lib/security \
#     && keytool -importcert -trustcacerts -cacerts -storepass changeit -noprompt -file cert.cer -alias VMS
# copy jar
COPY --from=builder /out/${JAR_FILE} ./app.jar
COPY --from=builder /out/${CONFIG_FILE} ./application.yml


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]