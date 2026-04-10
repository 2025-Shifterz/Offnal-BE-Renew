FROM eclipse-temurin:17-jre-alpine

ENV TZ=Asia/Seoul

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && apk del tzdata

COPY build/libs/shifterz-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]

