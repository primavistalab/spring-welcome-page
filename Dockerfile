FROM bellsoft/liberica-openjdk-debian:17.0.9
COPY target/welcome-app.jar /welcome-app.jar
CMD java \
    -Xmx64m \
    -jar /welcome-app.jar