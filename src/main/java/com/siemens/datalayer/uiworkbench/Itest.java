package com.siemens.datalayer.uiworkbench;

import com.siemens.datalayer.utils.WebDriverBaseClass;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Itest extends WebDriverBaseClass {
    public WebDriver driver;

    @BeforeClass
    public void setUp()
    {
        WebDriverBaseClass bs = new WebDriverBaseClass();
        driver = bs.initialize_driver();
        driver.get("https://www.selenium.dev/documentation/");
        driver.manage().window().maximize();

        Capabilities cap =  ((RemoteWebDriver) driver).getCapabilities();
    }

    @Test(description = "")
    public void test(){
        System.out.println(driver.getTitle());
    }

    @AfterClass
    public void tearDown()
    {
        if (driver != null) driver.quit();
    }
}
