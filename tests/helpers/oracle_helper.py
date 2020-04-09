import yaml
import os
from automation.core import logger
from automation import config
from automation.core.components.database.oracle import Oracle

class OracleHelper(object):

    def __init__(self):
        path = os.path.join(os.path.dirname(config.__file__), 'configs.yml')
        oracle_info = self._load_oracle_info(path)
        self.db = Oracle(oracle_info['username'], oracle_info['password'], oracle_info['server'], oracle_info['port'], oracle_info['db'])

    def _load_oracle_info(cls, yml_path):
        with open(yml_path, 'r') as stream:
            try:
                data = yaml.safe_load(stream)
            except yaml.YAMLError as exc:
                print(exc)
                logger.log_error("Failed to load apis from {} , exception is : \n {} \n".format(yml_path, exc))
                raise
            else:
                info = data.get('oracle', {})
                return info

    def select(self, sql_string, **kwargs):
        return self.db.query_with_string(sql_string, *kwargs)


oracle_helper = OracleHelper()