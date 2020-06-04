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

def parse_args(name, args, remain_args):
    result = None
    if name in args:
        result = args[args.index(name) + 1]
        for item in [name, result]:
            remain_args.remove(item)
    return result

def main(*args):
    # try:
    #     opts, aargs = getopt.getopt(args, "he:c:", ["engine=", "connector="])
    # except getopt.GetoptError:
    #     print('main.py -e <engine host> -c <connect host>')
    #     sys.exit(2)
    engine = None
    connector = None
    eagle = None

    other_args = list(args)

    try:
        engine = parse_args('-e', args, other_args)
        connector = parse_args('-c', args, other_args)
        eagle = parse_args('-m', args, other_args)
    except Exception as e:
        print(e)
        print('Usage: python main.py -a <engine host> -c <connect host> -m <eagle host> -k <match case>')
        raise
    else:
        Config.update_api_base(connector=connector, engine=engine, eagle=eagle)

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
    # import requests
    #
    # url = "http://140.231.89.85:31294/api/graphs"
    #
    # payload = {'partitionName': 'ontology'}
    # files = [
    #     ('graph',
    #      open('./SP5_CIM_EXPO_Fusion_KG.xml',
    #           'rb'))
    # ]
    # headers = {
    #     'accept': '*/*',
    #     'Content-Type': 'multipart/form-data'
    # }
    #
    # response = requests.request("POST", url, headers=headers, data=payload, files=files)
    #
    # print(response.text.encode('utf8'))
