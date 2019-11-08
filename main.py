import os
import shutil
import subprocess
import pytest
from automation.config.config import Config
import automation.core.logger as logger

def invoke(cmd):
    proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    (result, error) = proc.communicate()

    if proc.returncode != 0:
        if not error:
            error = ''
        else:
            error = error.decode('utf-8')
        if not result:
            result = ''
        else:
            result = result.decode('utf-8')
        msg = 'Failed to run test: \n\nstderr: {}\nstdout: {}'.format(
            error, result)
        raise Exception(msg)
    o = result.decode("utf-8")
    return o


if __name__ == '__main__':
    logger.setup_logger(Config.LOG_LEVEL, Config.LOG_FILE)
    args = ['-s', '-q', '--alluredir', Config.XML_REPORT_PATH]
    pytest.main(args)
    if not os.path.exists(Config.HTML_REPORT_PATH):
        os.makedirs(Config.HTML_REPORT_PATH, 0o755, True)
    else:
        shutil.rmtree(Config.HTML_REPORT_PATH)
        os.mkdir(Config.HTML_REPORT_PATH)
    # cmd = ['allure', 'generate', Config.XML_REPORT_PATH, '-o', Config.HTML_REPORT_PATH]
    # print('ddddddd')
    # msg = invoke(cmd)
    # print('ssssss')
    # print(msg)
    print('Finished!')
