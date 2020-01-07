"""Functions to interact with a webdriver browser object."""
import os
import traceback
from contextlib import contextmanager

from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities

from automation.core import logger
from automation.core.components.ui import execution
from automation.core.components.ui.settings import settings, convert_file_path_to_abs
from automation.core.components.ui.webdriver import (ChromeDriver,
                             EdgeDriver,
                             GeckoDriver,
                             IeDriver,
                             OperaDriver,
                             RemoteDriver)

class InvalidBrowserIdError(Exception):
    pass


def element(*args, **kwargs):
    """Shortcut to golem.browser.get_browser().find()"""
    webelement = get_browser().find(*args, **kwargs)
    return webelement


def elements(*args, **kwargs):
    """Shortcut to golem.browser.get_browser().find_all()"""
    webelement = get_browser().find_all(*args, **kwargs)
    return webelement


def get_browser_definition():
    if not execution.browser_definition:
       execution.browser_definition = define_browsers([settings['default_browser']], get_supported_browsers_suggestions())
    return execution.browser_definition


def open_browser(browser_id=None):
    """Open a browser.

    When opening more than one browser instance per test
    provide a browser_id to switch between browsers later on

    :Raises:
      - InvalidBrowserIdError: The browser Id is already in use

    :Returns:
      the opened browser
    """
    @contextmanager
    def validate_exec_path(browser_name, exec_path_setting, settings):
        executable_path = convert_file_path_to_abs(settings[exec_path_setting])
        if executable_path:
            from automation.core.components.ui.utils import match_latest_executable_path
            matched_executable_path = match_latest_executable_path(executable_path)
            if matched_executable_path:
                try:
                    yield matched_executable_path
                except:
                    msg = ('Could not start {} driver using the path \'{}\'\n'
                           'verify that the {} setting points to a valid webdriver executable.'
                           .format(browser_name, executable_path, exec_path_setting))
                    logger.log_error(msg)
                    logger.log_info(traceback.format_exc())
                    raise Exception(msg)
            else:
                msg = 'No executable file found using path {}'.format(executable_path)
                logger.log_error(msg)
                raise Exception(msg)
        else:
            msg = '{} setting is not defined'.format(exec_path_setting)
            logger.log_error(msg)
            raise Exception(msg)

    @contextmanager
    def validate_remote_url(remote_url):
        if remote_url:
            yield remote_url
        else:
            msg = 'remote_url setting is required'
            logger.log_error(msg)
            raise Exception(msg)

    driver = None

    if not browser_id:
        if len(execution.browsers) == 0:
            browser_id = 'main'
        else:
            browser_id = 'browser{}'.format(len(execution.browsers))
    if browser_id in execution.browsers:
        raise InvalidBrowserIdError("browser id '{}' is already in use".format(browser_id))

    browser_definition = get_browser_definition()[0]
    # remote
    if browser_definition['remote']:
        with validate_remote_url(settings['remote_url']) as remote_url:
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=browser_definition['capabilities'])
    # Chrome
    elif browser_definition['name'] == 'chrome':
        with validate_exec_path('chrome', 'chromedriver_path', settings) as ex_path:
            chrome_options = webdriver.ChromeOptions()
            if settings.get('start_maximized', None):
                chrome_options.add_argument('start-maximized')
            driver = ChromeDriver(executable_path=ex_path,
                                       chrome_options=chrome_options)
    # Chrome headless
    elif browser_definition['name'] == 'chrome-headless':
        with validate_exec_path('chrome', 'chromedriver_path', settings) as ex_path:
            chrome_options = webdriver.ChromeOptions()
            chrome_options.add_argument('headless')
            chrome_options.add_argument('--window-size=1600,1600')
            driver = ChromeDriver(executable_path=ex_path,
                                       chrome_options=chrome_options)
    # Chrome remote
    elif browser_definition['name'] == 'chrome-remote':
        with validate_remote_url(settings['remote_url']) as remote_url:
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=DesiredCapabilities.CHROME)
    # Chrome remote headless
    elif browser_definition['name'] == 'chrome-remote-headless':
        with validate_remote_url(settings['remote_url']) as remote_url:
            chrome_options = webdriver.ChromeOptions()
            chrome_options.add_argument('headless')
            desired_capabilities = chrome_options.to_capabilities()
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=desired_capabilities)
    # Edge
    elif browser_definition['name'] == 'edge':
        with validate_exec_path('edge', 'edgedriver_path', settings) as ex_path:
            driver = EdgeDriver(executable_path=ex_path)
    # Edge remote
    elif browser_definition['name'] == 'edge-remote':
        with validate_remote_url(settings['remote_url']) as remote_url:
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=DesiredCapabilities.EDGE)
    # Firefox
    elif browser_definition['name'] == 'firefox':
        with validate_exec_path('firefox', 'geckodriver_path', settings) as ex_path:
            driver = GeckoDriver(executable_path=ex_path)
    # Firefox headless
    elif browser_definition['name'] == 'firefox-headless':
        with validate_exec_path('firefox', 'geckodriver_path', settings) as ex_path:
            firefox_options = webdriver.FirefoxOptions()
            firefox_options.headless = True
            driver = GeckoDriver(executable_path=ex_path, firefox_options=firefox_options)
    # Firefox remote
    elif browser_definition['name'] == 'firefox-remote':
        with validate_remote_url(settings['remote_url']) as remote_url:
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=DesiredCapabilities.FIREFOX)
    # Firefox remote headless
    elif browser_definition['name'] == 'firefox-remote-headless':
        with validate_remote_url(settings['remote_url']) as remote_url:
            firefox_options = webdriver.FirefoxOptions()
            firefox_options.headless = True
            desired_capabilities = firefox_options.to_capabilities()
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=desired_capabilities)
    # IE
    elif browser_definition['name'] == 'ie':
        with validate_exec_path('internet explorer', 'iedriver_path', settings) as ex_path:
            driver = IeDriver(executable_path=ex_path)
    # IE remote
    elif browser_definition['name'] == 'ie-remote':
        with validate_remote_url(settings['remote_url']) as remote_url:
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=DesiredCapabilities.INTERNETEXPLORER)
    # Opera
    elif browser_definition['name'] == 'opera':
        with validate_exec_path('opera', 'operadriver_path', settings) as ex_path:
            opera_options = webdriver.ChromeOptions()
            if 'opera_binary_path' in settings:
                opera_options.binary_location = settings['opera_binary_path']
            driver = OperaDriver(executable_path=ex_path, options=opera_options)
    # Opera remote
    elif browser_definition['name'] == 'opera-remote':
        with validate_remote_url(settings['remote_url']) as remote_url:
            driver = RemoteDriver(command_executor=remote_url,
                                       desired_capabilities=DesiredCapabilities.OPERA)
    else:
        raise Exception('Error: {} is not a valid driver'.format(browser_definition['name']))

    if settings['start_maximized']:
        # currently there is no way to maximize chrome window on OSX (chromedriver 2.43), adding workaround
        # https://bugs.chromium.org/p/chromedriver/issues/detail?id=2389
        # https://bugs.chromium.org/p/chromedriver/issues/detail?id=2522
        # TODO: assess if this work-around is still needed when chromedriver 2.44 is released
        is_mac = 'mac' in driver.capabilities.get('platform', '').lower()
        if not ('chrome' in browser_definition['name'] and is_mac):
            driver.maximize_window()

    execution.browsers[browser_id] = driver
    # Set the new browser as the active browser
    execution.browser = driver
    return execution.browser


