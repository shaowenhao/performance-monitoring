from automation.core.components.api import call_api


class TestSearchModelSchemaByNameApi(object):

    def test_get_with_correct_name(self, testdata):
        call_api(testdata)

    def test_get_without_name_param(self, testdata):
        call_api(testdata)
