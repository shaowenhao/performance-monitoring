from automation.core.components.api import call_api


class TestJinzuApi(object):

    def test_get_projectList(self, testdata):
        call_api(testdata)

    # def test_get_projects(self, testdata):
    #     call_api(testdata)
