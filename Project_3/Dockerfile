FROM openjdk:8
COPY *.java /usr/src/M8B/
WORKDIR /usr/src/M8B/
RUN javac Magic8BallServer.java
EXPOSE 32000
CMD ["java", "Magic8BallServer", "32000"]