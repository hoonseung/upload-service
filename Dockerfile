#요약
 #
 #멀티 스테이지 빌드 + Layered JAR를 활용한 최적화 Dockerfile
 #
 #계층별 복사로 빌드 속도 향상
 #
 #Spring Boot JarLauncher 사용으로 계층별 캐싱 가능
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} upload-service.jar

# jvm 시스템 프로퍼티 layertools 설정, layertools 모드에서 실행하여 extract 적용
# jar를 레이어 별로 분해해서 copy 하여 효율적으로 캐싱 그래서 소스 변경 후 재 빌드 시 속도 빠름
RUN java -Djarmode=layertools -jar upload-service.jar extract

FROM eclipse-temurin:21-jdk-alpine
RUN adduser -D -s /bin/sh spring
USER spring

WORKDIR /workspace
COPY --from=builder workspace/dependencies/.  ./
COPY --from=builder workspace/spring-boot-loader/.  ./
COPY --from=builder workspace/snapshot-dependencies/.  ./
COPY --from=builder workspace/application/.  ./

EXPOSE 9898
ENTRYPOINT ["org.springframwork.boot.loader.JarLauncher"]