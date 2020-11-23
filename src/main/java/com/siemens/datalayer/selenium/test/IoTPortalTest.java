package com.siemens.datalayer.selenium.test;

import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.CustomizedTestListener;
import com.siemens.datalayer.utils.WebDriverBaseClass;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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
public class IoTPortalTest extends WebDriverBaseClass {
	
	public WebDriver driver;
	
	@BeforeClass
	public void setup()
	{
		WebDriverBaseClass bs = new WebDriverBaseClass();
		driver = bs.initialize_driver();
		driver.get("http://140.231.89.85:32189");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		
		Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
		AllureEnvironmentPropertiesWriter.addEnvironmentItem("Web Browser", caps.getBrowserName() + "driver " + caps.getVersion());
	}
	
	public static void snapshot(TakesScreenshot drivername, String filename)
	  {
	      // this method will take screen shot ,require two parameters ,one is driver name, another is file name
	      
	    String currentPath = System.getProperty("user.dir"); //get current work folder
	    System.out.println(currentPath);
	    File scrFile = drivername.getScreenshotAs(OutputType.FILE);
	        // Now you can do whatever you need to do with it, for example copy somewhere
	        try {
	            System.out.println("save snapshot path is:"+currentPath+"/"+filename);
	            FileUtils.copyFile(scrFile, new File(currentPath+"\\snapshot\\"+filename));
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            System.out.println("Can't save screenshot");
	            e.printStackTrace();
	        } 
	        finally
	        {
	           
	            System.out.println("screen shot finished");
	        }
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
		Thread.sleep(10000);
		snapshot((TakesScreenshot)driver,"open_iot_portal.png");
		int sum = driver.findElements(By.xpath("//span[@class='stat-value']")).size();
		Assert.assertEquals(sum, 3);
		String text = driver.findElement(By.xpath("//span[@class='stat-value']")).getText();
		Assert.assertEquals(text, "6");
		List<WebElement> seen = driver.findElements(By.xpath("//span[@class='stat-value']"));
	    List<String> ids = seen.stream().map(e -> e.getText()).collect(Collectors.toList());
	    
		driver.findElement(By.xpath("//span[contains(.,'西门子电气(无锡)有限公司 (3台)')]")).click();
		Thread.sleep(10000);
		snapshot((TakesScreenshot)driver,"navigation.png");
		
		driver.findElement(By.xpath("//span[contains(.,'陕西杭铣机电制造厂 (2台)')]")).click();
		Thread.sleep(10000);
		snapshot((TakesScreenshot)driver,"select_navigation.png");
		
		WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span")));
		List<WebElement> seen2 = driver.findElements(By.cssSelector("span"));
		for(WebElement w : seen2) {
			System.out.println(w.getAttribute("title"));
		}
		
		driver.findElement(By.xpath("//span[contains(.,' 力矩电动机')]")).click();
		Thread.sleep(10000);
		snapshot((TakesScreenshot)driver,"click_product.png");
		
		driver.findElement(By.xpath("//nz-tree-node[2]/li/span[2]/span")).click();
		Thread.sleep(10000);
		snapshot((TakesScreenshot)driver,"check_product_detail.png");
		
		
	}
	
	@AfterClass
	public void tearDown()
	{
		if (driver != null) driver.quit();
	}
}
