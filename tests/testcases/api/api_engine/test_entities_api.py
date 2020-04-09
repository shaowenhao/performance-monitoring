from automation.core.components.api import call_api
from automation.core.utils import query_json
from tests.helpers.oracle_helper import oracle_helper


class TestEntitiesApi(object):


    def test_entities_search_pagination(self, testdata):
        res = call_api(testdata)
        # res_body = res.body.get('data', [])
        # expect_total_count = oracle_helper.select(testdata['validate']['others']['sql'])
        # other_check_points = testdata['validate']['others']
        # res_body = res_body.get(other_check_points['entity_name'], [])
        # assert len(res_body) == other_check_points['items_no']
        # for item in res_body:
        #     assert set(item.keys()) == set(other_check_points['keys'])


    def test_entities_search_filter(self, testdata):
        res = call_api(testdata)
        content = query_json(res.body, testdata['validate']['others']['json_path'])
        assert len(content) == testdata['validate']['others']['content_length']
        assert content[0].keys() == testdata['validate']['others']['key_values'].keys()


    def test_entities_search_filter_nagetive(self, testdata):
        res = call_api(testdata)