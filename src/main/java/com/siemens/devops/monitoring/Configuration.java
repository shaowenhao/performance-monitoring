package com.siemens.devops.monitoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.xml.XmlClass;

import com.siemens.devops.monitoring.utils.Alert;

public class Configuration {
	public static String KEY_PROJECT_NAME = "projectName";
	public static String KEY_ENV_NAME = "envName";
	public static String KEY_ENABLE_ALERT = "enableAlert";
	public static String KEY_ALERT_TYPE = "alertType";
	public static String KEY_DATA_FILE_FOR_API_ENGINE_TEST = "dataFileForApiEngineTest";
	public static String KEY_RUN_API_ENGINE_TEST = "runApiEngineTest";
	public static String KEY_API_ENGINE_BASE_URL = "apiEngineBaseUrl";
	public static String KEY_API_ENGINE_PORT = "apiEnginePort";
	public static String KEY_CONNECTOR_CONFIGURE_BASE_URL = "connectorConfigureBaseUrl";
	public static String KEY_CONNECTOR_CONFIGURE_PORT = "connectorConfigurePort";

	private boolean runRegressionTest;
	private boolean generateTestNGXML;
	private boolean enableAlert;
	private Alert.AlertType alertType;

	private Map<String, String> paramsOfGlobal = new LinkedHashMap<String, String>();
	private Map<String, String> paramsForApiEngineTest = new LinkedHashMap<String, String>();

	public Configuration() {
		runRegressionTest = true;
		generateTestNGXML = true;
		enableAlert = true;
		this.putParamOfGlobal(KEY_ENABLE_ALERT, Boolean.toString(enableAlert));
		alertType = Alert.AlertType.EMAIL;
		this.putParamOfGlobal(KEY_ALERT_TYPE, alertType.name());
	}

	public Map<String, String> getParamsOfGlobal() {
		return paramsOfGlobal;
	}

	public Map<String, String> getParamsForApiEngineTest() {
		return paramsForApiEngineTest;
	}

	public boolean isRunRegressionTest() {
		return runRegressionTest;
	}

	public void setRunRegressionTest(boolean runRegressionTest) {
		this.runRegressionTest = runRegressionTest;
	}

	public boolean isGenerateTestNGXML() {
		return generateTestNGXML;
	}

	public void setGenerateTestNGXML(boolean generateTestNGXML) {
		this.generateTestNGXML = generateTestNGXML;
	}

	public boolean isEnableAlert() {
		return enableAlert;
	}

	public void setEnableAlert(boolean enableAlert) {
		this.enableAlert = enableAlert;
		this.putParamOfGlobal(KEY_ENABLE_ALERT, Boolean.toString(enableAlert));
	}

	public Alert.AlertType getAlertType() {
		return alertType;
	}

	public void setAlertType(Alert.AlertType alertType) {
		this.alertType = alertType;
		this.putParamOfGlobal(KEY_ALERT_TYPE, alertType.name());
	}

	public String getEnvName() {
		return this.getParamOfGlobal(KEY_ENV_NAME);
	}

	public void setEnvName(String envName) {
		this.putParamOfGlobal(KEY_ENV_NAME, envName);
	}

	public String getProjectName() {
		return this.getParamOfGlobal(KEY_PROJECT_NAME);
	}

	public void setProjectName(String projectName) {
		this.putParamOfGlobal(KEY_PROJECT_NAME, projectName);
	}

	private void setParamsForApiEngineTest(String apiEngineBaseUrl, String port, String connectorConfigureBaseUrl,
			String connectorConfigurePort) {
		this.paramsForApiEngineTest.put(KEY_API_ENGINE_BASE_URL, apiEngineBaseUrl);
		this.paramsForApiEngineTest.put(KEY_API_ENGINE_PORT, port);
		this.paramsForApiEngineTest.put(KEY_CONNECTOR_CONFIGURE_BASE_URL, connectorConfigureBaseUrl);
		this.paramsForApiEngineTest.put(KEY_CONNECTOR_CONFIGURE_PORT, connectorConfigurePort);

	}

	private void putParamOfGlobal(String name, String value) {
		this.getParamsOfGlobal().put(name, value);
	}

	private String getParamOfGlobal(String name) {
		return this.getParamsOfGlobal().get(name);
	}

	public boolean isRunApiEngineTest() {
		return Boolean.valueOf(this.paramsForApiEngineTest.get(KEY_RUN_API_ENGINE_TEST));
	}

	private void setRunApiEngineTest(boolean runApiEngineTest) {
		this.paramsForApiEngineTest.put(KEY_RUN_API_ENGINE_TEST, Boolean.toString(runApiEngineTest));
	}