def get_browser() -> RemoteDriver:
    """Returns the active browser. Starts a new one if there is none."""
    if not execution.browser:
        open_browser()
    return execution.browser


def activate_browser(browser_id):
    """Activate a browser.
    Only needed when the test starts more than one browser instance.

    :Raises:
      - InvalidBrowserIdError: The browser Id does not correspond to an opened browser

    :Returns:
      the active browser
    """
    if browser_id not in execution.browsers:
        raise InvalidBrowserIdError("'{}' is not a valid browser id. Current browsers are: {}"
                                    .format(browser_id, ', '.join(execution.browsers.keys())))
    else:
        execution.browser = execution.browsers[browser_id]
    return execution.browser


def define_browsers(browsers, default_browsers, remote_browsers=None):
    """Generate the definitions for the browsers.

    A defined browser contains the following attributes:
      'name':         real name
      'full_name':    remote browser name defined by user in settings
      'remote':       boolean
      'capabilities': capabilities defined by remote_browsers setting
    """
    browsers_definition = []
    for browser in browsers:
        if remote_browsers and browser in remote_browsers:
            remote_browser = remote_browsers[browser]
            b = {
                'name': remote_browser['browserName'],
                'full_name': browser,
                'remote': True,
                'capabilities': remote_browser
            }
            browsers_definition.append(b)
        elif browser in default_browsers:
            b = {
                'name': browser,
                'full_name': None,
                'remote': False,
                'capabilities': {}
            }
            browsers_definition.append(b)
        else:
            msg = ['Error: the browser {} is not defined\n'.format(browser),
                   'available options are:\n',
                   '\n'.join(default_browsers),
                   '\n'.join(remote_browsers)]
            raise Exception(''.join(msg))
    return browsers_definition


def get_supported_browsers_suggestions():
    """Return a list of supported browsers by default."""
    supported_browsers = [
        'chrome',
        'chrome-remote',
        'chrome-headless',
        'chrome-remote-headless',
        'edge',
        'edge-remote',
        'firefox',
        'firefox-headless',
        'firefox-remote',
        'firefox-remote-headless',
        'ie',
        'ie-remote',
        'opera',
        'opera-remote',
    ]
    return supported_browsers