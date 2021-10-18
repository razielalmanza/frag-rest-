FROM openjdk:12
VOLUME /tmp
ADD target/frag-rest-0.0.1-SNAPSHOT.jar frag-rest.jar
RUN sh -c 'touch /frag-rest.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/frag-rest.jar"]
ENV TZ America/Mexico_City
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone