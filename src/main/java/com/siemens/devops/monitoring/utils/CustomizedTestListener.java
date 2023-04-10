package com.siemens.devops.monitoring.utils;

import static org.testng.internal.Utils.isStringNotBlank;

import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.Utils;

import io.qameta.allure.Attachment;

public class CustomizedTestListener implements ITestListener {

	private static CaseResultsCollector caseResultsCollector = new CaseResultsCollector();

	public static CaseResultsCollector getCaseResultsCollector() {
		return caseResultsCollector;
	}

	private String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}

	@SuppressWarnings("unchecked")
	private String getCaseId(ITestResult iTestResult) {
		Object[] parameters = iTestResult.getParameters();
		return CasePropertyUtils.getCaseId((Map<String, String>) parameters[0]);
	}

	@Attachment
	public byte[] saveFailureScreenShot(WebDriver driver) {
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	}

	@Attachment(value = "{0}", type = "text/plain")
	public String saveTextLog(String message) {
		return message;
	}

//	This method is invoked before any test method gets executed. This can be used to get the directory from where the tests are running.
	@Override
	public void onStart(ITestContext iTestContext) {
		System.out.println("Ready to run all the tests in the suite");
		AllureEnvironmentPropertiesWriter.initializeTestSettings();
//	    iTestContext.setAttribute("WebDriver", WebDriverBaseClass.getDriver());
	}

//	This method is invoked after all tests methods gets executed. This can be used to store information of all the tests that were run.
	@Override
	public void onFinish(ITestContext iTestContext) {
		System.out.println("This is the end of test execution.");
		AllureEnvironmentPropertiesWriter.writeTestSettings();

		logResults(iTestContext);
	}

	// This method is invoked before a specific test method is invoked.
	@Override
	public void onTestStart(ITestResult iTestResult) {
		System.out.println(
				"Run case [caseId=" + getCaseId(iTestResult) + "] with method " + getTestMethodName(iTestResult));
	}

//	This method is invoked when a specific test method succeeds. 
	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		System.out.println("Case [caseId=" + getCaseId(iTestResult) + "] with method " + getTestMethodName(iTestResult)
				+ " is passed!");
	}

//	This method is invoked when a specific test method fails. 
	@Override
	public void onTestFailure(ITestResult iTestResult) {
		System.out.println("Case [caseId=" + getCaseId(iTestResult) + "] with method " + getTestMethodName(iTestResult)
				+ " is failed!");

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
		System.out.println("Case [caseId=" + getCaseId(iTestResult) + "] with method " + getTestMethodName(iTestResult)
				+ " is skipped!");
	}

//	This method is invoked when the test method fails but its success percentage is within the mentioned ratio
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		System.out.println("Causion: Case [caseId=" + getCaseId(iTestResult) + "] with method "
				+ getTestMethodName(iTestResult) + " failed but its success rate is within the defined percentage");
	}

	private void logResults(ITestContext iTestContext) {
		for (ITestResult tr : iTestContext.getFailedTests().getAllResults()) {
			Throwable ex = tr.getThrowable();
			String stackTrace = "";
			stackTrace = Utils.shortStackTrace(ex, false);

			logResult(CaseResultsCollector.STR_FAILED, tr, stackTrace);
		}

		for (ITestResult tr : iTestContext.getSkippedTests().getAllResults()) {
			Throwable throwable = tr.getThrowable();
			logResult(CaseResultsCollector.STR_SKIPPED, tr,
					throwable != null ? Utils.shortStackTrace(throwable, false) : null);
		}

		int failedTestCount = iTestContext.getFailedTests().getAllResults().size();
		int passedTestCount = iTestContext.getPassedTests().getAllResults().size();
		int skippedTestCount = iTestContext.getSkippedTests().getAllResults().size();

		StringBuilder logBuf = new StringBuilder();
		logBuf.append("    Passed: ").append(String.valueOf(passedTestCount)).append(", Failed: ")
				.append(failedTestCount).append(", Skipped: ").append(skippedTestCount);
		logResult(CaseResultsCollector.STR_SUMMARY, logBuf.toString());

	}

	private void logResult(String status, ITestResult tr, String stackTrace) {
		logResult(status, tr.getName(), tr.getMethod().getDescription(), stackTrace, tr.getParameters(),
				tr.getMethod().getConstructorOrMethod().getParameterTypes());
	}

	private void logResult(String status, String name, String description, String stackTrace, Object[] params,
			Class<?>[] paramTypes) {
		StringBuilder msg = new StringBuilder(name);

		if (null != params && params.length > 0) {
			msg.append("(");

			// The error might be a data provider parameter mismatch, so make
			// a special case here
			if (params.length != paramTypes.length) {
				msg.append(name).append(": Wrong number of arguments were passed by ")
						.append("the Data Provider: found ").append(params.length).append(" but ").append("expected ")
						.append(paramTypes.length).append(")");
			} else {
				for (int i = 0; i < params.length; i++) {
					if (i > 0) {
						msg.append(", ");
					}
					msg.append(Utils.toString(params[i], paramTypes[i]));
				}

				msg.append(")");
			}
		}
		if (!Utils.isStringEmpty(description)) {
			msg.append("\n");
			for (int i = 0; i < status.length() + 2; i++) {
				msg.append(" ");
			}
			msg.append(description);
		}
		if (!Utils.isStringEmpty(stackTrace)) {
			msg.append("\n").append(stackTrace);
		}

		logResult(status, msg.toString());
	}

	private void logResult(String status, String message) {
		if (!CaseResultsCollector.STR_SUMMARY.equalsIgnoreCase(status)) {
			StringBuilder buf = new StringBuilder();
			buf.append(status).append(": ");
			buf.append(message);
			caseResultsCollector.add(status, buf.toString());
		} else {
			caseResultsCollector.setSummary(message);
		}

	}
}
