FROM maven:3.6.2-jdk-13 as build
COPY ./pom.xml ./pom.xml
RUN mvn verify --fail-never
# Copy rest of the project as separate layer
COPY . .
RUN mvn package

FROM bellsoft/liberica-openjdk-alpine:13 as run
WORKDIR /opt/i2c-service
COPY --from=build /target/home-i2c-service-0.0.1.jar ./home-i2c-service-0.0.1.jar
RUN sed -i "s/v[0-9.]*\/community/edge\/community/" /etc/apk/repositories \
    && apk update && apk add i2c-tools

RUN chmod 0764 /opt/i2c-service/home-i2c-service-0.0.1.jar

CMD ["java", "-jar", "/opt/i2c-service/home-i2c-service-0.0.1.jar"]
#CMD ["sh", "/opt/i2c-service/run.sh"]