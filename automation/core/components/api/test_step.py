from automation.core.components.api.api_wrapper import ApiWrapper
from automation.core.components.api.validator import Validator
from automation.core import logger


class TestStep(object):

    def __init__(self, step_data, http_client=None):
        self.request = step_data.get('request', {})
        self.validate = step_data.get('validate', {})
        self.import_data = step_data.get('import', {})
        self.export_data = step_data.get('export', {})
        self.api = ApiWrapper(self.request, http_client)

    def before_test(self):
        pass

    def test(self):
        # call api first
        resp = self.api.request()
        # validate response
        v = Validator()
        if not v.validate(self.validate, resp.to_check(**self._get_if_check_others())):
            raise AssertionError('validate result is False, test case failed!')
        try:
            if self.export_data:
                resp.extract_response(self.export_data)
        except Exception as e:
            logger.log_error('Failed to export data from response! error is {}'.format(e))
        return resp

    def _get_if_check_others(self):
        result = {
            'with_headers': False,
            'with_cookies': False
        }
        if 'headers' in self.validate.get('points'):
            result['with_headers'] = True
        if 'cookies' in self.validate.get('points'):
            result['with_cookies'] = True
        return result

    def after_test(self):
        pass