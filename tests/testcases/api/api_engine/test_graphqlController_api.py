from automation.core.components.api import call_api


class TestGraphqlControllerApi(object):

    def test_get_schema(self, testdata):
        call_api(testdata)
