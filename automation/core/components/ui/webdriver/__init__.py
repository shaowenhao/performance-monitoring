from selenium.webdriver import Chrome as SeleniumChromeDriver
from selenium.webdriver import Edge as SeleniumEdgeDriver
from selenium.webdriver import Firefox as SeleniumGeckoDriver
from selenium.webdriver import Ie as SeleniumIeDriver
from selenium.webdriver import Opera as SeleniumOperaDriver
from selenium.webdriver import Remote as SeleniumRemoteDriver

from automation.core.components.ui.webdriver.extended_driver import ExtendedDriver


class ChromeDriver(SeleniumChromeDriver, ExtendedDriver):
    pass


class EdgeDriver(SeleniumEdgeDriver, ExtendedDriver):
    pass


class GeckoDriver(SeleniumGeckoDriver, ExtendedDriver):
    pass


class IeDriver(SeleniumIeDriver, ExtendedDriver):
    pass


class OperaDriver(SeleniumOperaDriver, ExtendedDriver):
    pass


class RemoteDriver(SeleniumRemoteDriver, ExtendedDriver):
    pass
