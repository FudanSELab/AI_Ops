FROM java:8-jre


ADD ./target/rest-service-collect-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/rest-service-collect-1.0.jar"]

EXPOSE 9413