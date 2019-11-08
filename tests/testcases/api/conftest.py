import pytest
import os
from automation.core import logger
from automation.core.components.api.apis import Apis
import yaml
# conftest.py

# https://docs.pytest.org/en/latest/example/parametrize.html
def pytest_generate_tests(metafunc):
    prepare_apis_if_need()
    if "testdata" in metafunc.fixturenames:
        path = metafunc.module.__file__
        method_name = metafunc.definition.name
        testdata = get_testdata(path).get(method_name, {})
        if not testdata:
            logger.log_warning('Notice!!! Not find testdata with name {} , please check if the name same with the method name'.format(method_name))
        metafunc.parametrize("testdata", testdata)


def get_testdata(path):
    testdata_path = path.replace("testcases", "testdata").replace(".py", ".yml")
    if testdata_path not in CACHES:
        with open(testdata_path, 'r') as stream:
            try:
                testdata = yaml.safe_load(stream)
            except yaml.YAMLError as exc:
                print(exc)
                logger.log_error("Failed to load {} , exception is : \n {} \n".format(testdata_path, exc))
                raise
            else:
                CACHES[testdata_path] = testdata
    return CACHES[testdata_path]

def prepare_apis_if_need():
    if not Apis.all_apis:
        Apis.load_default_apis()

CACHES = {}