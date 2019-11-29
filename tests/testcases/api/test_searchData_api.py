from automation.core.components.api import call_api


class TestSearchDataApi(object):

    def test_get_without_name_param(self, testdata):
        call_api(testdata)

    def test_get_with_fields_name_forbidden(self, testdata):
        call_api(testdata)
