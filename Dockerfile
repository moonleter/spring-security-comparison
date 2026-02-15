FROM maven:3.9-eclipse-temurin-25-alpine AS builder
WORKDIR /usr/src/app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests=true

FROM eclipse-temurin:25-jre-alpine
WORKDIR /usr/src/app

COPY --from=builder /usr/src/app/target/*.jar app.jar

EXPOSE 8080

ARG PROFILE=default
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

ENTRYPOINT ["sh", "-c", "echo '----------------------' && \
           echo 'SPRING_PROFILES_ACTIVE='${SPRING_PROFILES_ACTIVE} && \
           echo '----------------------' && \
           java -jar app.jar"]