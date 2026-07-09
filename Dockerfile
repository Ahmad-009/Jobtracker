FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace/jobtracker

COPY jobtracker/mvnw ./mvnw
COPY jobtracker/pom.xml ./pom.xml
COPY jobtracker/.mvn ./.mvn
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY jobtracker/src ./src
RUN ./mvnw -B -ntp package -DskipTests

FROM eclipse-temurin:21-jre
RUN groupadd -r app && useradd -r -g app app
WORKDIR /app

COPY --from=builder /workspace/jobtracker/target/*.jar app.jar
RUN chown app:app /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
