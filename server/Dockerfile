FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
ADD /ext /ext
COPY build/libs/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]