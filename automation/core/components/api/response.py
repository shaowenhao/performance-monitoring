import json
import re
from collections import OrderedDict
import jsonpath
from automation.core.utils import formats, ensure_mapping_format, basestring
from automation.core import logger, exceptions, utils

text_extractor_regexp_compile = re.compile(r".*\(.*\).*")

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
                logger.log_info("print response body text: {}".format(self.body))
            except:
                logger.log_error('Failed to parse response text! return it directly')
                self.body = request_response.text

        self.headers = request_response.headers
        self.cookies = request_response.cookies
        self.total_seconds = request_response.elapsed.total_seconds()

    def __getattr__(self, key):
        try:
            if key == "json":
                value = self.raw_response.json()
            elif key == "cookies":
                value = self.raw_response.cookies.get_dict()
            else:
                value = getattr(self.raw_response, key)

            self.__dict__[key] = value
            return value
        except AttributeError:
            err_msg = "ResponseObject does not have attribute: {}".format(key)
            logger.log_error(err_msg)
            raise exceptions.ParamsError(err_msg)

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


    def extract_response(self, extractors):
        if not extractors:
            return {}

        logger.log_debug("start to extract from response object.")
        extracted_variables_mapping = OrderedDict()
        extract_binds_order_dict = ensure_mapping_format(extractors)

        for key, field in extract_binds_order_dict.items():
            extracted_variables_mapping[key] = self.extract_field(field)

        return extracted_variables_mapping

    def _extract_field_with_jsonpath(self, field):
        result = jsonpath.jsonpath(self.parsed_body(), field)
        if result:
            return result
        else:
            raise exceptions.ExtractFailure("\tjsonpath {} get nothing\n".format(field))

    def _extract_field_with_regex(self, field):
        matched = re.search(field, self.text)
        if not matched:
            err_msg = u"Failed to extract data with regex! => {}\n".format(field)
            err_msg += u"response body: {}\n".format(self.text)
            logger.log_error(err_msg)
            raise exceptions.ExtractFailure(err_msg)

        return matched.group(1)

    def _extract_field_with_delimiter(self, field):
        try:
            top_query, sub_query = field.split('.', 1)
        except ValueError:
            top_query = field
            sub_query = None

        # status_code
        if top_query in ["status_code", "encoding", "ok", "reason", "url"]:
            if sub_query:
                # status_code.XX
                err_msg = u"Failed to extract: {}\n".format(field)
                logger.log_error(err_msg)
                raise exceptions.ParamsError(err_msg)

            return getattr(self, top_query)

        # cookies
        elif top_query == "cookies":
            cookies = self.cookies
            if not sub_query:
                # extract cookies
                return cookies

            try:
                return cookies[sub_query]
            except KeyError:
                err_msg = u"Failed to extract cookie! => {}\n".format(field)
                err_msg += u"response cookies: {}\n".format(cookies)
                logger.log_error(err_msg)
                raise exceptions.ExtractFailure(err_msg)

        # elapsed
        elif top_query == "elapsed":
            available_attributes = u"available attributes: days, seconds, microseconds, total_seconds"
            if not sub_query:
                err_msg = u"elapsed is datetime.timedelta instance, attribute should also be specified!\n"
                err_msg += available_attributes
                logger.log_error(err_msg)
                raise exceptions.ParamsError(err_msg)
            elif sub_query in ["days", "seconds", "microseconds"]:
                return getattr(self.raw_response.elapsed, sub_query)
            elif sub_query == "total_seconds":
                return self.raw_response.elapsed.total_seconds()
            else:
                err_msg = "{} is not valid datetime.timedelta attribute.\n".format(sub_query)
                err_msg += available_attributes
                logger.log_error(err_msg)
                raise exceptions.ParamsError(err_msg)

        # headers
        elif top_query == "headers":
            headers = self.headers
            if not sub_query:
                # extract headers
                return headers

            try:
                return headers[sub_query]
            except KeyError:
                err_msg = u"Failed to extract header! => {}\n".format(field)
                err_msg += u"response headers: {}\n".format(headers)
                logger.log_error(err_msg)
                raise exceptions.ExtractFailure(err_msg)

        # response body
        elif top_query in ["content", "text", "json", "body"]:
            try:
                body = self.json
            except exceptions.JSONDecodeError:
                body = self.text

            if not sub_query:
                # extract response body
                return body

            if isinstance(body, (dict, list)):
                # content = {"xxx": 123}, content.xxx
                return utils.query_json(body, sub_query)
            elif sub_query.isdigit():
                # content = "abcdefg", content.3 => d
                return utils.query_json(body, sub_query)
            else:
                # content = "<html>abcdefg</html>", content.xxx
                err_msg = u"Failed to extract attribute from response body! => {}\n".format(field)
                err_msg += u"response body: {}\n".format(body)
                logger.log_error(err_msg)
                raise exceptions.ExtractFailure(err_msg)

        # new set response attributes in teardown_hooks
        elif top_query in self.__dict__:
            attributes = self.__dict__[top_query]

            if not sub_query:
                # extract response attributes
                return attributes

            if isinstance(attributes, (dict, list)):
                # attributes = {"xxx": 123}, content.xxx
                return utils.query_json(attributes, sub_query)
            elif sub_query.isdigit():
                # attributes = "abcdefg", attributes.3 => d
                return utils.query_json(attributes, sub_query)
            else:
                # content = "attributes.new_attribute_not_exist"
                err_msg = u"Failed to extract cumstom set attribute from teardown hooks! => {}\n".format(field)
                err_msg += u"response set attributes: {}\n".format(attributes)
                logger.log_error(err_msg)
                raise exceptions.TeardownHooksFailure(err_msg)

        # others
        else:
            err_msg = u"Failed to extract attribute from response! => {}\n".format(field)
            err_msg += u"available response attributes: status_code, cookies, total_seconds, headers, body, content, text, json, encoding, ok, reason, url.\n\n"
            err_msg += u"If you want to set attribute in teardown_hooks, take the following example as reference:\n"
            err_msg += u"response.new_attribute = 'new_attribute_value'\n"
            logger.log_error(err_msg)
            raise exceptions.ParamsError(err_msg)

    def extract_field(self, field):
        """ extract value from requests.Response.
        """
        if not isinstance(field, basestring):
            err_msg = u"Invalid extractor! => {}\n".format(field)
            logger.log_error(err_msg)
            raise exceptions.ParamsError(err_msg)

        msg = "extract: {}".format(field)

        if field.startswith("$"):
            value = self._extract_field_with_jsonpath(field)
        elif text_extractor_regexp_compile.match(field):
            value = self._extract_field_with_regex(field)
        else:
            value = self._extract_field_with_delimiter(field)

        msg += "\t=> {}".format(value)
        logger.log_debug(msg)

        return value
