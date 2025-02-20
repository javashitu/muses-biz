# Step : Test and package
FROM maven:3.8.1-openjdk-17-slim as builder
WORKDIR /build
COPY . /build

RUN mvn dependency:go-offline package -DskipTests=true #remove -DskipTests=true to run the tests while building image

# Step : Package image
FROM openjdk:17-slim
COPY --from=builder /build/application/target/*.jar /app/app.jar
EXPOSE 9090
#CMD exec java $JAVA_OPTS -jar /app/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/app.jar ${0} ${@}" ]