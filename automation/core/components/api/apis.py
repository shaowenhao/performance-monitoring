import yaml
import os
from automation.core import logger
from automation.config.config import Config

class Apis(object):

    all_apis = {}

    @classmethod
    def load_default_apis(cls):
        Apis.all_apis = Config.load_configs().get('apis', {})

    @classmethod
    def get_api(cls, name):
        api = None
        try:
            for _, item in Apis.all_apis.items():
                if name in item:
                    api = item[name]
                    break
        except Exception as e:
            logger.log_error("Failed to get api with name {}, exception is {}".format(name, e))
            raise
        if not api:
            raise ValueError("Can not find api with name {}, pls check if it defined".format(name))
        return api


    @classmethod
    def get_base_api(cls, name):
        base_api = None
        try:
            for _, item in Apis.all_apis.items():
                if name in item:
                    base_api = item['base']
                    break
        except Exception as e:
            logger.log_error("Failed to get base api with name {}, exception is {}".format(name, e))
            raise
        if not base_api:
            raise ValueError("Can not find base api with name {}, pls check if it defined")
        return base_api
