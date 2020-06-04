from automation.core.components.api import call_api


class TestEagleApi(object):


    def test_load_graphs_api(self, testdata):
        res = call_api(testdata)
        # res_body = res.body.get('data', [])