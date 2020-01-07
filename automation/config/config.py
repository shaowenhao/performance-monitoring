import os

class Config:
    XML_REPORT_PATH = './report/xml'
    HTML_REPORT_PATH = './report/html'
    LOG_LEVEL = 'INFO'
    LOG_FILE = './logs/automation.log'
    VALIDATE_TYPES = ['no_check', 'full_check', 'part_check', 'part_contain_check']
    UI_SETTINGS_PATH = os.path.join(os.path.dirname(__file__), '../../settings.json')