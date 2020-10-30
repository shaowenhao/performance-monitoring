package com.siemens.datalayer.selenium.test;

import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.CustomizedTestListener;
import com.siemens.datalayer.utils.WebDriverBaseClass;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;

//@Listeners({TestngListener.class})
@Listeners({CustomizedTestListener.class})
public class SampleTest2 extends WebDriverBaseClass {
	
	public WebDriver driver;
	
	@BeforeClass
	public void setup()
	{
		WebDriverBaseClass bs = new WebDriverBaseClass();
		driver = bs.initialize_driver();
		driver.get("http://localhost:4500/#/overview");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		
		Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
		AllureEnvironmentPropertiesWriter.addEnvironmentItem("Web Browser", caps.getBrowserName() + "driver " + caps.getVersion());
	}

	
	@Test(description="Verify the user login function")
	@Description("Verify user login process")
	@Epic("EP001 Home Page Layout")
	@Feature("Feature2: Login")
	@Step("Check the Login function with incorrect password")
	@Severity(SeverityLevel.CRITICAL)
	@Link(name = "JIRA task", url = "https://agile.siemens.net/browse/SDL-2209", type = "tms")
	@TmsLink("test-case-123")
	@Issue("2209")
	public void loginTest() throws InterruptedException
	{
		
		int sum = driver.findElements(By.xpath("//span[@class='stat-value']")).size();
		Assert.assertEquals(sum, 3);
		String text = driver.findElement(By.xpath("//span[@class='stat-value']")).getText();
		Assert.assertEquals(text, "6");
		
		List<WebElement> seen = driver.findElements(By.xpath("//span[@class='stat-value']"));
		 
	    List<String> ids = seen.stream().map(e -> e.getText()).collect(Collectors.toList());
		driver.findElement(By.xpath("//span[contains(.,'西门子电气(无锡)有限公司 (3台)')]")).click();
		driver.findElement(By.xpath("//span[contains(.,'陕西杭铣机电制造厂 (2台)')]")).click();
		WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span")));
		List<WebElement> seen2 = driver.findElements(By.cssSelector("span"));
		for(WebElement w : seen2) {
			System.out.println(w.getAttribute("title"));
		}
		
	}
	
	@AfterClass
	public void tearDown()
	{
		if (driver != null) driver.quit();
	}
}
