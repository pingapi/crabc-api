FROM openjdk:8-jdk-alpine
MAINTAINER crabc <creabc@qq.com>

ADD crabc-admin.jar /app.jar
ENV LANG="en_US.UTF-8"
EXPOSE 9377
ENV db_url ${db_url}
ENV db_user ${db_user}
ENV db_pwd ${db_pwd}

CMD java -jar /app.jar --spring.datasource.url=${db_url}  --spring.datasource.username=${db_user}  --spring.datasource.password=${db_pwd}