package com.siemens.datalayer.selenium.test;

import com.siemens.datalayer.utils.CustomizedTestListener;
import com.siemens.datalayer.utils.WebDriverBaseClass;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;

//@Listeners({TestngListener.class})
@Listeners({CustomizedTestListener.class})
public class SampleTest extends WebDriverBaseClass {
	
	public WebDriver driver;
	
	@BeforeClass
	public void setup()
	{
		WebDriverBaseClass bs = new WebDriverBaseClass();
		driver = bs.initialize_driver();
		driver.get("https://demo.nopcommerce.com/");
		driver.manage().window().maximize();
	}
	
	@Test(description="Verify the presence of a Logo on homepage")
	@Description("Verify Logo presence on Home Page")
	@Epic("EP001 Home Page Layout")
	@Feature("Feature1: Logo")
	@Step("Check the Logo Presence")
	@Severity(SeverityLevel.MINOR)
	public void logoPresence()
	{
		driver.findElement(By.xpath("//div[@class='header-logo']//a//img"));
	}
	
	@Test(description="Verify the user login function")
	@Description("Verify user login process")
	@Epic("EP001 Home Page Layout")
	@Feature("Feature2: Login")
	@Step("Check the Login function with incorrect password")
	@Severity(SeverityLevel.CRITICAL)
	public void loginTest()
	{
		driver.findElement(By.linkText("Log in")).click();
		driver.findElement(By.id("Email")).sendKeys("pavanoltraining@gmail.com");
		driver.findElement(By.id("Password")).sendKeys("Test@123");
		driver.findElement(By.xpath("//input[@class='button-1 login-button']")).click();
		
		Assert.assertEquals(driver.getTitle(), "nopCommerce demo store. Login???");
	}
	
	@AfterClass
	public void tearDown()
	{
		if (driver != null) driver.quit();
	}
}
