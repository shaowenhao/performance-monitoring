package com.siemens.datalayer.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Attachment;

public class CustomizedTestListener implements ITestListener {
	
	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}
	
	@Attachment
	public byte[] saveFailureScreenShot(WebDriver driver) {
		return ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
	}

	@Attachment(value = "{0}", type = "text/plain")
	public static String saveTextLog(String message) {
		return message;
	}
	
//	This method is invoked before any test method gets executed. This can be used to get the directory from where the tests are running.
	@Override
	public void onStart(ITestContext iTestContext) {
	    System.out.println("Ready to run all the tests in the suite");
	    iTestContext.setAttribute("WebDriver", WebDriverBaseClass.getDriver());
	}
	
//	This method is invoked after all tests methods gets executed. This can be used to store information of all the tests that were run.
	@Override
	public void onFinish(ITestContext iTestContext) {
		System.out.println("This is the end of test execution.");
	}
	
//	This method is invoked before a specific test method is invoked.
	@Override
	public void onTestStart(ITestResult iTestResult) {
		System.out.println("Start to run test: " + getTestMethodName(iTestResult) + " ...");
	}
	
//	This method is invoked when a specific test method succeeds. 
	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		System.out.println("Test method " + getTestMethodName(iTestResult) + " succeed.");
	}
	
//	This method is invoked when a specific test method fails. 
	@Override
	public void onTestFailure(ITestResult iTestResult) {
		System.out.println("Test method " + getTestMethodName(iTestResult) + " failed!");
		
		WebDriver driver = WebDriverBaseClass.getDriver();
		
		if (driver != null) {
			if (driver instanceof WebDriver) {
				System.out.println("Screenshot capture for test case: " + getTestMethodName(iTestResult));
				saveFailureScreenShot(driver);
			}
			saveTextLog("Screenshot is taken for failed test " + getTestMethodName(iTestResult));
		}
	}
	
//	This method is invoked when the execution of a specific test method is skipped. 
	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		System.out.println("Execution of test method " + getTestMethodName(iTestResult) + " is skipped");
	}
	
//	This method is invoked when the test method fails but its success percentage is within the mentioned ratio
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		System.out.println("Causion: Test method " + getTestMethodName(iTestResult) + " failed but its success rate is within the defined percentage");
	}
}
