from automation.core.components.api import call_api


class TestEagleApi(object):


    def test_load_graphs_api(self, testdata):
        call_api(testdata)

    def test_ontologies_api(self, testdata):
        call_api(testdata)

    def test_projects_api(self, testdata):
        call_api(testdata)

    def test_entities_api(self, testdata):
        call_api(testdata)

    def test_relations_api(self, testdata):
        call_api(testdata)