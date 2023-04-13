package com.siemens.devops.monitoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.siemens.devops.monitoring.utils.Alert;
import com.siemens.devops.monitoring.utils.CustomizedTestListener;
import com.siemens.devops.monitoring.utils.EmailAlert;
import com.siemens.devops.monitoring.utils.TeamsAlert;

import cn.hutool.core.util.EnumUtil;

public class Main {

	private String[] originalArgs;
	private Configuration configuration;

	public static void main(String[] args) {
		Main main = new Main();
		main.processArgsAndExecution(args);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	private Main() {
		configuration = new Configuration();
	}

	private void processArgsAndExecution(String[] args) {
		originalArgs = args;
		for (int i = 0; i < originalArgs.length; i++) {
			String arg = originalArgs[i];
			if ("-noTest".equalsIgnoreCase(arg)) {
				configuration.setRunRegressionTest(false);
			} else if ("-noXML".equalsIgnoreCase(arg)) {
				configuration.setGenerateTestNGXML(false);
			} else if ("-projectName".equalsIgnoreCase(arg)) {
				configuration.setProjectName(originalArgs[++i].toLowerCase());
			} else if ("-envName".equalsIgnoreCase(arg)) {
				configuration.setEnvName(originalArgs[++i].toLowerCase());
			} else if ("-enableAlert".equalsIgnoreCase(arg)) {
				configuration.setEnableAlert(true);
			} else if ("-disableAlert".equalsIgnoreCase(arg)) {
				configuration.setEnableAlert(false);
			} else if ("-alertType".equalsIgnoreCase(arg)) {
				Alert.AlertType alertType = Alert.AlertType.EMAIL;
				try {
					alertType = Alert.AlertType.valueOf(originalArgs[++i].toUpperCase());
				} catch (IllegalArgumentException ex) {
					System.out.println("Need to set -alertType [alertType]");
					System.out.println("alertType could be any of " + EnumUtil.getNames(Alert.AlertType.class));
					System.exit(1);
				}
				configuration.setAlertType(alertType);
			} else if ("-sendReport".equalsIgnoreCase(arg)) {
				String reportUrl = originalArgs[++i];
				String reportName = originalArgs[++i];
				Alert alert = new EmailAlert("zhang.hui@siemens.com", reportUrl, reportName);
				System.out.println("Trigger alert [reportUrl: " + reportUrl + "][reportName: " + reportName + "]");
				alert.execute();
				return;
			}
		}

		if (configuration.setUpParameters()) {
			this.execute();
		} else {
			System.out.println("Error: Failed to initialize parameters");
		}

	}

	private void execute() {
		System.out.println("projectName=" + configuration.getProjectName());
		System.out.println("envName=" + configuration.getEnvName());

		TestNG testNGInstance = new TestNG();
		XmlSuite myTestSuite = new XmlSuite();
		myTestSuite.setName("data-layer-automation test suite");

		List<String> myListeners = new ArrayList<String>();
		myListeners.add("com.siemens.devops.monitoring.utils.CustomizedTestListener");

		myTestSuite.setListeners(myListeners);

		// Create an instance of XmlTest and assign a name for it.
		XmlTest myTest = new XmlTest(myTestSuite);
		myTest.setName("Regression Tests for " + configuration.getConfigName());
		myTest.setVerbose(2);
		myTest.setPreserveOrder(true);

		// Add any parameters that you want to set to the Test.
		myTest.setParameters(configuration.getParamsOfGlobal());

		// Create a list which can contain the classes that you want to run.
		List<XmlClass> myClasses = configuration.generateAllClassForTest();

		// Attach the list of test classes to the XmlTest Object created earlier.
		myTest.setXmlClasses(myClasses);

		// Create a list of XML Suite and add the XML Suite into it.
		List<XmlSuite> mySuiteList = new ArrayList<XmlSuite>();
		mySuiteList.add(myTestSuite);

		// Set the above list of XML Suites into the testNG object created earlier.
		testNGInstance.setXmlSuites(mySuiteList);

		// Set the testNG XML file name
		String testngXmlName = configuration.getConfigName() + "-regression-test.xml";
		myTestSuite.setFileName(testngXmlName);

		// Execute the tests
		int executionStatus = 0;
		myTestSuite.setThreadCount(1);
		if (configuration.isRunRegressionTest()) {
			testNGInstance.run();
			executionStatus = testNGInstance.getStatus();
			System.out.println("TestNG execution status: " + executionStatus);
			if (executionStatus != 0) {
				triggerAlert(executionStatus);
			}
		}

		// Print the global parameters
		System.out.println("\nList of global test parameters:");
		Map<String, String> params = myTest.getAllParameters();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}

		// Create a physical XML file based on the virtual XML content
		if (configuration.isGenerateTestNGXML()) {
			try {
				createXmlFile(myTestSuite, testngXmlName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.exit(executionStatus);

	}

	// This method will create an Xml file based on the XmlSuite data
	private void createXmlFile(XmlSuite mSuite, String testngXmlName) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(testngXmlName));
			writer.write(mSuite.toXml());
			writer.flush();

			System.out.println("\nWrite test configurations to the following xml file:");
			System.out.println(new File(testngXmlName).getAbsolutePath());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void triggerAlert(int executionStatus) {
		if (this.configuration.isEnableAlert()) {
			Alert alert = null;
			if (this.configuration.getAlertType() == Alert.AlertType.EMAIL) {
				alert = new EmailAlert("zhang.hui@siemens.com", this.configuration);

			} else if (this.configuration.getAlertType() == Alert.AlertType.TEAMS) {
				alert = new TeamsAlert();
			}
			if (alert != null) {
				System.out.println("Trigger alert [" + this.getConfiguration().getConfigName() + "]");
				alert.execute();
			}
		}
	}
}
