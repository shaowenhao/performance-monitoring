FROM openjdk:8
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG jarName

ENV JARNAME=${jarName}

COPY $JARNAME /opt/$JARNAME

CMD java -jar /opt/${JARNAME}

EXPOSE 8080
