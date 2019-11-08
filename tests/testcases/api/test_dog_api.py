import requests
import pytest
import yaml
import os
from automation.core.components.api import call_api

# @pytest.fixture
# def get_testdata():
#     testdata_path = os.path.realpath(__file__).replace("testcases", "testdata").replace(".py", ".yml")
#     with open(testdata_path, 'r') as stream:
#         try:
#             testdata = yaml.safe_load(stream)
#         except yaml.YAMLError as exc:
#             print(exc)
#         else:
#             return testdata['get_all_breeds']
#
#
# @pytest.fixture
# def apis():
#     with open('../../../config/apis.yml', 'r') as stream:
#         try:
#             apis = yaml.safe_load(stream)
#         except yaml.YAMLError as exc:
#             print(exc)
#         else:
#             return apis['apis']

class TestDogApi(object):

    def test_list_all_breeds(self, testdata):
        call_api(testdata)
