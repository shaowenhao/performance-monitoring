import os
import sys, getopt
import shutil
from distutils.dir_util import copy_tree
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


def main(*args):
    # try:
    #     opts, aargs = getopt.getopt(args, "he:c:", ["engine=", "connector="])
    # except getopt.GetoptError:
    #     print('main.py -e <engine host> -c <connect host>')
    #     sys.exit(2)
    engine = None
    connector = None

    try:
        if not (set(['-e', '-c']) - set(args)):
            engine = args[args.index('-e') + 1]
            connector = args[args.index('-c') + 1]
        else:
            print('main.py -a <engine host> -c <connect host> -k <match case>')
            sys.exit()
    except Exception as e:
        print(e)
        raise
    else:
        Config.update_api_base(connector, engine)
        other_args = list(args)
        for item in ['-e', engine, '-c', connector]:
            other_args.remove(item)

    logger.setup_logger(Config.LOG_LEVEL, Config.LOG_FILE)
    default_args = ['-s', '-q', './tests', '--alluredir', Config.XML_REPORT_PATH]
    logger.log_info("start to run with default_args {} , other_args {}".format(default_args, other_args))
    pytest.main([*default_args, *other_args])

    if not os.path.exists(Config.HTML_REPORT_PATH):
        os.makedirs(Config.HTML_REPORT_PATH, 0o755, True)
    else:
        shutil.rmtree(Config.HTML_REPORT_PATH)
        os.mkdir(Config.HTML_REPORT_PATH)
    copy_tree(Config.XML_REPORT_PATH, Config.XML_REPORT_REPO_PATH)
    # cmd = ['allure', 'generate', Config.XML_REPORT_PATH, '-o', Config.HTML_REPORT_PATH]
    # print('ddddddd')
    # msg = invoke(cmd)
    # print('ssssss')
    # print(msg)
    print('Finished!')


if __name__ == '__main__':
    main(*sys.argv[1:])
