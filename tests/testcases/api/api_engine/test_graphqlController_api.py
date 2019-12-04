from automation.core.components.api import call_api


class TestGraphqlControllerApi(object):

    # def test_get_schema(self, testdata):
    #     call_api(testdata)

    def test_search_data(self, testdata):
        res = call_api(testdata)
        res_body = res.body.get('data', [])
        other_check_points = testdata['validate']['others']
        res_body = res_body.get(other_check_points['entity_name'], [])
        assert len(res_body) == other_check_points['items_no']
        for item in res_body:
            assert set(item.keys()) == set(other_check_points['keys'])
