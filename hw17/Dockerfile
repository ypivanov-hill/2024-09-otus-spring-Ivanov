FROM maven:3.8.5-openjdk-17 AS dependencies

WORKDIR /opt/app/build
COPY pom.xml .

RUN mvn -B -e org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline -DexcludeArtifactIds=domain

FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /opt/app/build
COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /opt/app/build /opt/app/build
COPY src /opt/app/build/src

RUN mvn -B -e clean install -DskipTests

FROM bellsoft/liberica-openjdk-alpine-musl:17.0.13

LABEL version="0.0.1"
LABEL description="hw17 Docker image"

COPY --from=builder /opt/app/build/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
