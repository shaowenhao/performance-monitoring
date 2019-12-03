from automation.core.components.api import call_api


class TestSearchDataApi(object):

    def test_get_without_name_param(self, testdata):
        call_api(testdata)

    def test_get_with_fields_name_forbidden(self, testdata):
        call_api(testdata)

    def test_get_with_correct_name_param(self, testdata):
        res = call_api(testdata)
        res_body = res.body.get('data', [])
        other_check_points = testdata['validate']['others']
        assert len(res_body) == other_check_points['items_no']
        for item in res_body:
            assert set(item.keys()).issubset(other_check_points['keys'])

    def test_get_with_condition_param(self, testdata):
        res = call_api(testdata)

    def test_get_with_fields_param(self, testdata):
        res = call_api(testdata)