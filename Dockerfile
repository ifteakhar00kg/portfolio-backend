# Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app.jar"]