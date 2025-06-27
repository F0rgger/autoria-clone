FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/autoria-clone-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Можно передавать JAVA_OPTS для JVM через переменные окружения
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]