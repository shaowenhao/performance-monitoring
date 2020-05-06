FROM 140.231.89.84:30003/ctsharp-datalayer/data-layer-automation:base
WORKDIR /app

# Install app dependencies
COPY requirements.txt ./

RUN pip3 --proxy http://194.145.60.1:9400 install -i https://pypi.tuna.tsinghua.edu.cn/simple --no-cache-dir  -r requirements.txt
ENV HTTP_PROXY=
ENV HTTPS_PROXY=
ENV http_proxy=
ENV https_proxy=
ENV no_proxy=
# Bundle app source
COPY . /app

ENTRYPOINT [ "python3", "main.py" ]