import json
from automation.core.utils import formats
from automation.core import logger

class Response(object):

    def __init__(self, request_response, response_format='json'):
        self.raw_response = request_response
        self.status_code = request_response.status_code

        has_body = len(request_response.text) > 0

        if not has_body:
            self.body = {}
        else:
            try:
                self.body = formats.parse(response_format, request_response.text)
            except:
                logger.log_error('Failed to parse response text! return it directly')
                self.body = request_response.text

        self.headers = request_response.headers
        self.cookies = request_response.cookies
        self.total_seconds = request_response.elapsed.total_seconds()

    def to_check(self, with_headers=False, with_cookies=False):
        check = {
            'status_code': self.status_code,
            'body': self.body
        }
        if with_headers:
            check['headers'] = self.headers
        if with_cookies:
            check['cookies'] = self.cookies
        return check

