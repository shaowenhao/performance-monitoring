import copy
from automation.core.components.api.apis import Apis
from automation.core.components.api.http_client import HttpClient

class ApiWrapper(object):

    def __init__(self, request, http_client):
        self.request = copy.deepcopy(request)
        self.method = self.request.pop('method', 'get').lower()
        self.api_url = Apis.get_api(self.request.pop('api'))
        self.http_client = http_client
        if self.http_client:
            base_url = Apis.get_api('base')
            self.http_client = HttpClient(base_url, verify=False)

    def request(self):
        return self.http_client.request(self.method, self.api_url, **self.request)
