# ---- 1단계: Build Stage ----
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 소스 복사
COPY . .

# 테스트는 건너뛰고 빌드만 수행
RUN gradle clean build -x test

# ---- 2단계: Run Stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# 위에서 만든 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션이 사용하는 포트
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
