# ========================
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /workspace

# tzdata 설치 후 한국 시간대 설정
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && apk del tzdata

# 빌드된 JAR 파일 복사
COPY build/libs/*-SNAPSHOT.jar app.jar

# 권한 낮은 사용자 생성
RUN adduser -D -s /bin/sh spring
USER spring

EXPOSE 9898

# 단순 JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
