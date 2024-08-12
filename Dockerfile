FROM openjdk:17
EXPOSE 9090

ADD target/task-stream.jar task-stream.jar
ENTRYPOINT ["java","-jar","/task-stream.jar"]


