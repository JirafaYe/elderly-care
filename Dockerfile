FROM openjdk:8-alpine
VOLUME ["/tmp","/log"]
COPY ./elderly-care-code/target/elderly-care-code-1.0-SNAPSHOT.jar elderly-care-code-1.0-SNAPSHOT.jar
EXPOSE 8638
ENTRYPOINT ["java","-jar","-Xmx400m","./elderly-care-code-1.0-SNAPSHOT.jar","&"]
