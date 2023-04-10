package com.siemens.devops.monitoring.utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

// WebDriverBaseClass这个类启动chrome driver
public class WebDriverBaseClass {

	public WebDriver driver;
	public static ThreadLocal<WebDriver> tdriver = new ThreadLocal<WebDriver>();

	public WebDriver initialize_driver() {

		// 当selenium升级到3.0之后，对不同的浏览器驱动进行了规范。如果想使用selenium驱动不同的浏览器，必须单独下载并设置不同的浏览器驱动。
		WebDriverManager.chromedriver().proxy("194.138.0.19:9400").setup();
//		System.setProperty("webdriver.chrome.driver", "d:\\tools\\chromedriver\\chromedriver.exe");
//		System.setProperty("webdriver.chrome.whitelistedIps", "");

		// set browser options
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("start-maximized");

		driver = new ChromeDriver(options);
		// 隐式等待10秒
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		tdriver.set(driver);
		return getDriver();
	}

	public static synchronized WebDriver getDriver() {
		return tdriver.get();
	}
}
