FROM java:8-jre

ADD ./target/scenario_test-1.0.jar /app/
CMD ["java", "-Djava.rmi.server.hostname=10.141.212.148", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.rmi.port=12590", "-Dcom.sun.management.jmxremote.port=12590", "-Dcom.sun.management.jmxremote.ssl=false", "-Dcom.sun.management.jmxremote.authenticate=false", "-jar", "/app/scenario_test-1.0.jar"]

EXPOSE 16278