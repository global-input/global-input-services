FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD ./app /app
ENV JAVA_OPTS=""

WORKDIR /app

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar" ]

EXPOSE 8080