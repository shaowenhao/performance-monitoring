import os
import yaml
from automation.core import logger

class Config:
    XML_REPORT_PATH = './report/xml'
    XML_REPORT_REPO_PATH = '/allure-xml'
    HTML_REPORT_PATH = './report/html'
    LOG_LEVEL = 'INFO'
    LOG_FILE = './logs/automation.log'
    VALIDATE_TYPES = ['no_check', 'full_check', 'part_check', 'part_contain_check']
    ROOT_DIR = os.path.join(os.path.dirname(__file__), '../../')
    UI_SETTINGS_PATH = os.path.join(ROOT_DIR, 'settings.json')
    CONFIG_YML_PATH = os.path.join(os.path.dirname(__file__), 'configs.yml')

    @classmethod
    def load_configs(cls):
        with open(Config.CONFIG_YML_PATH, 'r') as stream:
            try:
                data = yaml.safe_load(stream)
            except yaml.YAMLError as exc:
                print(exc)
                logger.log_error("Failed to load config data from {} , exception is : \n {} \n".format(Config.CONFIG_YML_PATH, exc))
                raise
            else:
                return data

    @classmethod
    def save_configs(cls, data):
        with open(Config.CONFIG_YML_PATH, 'w') as outfile:
            try:
                yaml.dump(data, outfile, default_flow_style=False)
            except Exception as exc:
                print(exc)
                logger.log_error(
                    "Failed to save config data to {} , exception is : \n {} \n".format(Config.CONFIG_YML_PATH, exc))
                raise
            else:
                logger.log_info("current config file: \n {} ".format(data))
                logger.log_info("Save data success to config file {} \n".format(Config.CONFIG_YML_PATH))

    @classmethod
    def update_api_base(cls, connector=None, engine=None, eagle=None):
        configs = Config.load_configs()
        changed = False
        if connector:
            configs['apis']['connector']['base'] = connector
            logger.log_info("Set connector base to {} \n".format(connector))
            changed = True
        if engine:
            configs['apis']['entine']['base'] = engine
            logger.log_info("Set engine base to {} \n".format(engine))
            changed = True
        if eagle:
            configs['apis']['eagle']['base'] = eagle
            logger.log_info("Set eagle base to {} \n".format(eagle))
            changed = True
        if changed:
            Config.save_configs(configs)
