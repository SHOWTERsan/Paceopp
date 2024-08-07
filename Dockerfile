FROM openjdk:17
WORKDIR /app
COPY . /app
RUN chmod +x /app/mvnw
RUN ./mvnw package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./target/Paceopp-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
