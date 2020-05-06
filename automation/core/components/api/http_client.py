import requests
import time
import validators
from automation.core.utils import formats, url_path_join
from automation.core.components.api.response import Response
from automation.core import logger


#: The maximum length of a response displayed in a debug message
DEBUG_MAX_TEXT_LENGTH = 100

class HttpClient(object):

    def __init__(self, base_url, headers=None, params=None,
                 debug=None, cache_lifetime=None, silent=None,
                 cache=None, delay=None, **kwargs):
        self.base_url = base_url
        self.cache = cache
        if not headers:
            headers = {}
        if not params:
            params = {}
        self.config = {
            'headers': headers,
            'params': params,
            'debug': debug,
            'cache_lifetime': cache_lifetime,
            'silent': silent,
            'delay': delay
        }
        self.defaults = kwargs
        self.session = requests.session()
        self._last_request_time = None

    def send_request(self, *args, **kwargs):
        """Wrapper for session.request
        Handle connection reset error even from pyopenssl
        """
        try:
            return self.session.request(*args, **kwargs)
        except ConnectionError:
            self.session.close()
            return self.session.request(*args, **kwargs)


    def request(self, method, path, headers=None, params=None, data=None,
                 debug=None, cache_lifetime=None, silent=True, ignore_cache=True,
                 format='json', delay=None, **kwargs):
        """Requests a path and returns response.
        """
        # build the request headers
        request_headers = self.config.get('headers', {})
        if headers is not None:
            request_headers.update(headers)

        # build the request params
        request_params = self.config.get('params', {})
        if params is not None:
            request_params.update(params)

        # extract request_format and response_format from format arguments
        if type(format) in (list, tuple) and len(format) == 2:
            request_format, response_format = format
        else:
            request_format = response_format = format

        # add the 'Content-Type' header and compose data, only when:
        #   1. the content is actually sent (whatever the HTTP verb is used)
        #   2. the format is provided ('json' by default)
        if request_format and (data is not None) and not isinstance(data, str):
            request_headers.setdefault(
                'Content-Type', formats.meta(request_format).get('content_type'))
            data = formats.compose(request_format, data)

        if validators.url(path):
            url = path
        else:
            url = url_path_join(self.base_url, path)
        # # log a debug message about the request
        # self._log(debug_messages['request'], debug, method=method.upper(),
        #           url=url, headers=request_headers, params=params, data=data)

        # # check if the response for this request is cached
        # cache_key = (url, str(params), str(headers))
        # if self.cache.has(cache_key) and not ignore_cache:
        #     item = self.cache.get(cache_key)
        #     self._log(debug_messages['cached_response'], debug, text=item)
        #     return bunchify(item)

        # delay the request if needed
        if not delay:
            delay = self.config['delay']
        if delay and delay > 0:
            t = time.time()
            if self._last_request_time is None:
                self._last_request_time = t

            elapsed = t - self._last_request_time
            if elapsed < delay:
                time.sleep(delay - elapsed)

        # use default request parameters
        for name, value in self.defaults.items():
            kwargs.setdefault(name, value)

        logger.log_info("".join([
                        "request details:\n"
                        "  method: {} \n".format(method),
                        "  url: {} \n".format(url),
                        "  params: {} \n".format(request_params),
                        "  headers: {} \n".format(request_headers),
                        "  data: {} \n".format(data),
                        "  kwargs: {} \n".format(kwargs)
        ]))
        # execute the request
        r = self.send_request(method, url, params=request_params,
                              headers=request_headers, data=data, **kwargs)
        self._last_request_time = time.time()

        # when not silent, raise an exception for any HTTP status code >= 400
        if not silent:
            r.raise_for_status()

        response = None

        try:
            # parse the response into something nice
            response = Response(r)
        except ValueError as e:
            logger.log_error("failed to parse response")
            # we've failed, raise this stuff when not silent
            if len(r.text) > DEBUG_MAX_TEXT_LENGTH:
                text = r.text[:DEBUG_MAX_TEXT_LENGTH] + '...'
            else:
                text = r.text
            logger.log_error(text)
            if silent:
                return None
            raise e

        # # cache the response if required
        # # only GET requests are cached
        # if cache_lifetime and cache_lifetime > 0 and method.lower() == 'get':
        #     self.cache.set(cache_key, parsed_response, cache_lifetime)


        # return our findings and try to make it a bit nicer
        return response


    def get(self, *parts, **options):
        """Executes a `GET` request on the currently formed URL."""
        return self.request('get', *parts, **options)

    def post(self, *parts, **options):
        """Executes a `POST` request on the currently formed URL."""
        return self.request('post', *parts, **options)

    def put(self, *parts, **options):
        """Executes a `PUT` request on the currently formed URL."""
        return self.request('put', *parts, **options)

    def patch(self, *parts, **options):
        """Executes a `PATCH` request on the currently formed URL."""
        return self.request('patch', *parts, **options)

    def delete(self, *parts, **options):
        """Executes a `DELETE` request on the currently formed URL."""
        return self.request('delete', *parts, **options)

    def head(self, *parts, **options):
        """Executes a `HEAD` request on the currently formed URL."""
        return self.request('head', *parts, **options)

    def __repr__(self):
        return "<{} for {}>".format(self.__class__.__name__, self.base_url)

# def urljoin(*args):
#     """
#     Joins given arguments into an url. Trailing but not leading slashes are
#     stripped for each argument.
#     """
#
#     return "/".join(map(lambda x: str(x).rstrip('/'), args))

if __name__ == '__main__':
    client = HttpClient('https://dog.ceo/api/breeds')
    resp = client.get('list/all')
    # print(url_path_join('https://dog.ceo/api/breeds', 'list/all'))
    # print(url_path_join('https://dog.ceo/api/breeds', '/list/all'))
    print(resp.__dict__)
    print('finish!')