	public String getConfigName() {
		return this.getProjectName() + "-" + this.getEnvName();
	}

	public boolean setUpParameters() {
		List<String> projectNameList = Arrays.asList("iot", "ansteel");
		List<String> environmentList = Arrays.asList("dev", "test", "prod");
		if (projectNameList.contains(this.getProjectName()) && environmentList.contains(this.getEnvName())) {

			this.putParamOfGlobal(KEY_DATA_FILE_FOR_API_ENGINE_TEST,
					this.getProjectName() + "-api-engine-test-data.xlsx");

			String configName = getConfigName();
			switch (configName) {
			case ("iot-dev"):
				setRunApiEngineTest(true);
				setParamsForApiEngineTest(EnvironmentConstants.IOT_DEV_APIENGINE_BASE_URL,
						EnvironmentConstants.IOT_DEV_APIENGINE_PORT,
						EnvironmentConstants.IOT_DEV_CONNECTOR_CONFIGURE_BASE_URL,
						EnvironmentConstants.IOT_DEV_CONNECTOR_CONFIGURE_PORT);
				break;
			case ("iot-test"):
				setRunApiEngineTest(true);
				setParamsForApiEngineTest(EnvironmentConstants.IOT_TEST_APIENGINE_BASE_URL,
						EnvironmentConstants.IOT_TEST_APIENGINE_PORT,
						EnvironmentConstants.IOT_TEST_CONNECTOR_CONFIGURE_BASE_URL,
						EnvironmentConstants.IOT_TEST_CONNECTOR_CONFIGURE_PORT);
				break;
			case ("iot-prod"):
				setRunApiEngineTest(true);
				setParamsForApiEngineTest(EnvironmentConstants.IOT_PROD_APIENGINE_BASE_URL,
						EnvironmentConstants.IOT_PROD_APIENGINE_PORT,
						EnvironmentConstants.IOT_PROD_CONNECTOR_CONFIGURE_BASE_URL,
						EnvironmentConstants.IOT_PROD_CONNECTOR_CONFIGURE_PORT);
				break;
			case ("ansteel-dev"):
				setRunApiEngineTest(true);
				setParamsForApiEngineTest(EnvironmentConstants.ANSTEEL_TEST_APIENGINE_BASE_URL,
						EnvironmentConstants.ANSTEEL_TEST_APIENGINE_PORT,
						EnvironmentConstants.ANSTEEL_TEST_CONNECTOR_CONFIGURE_BASE_URL,
						EnvironmentConstants.ANSTEEL_TEST_CONNECTOR_CONFIGURE_PORT);
			case ("ansteel-test"):
				setRunApiEngineTest(true);
				setParamsForApiEngineTest(EnvironmentConstants.ANSTEEL_TEST_APIENGINE_BASE_URL,
						EnvironmentConstants.ANSTEEL_TEST_APIENGINE_PORT,
						EnvironmentConstants.ANSTEEL_TEST_CONNECTOR_CONFIGURE_BASE_URL,
						EnvironmentConstants.ANSTEEL_TEST_CONNECTOR_CONFIGURE_PORT);
			case ("ansteel-prod"):
				setRunApiEngineTest(true);
				setParamsForApiEngineTest(EnvironmentConstants.ANSTEEL_TEST_APIENGINE_BASE_URL,
						EnvironmentConstants.ANSTEEL_TEST_APIENGINE_PORT,
						EnvironmentConstants.ANSTEEL_TEST_CONNECTOR_CONFIGURE_BASE_URL,
						EnvironmentConstants.ANSTEEL_TEST_CONNECTOR_CONFIGURE_PORT);
				break;
			default:
				System.out.println("Error: Unknown environment '" + configName + "'!");
				return false;
			}
			return true;
		} else {
			if (!projectNameList.contains(this.getProjectName())) {
				System.out.println("Error: projectName '" + this.getProjectName() + "' does not exist!");
				System.out.println("Error: projectName should be any of " + projectNameList);
			}
			if (!environmentList.contains(this.getEnvName())) {
				System.out.println("Error: envName '" + this.getEnvName() + "' does not exist!");
				System.out.println("Error: envName should be any of " + environmentList);
			}
			return false;
		}

	}

	public List<XmlClass> generateAllClassForTest() {
		List<XmlClass> myClasses = new ArrayList<XmlClass>();

		if (this.isRunApiEngineTest()) {
			XmlClass apiEngineTest = new XmlClass("com.siemens.devops.monitoring.test.QueryEndPointTest");
			Map<String, String> apiEngineTestParams = this.getParamsForApiEngineTest();
			apiEngineTest.setParameters(apiEngineTestParams);
			myClasses.add(apiEngineTest);
		}

		return myClasses;
	}

}
