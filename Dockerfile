FROM eclipse-temurin:21-jdk-alpine
LABEL version="6.3.0"
COPY apigo-admin.jar /app.jar
ENV LANG="en_US.UTF-8"
EXPOSE 9377
# 和docker run传入的环境变量名保持大写一致
ENV DB_URL="" DB_USER="" DB_PWD=""
CMD ["java", "-jar", "/app.jar", "--spring.datasource.url=${DB_URL}", "--spring.datasource.username=${DB_USER}", "--spring.datasource.password=${DB_PWD}"]