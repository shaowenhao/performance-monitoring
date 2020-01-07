import os
import json
import traceback
from automation.config.config import Config

class Settings(object):
    def settings_path(self):
        return Config.UI_SETTINGS_PATH

    def get_global_settings(self):
        """Get global settings from test-directory folder as a dictionary"""
        settings = {}
        path = self.settings_path()
        if os.path.isfile(path):
            settings = self._read_json_with_comments(path)
        else:
            print('Warning: settings file is not present')
        return settings


    def _read_json_with_comments(self, json_path):
        """Reads a file with '//' comments.
        Reads the file, removes the commented lines and return
        a json loads of the result.
        """
        file_lines = []
        with open(json_path) as json_file:
            file_lines = json_file.readlines()
        lines_without_comments = []
        for line in file_lines:
            if line.strip()[0:2] != '//' and len(line.strip()) > 0:
                lines_without_comments.append(line)
        file_content_without_comments = ''.join(lines_without_comments)
        json_data = {}
        try:
            json_data = json.loads(file_content_without_comments)
        except Exception:
            print('There was an error reading file {}'.format(json_path))
            print(traceback.format_exc())
        return json_data


def convert_file_path_to_abs(path):
    abs_path = path
    if not os.path.isabs(abs_path):
        abs_path = os.path.join(os.path.dirname(Settings().settings_path()), path)
    return abs_path


settings = Settings().get_global_settings()