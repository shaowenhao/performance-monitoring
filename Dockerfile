FROM binarycat/cx_oracle:6
ENV HTTP_PROXY http://194.145.60.1:9400
ENV HTTPS_PROXY http://194.145.60.1:9400
ENV http_proxy http://194.145.60.1:9400
ENV https_proxy http://194.145.60.1:9400
RUN yum update -y && yum -y install yum-utils && yum -y groupinstall development && yum -y install https://centos7.iuscommunity.org/ius-release.rpm
RUN yum install -y python3-devel
RUN pip3 install cx-Oracle==5.3
WORKDIR /app

# Install app dependencies
COPY requirements.txt ./

RUN pip3 --proxy http://194.145.60.1:9400 install -i https://pypi.tuna.tsinghua.edu.cn/simple --no-cache-dir  -r requirements.txt

ENV no_proxy 127.0.0.1,/var/run/docker.sock,data-layer-*
# Bundle app source
COPY . /app

ENTRYPOINT [ "python3", "main.py" ]