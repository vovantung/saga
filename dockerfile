FROM eclipse-temurin:17-jdk-alpine
COPY main-app/target/saga.jar /saga.jar
ENTRYPOINT ["java","-jar","/saga.jar"]
EXPOSE 8080