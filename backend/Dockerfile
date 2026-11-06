FROM maven:3-amazoncorretto-25 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src/main ./src/main
RUN mvn clean package

FROM amazoncorretto:25 AS runner
WORKDIR /app
COPY --from=builder ./app/target/rs-0.0.1-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
