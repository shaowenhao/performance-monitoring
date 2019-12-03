import yaml
import os
from automation.core import logger
from automation import config

class Apis(object):

    all_apis = {}

    @classmethod
    def load_apis(cls, yml_path):
        with open(yml_path, 'r') as stream:
            try:
                apis = yaml.safe_load(stream)
            except yaml.YAMLError as exc:
                print(exc)
                logger.log_error("Failed to load apis from {} , exception is : \n {} \n".format(yml_path, exc))
                raise
            else:
                Apis.all_apis = apis.get('apis', {})

    @classmethod
    def load_default_apis(cls):
        path = os.path.join(os.path.dirname(config.__file__), 'apis.yml')
        Apis.load_apis(path)

    @classmethod
    def get_api(cls, name):
        api = None
        try:
            for item in Apis.all_apis:
                if name in item:
                    api = item[name]
                    break
        except Exception as e:
            logger.log_error("Failed to get api with name {}, exception is {}".format(name, e))
            raise
        if not api:
            raise ValueError("Can not find api with name {}, pls check if it defined")
        return api


    @classmethod
    def get_base_api(cls, name):
        base_api = None
        try:
            for item in Apis.all_apis:
                if name in item:
                    base_api = item['base']
                    break
        except Exception as e:
            logger.log_error("Failed to get base api with name {}, exception is {}".format(name, e))
            raise
        if not base_api:
            raise ValueError("Can not find base api with name {}, pls check if it defined")
        return base_api
