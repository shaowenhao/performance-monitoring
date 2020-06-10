import os
import re
import copy
from automation.core.components.api.apis import Apis
from automation.core.components.api.http_client import HttpClient
from automation.core import logger
from automation.config.config import Config

URL_REGEX_COMPILE = re.compile(r"\{(\w+)\}")

class ApiWrapper(object):

    def __init__(self, request, http_client):
        self.request_data = copy.deepcopy(request)
        self.method = self.request_data.pop('method', 'get').lower()
        api_name = self.request_data.pop('api')
        self.api_url = Apis.get_api(api_name)
        if 'sub_url' in self.request_data:
            sub_url = self.request_data.pop('sub_url')
            if URL_REGEX_COMPILE.search(self.api_url):
               if isinstance(sub_url, str):
                    self.api_url = URL_REGEX_COMPILE.sub(sub_url, self.api_url)
               else:
                   for surl in sub_url:
                       self.api_url = URL_REGEX_COMPILE.sub(surl, self.api_url, count=1)
            else:
                self.api_url = '/'.join((self.api_url, sub_url))

        self.http_client = http_client
        if not self.http_client:
            base_url = self._parse_url(api_name)
            self.http_client = HttpClient(base_url, verify=False)

    def _parse_url(self, name):
        base_url = Apis.get_base_api(name)
        if not base_url.startswith('http'):
            base_url = 'http://' + base_url
        return base_url

    def request(self):
        if 'files' in self.request_data:
            files = self.request_data.get('files', None)
            try:
                content = open(os.path.join(Config.ROOT_DIR, files[1]), 'rb')
            except Exception as e:
                logger.log_error("Failed to load file content {}".format(e))
                raise
            else:
                files[1] = content
                self.request_data['files'] = [tuple(files)]
        return self.http_client.request(self.method, self.api_url, **self.request_data)
