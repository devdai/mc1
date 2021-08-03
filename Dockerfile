FROM adoptopenjdk/openjdk11
COPY target/MC1-0.0.1-SNAPSHOT.jar MC1.jar

USER root
RUN apt-get update && apt-get install -y wget

USER root
# Add docker-compose-wait tool -------------------
ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

CMD ["java","-jar","/MC1.jar"]