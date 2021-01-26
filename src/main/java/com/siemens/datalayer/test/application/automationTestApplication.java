package com.siemens.datalayer.test.application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class automationTestApplication {
	
	static Boolean runRegressionTest = true;
	static Boolean generateTestNGXML = true;
	
	public void runSDLRegressionTests(testConfigurationClass testConfig, Map<String,String> testParams)
	{
		//Create an instance on TestNG 
		TestNG testNGInstance = new TestNG();   
 
		//Create an instance of XML Suite and assign a name for it. 
		XmlSuite myTestSuite = new XmlSuite(); 
		myTestSuite.setName("data-layer-automation test suite"); 
		
		List<String> myListeners = new ArrayList<String>();
		myListeners.add("com.siemens.datalayer.utils.CustomizedTestListener");
		
		myTestSuite.setListeners(myListeners);	
//		mySuite.setParallel(XmlSuite.ParallelMode.METHODS);   

		//Create an instance of XmlTest and assign a name for it.  
		XmlTest myTest = new XmlTest(myTestSuite); 
		myTest.setName("Regression Tests for " + testConfig.getConfigName());
		myTest.setVerbose(2);
		myTest.setPreserveOrder(true);
	  
		//Add any parameters that you want to set to the Test. 
		myTest.setParameters(testParams); 

		//Create a list which can contain the classes that you want to run.
		List<XmlClass> myClasses = new ArrayList<XmlClass>();
	    
		//Add test class for Connector
		XmlClass connectorTest = new XmlClass("com.siemens.datalayer.connector.test.InterfaceTests");
		
		Map<String,String> connectorTestParams = new LinkedHashMap<String,String> ();
		connectorTestParams.put("base_url", testConfig.getConnectorBaseURL());
		connectorTestParams.put("port", testConfig.getConnectorPort());
		connectorTestParams.put("domain_name", testConfig.getDomainName());	
		
		connectorTest.setParameters(connectorTestParams);
		myClasses.add(connectorTest); 
		
		//Add test class for ApiEngine
		XmlClass apiEngineTest = new XmlClass("com.siemens.datalayer.apiengine.test.QueryEndPointTests");
		
		Map<String,String> apiEngineTestParams = new LinkedHashMap<String,String> ();
		apiEngineTestParams.put("base_url", testConfig.getApiEngineBaseURL());
		apiEngineTestParams.put("port", testConfig.getApiEnginePort());
		
		apiEngineTest.setParameters(apiEngineTestParams);
		myClasses.add(apiEngineTest);
		
		//Add test class for ApiService
		if (testConfig.getRunApiServiceTest())
		{
			XmlClass apiServiceTest = new XmlClass("com.siemens.datalayer.iems.test.ApiServiceTests");
			
			Map<String,String> apiServiceTestParams = new LinkedHashMap<String,String> ();
			apiServiceTestParams.put("base_url", testConfig.getApiServiceBaseURL());
			apiServiceTestParams.put("port", testConfig.getApiServicePort());
			apiServiceTestParams.put("pre_asset", testConfig.getPreAsset());
			apiServiceTestParams.put("pre_data", testConfig.getPreData());
			apiServiceTestParams.put("rabbitmq_host", testConfig.getRabbitMQHost());
			apiServiceTestParams.put("rabbitmq_port", testConfig.getRabbitMQPort());
			apiServiceTestParams.put("rabbitmq_username", testConfig.getRabbitMQUserName());		
			apiServiceTestParams.put("rabbitmq_password", testConfig.getRabbitMQPassword());		
			apiServiceTestParams.put("rabbitmq_virtual_host", testConfig.getRabbitMQVirtualHost());		
			apiServiceTestParams.put("rabbitmq_timeout", testConfig.getRabbitMQTimeout());
			apiServiceTestParams.put("rabbitmq_exchange", testConfig.getRabbitMQExchange());		
			
			apiServiceTest.setParameters(apiServiceTestParams);
			myClasses.add(apiServiceTest);
		}
		
		//Add test class for EntityManagement
		if (testConfig.getRunEntityMgmtTest())
		{
			XmlClass entityMgmtTest = new XmlClass("com.siemens.datalayer.entitymanagement.test.EntityManagementTests");
			
			Map<String,String> entityMgmtTestParams = new LinkedHashMap<String,String> ();
			entityMgmtTestParams.put("base_url", testConfig.getEntityManagementBaseURL());
			entityMgmtTestParams.put("port", testConfig.getEntityManagementPort());
			
			entityMgmtTest.setParameters(entityMgmtTestParams);
			myClasses.add(entityMgmtTest); 
		}

		//Assign that to the XmlTest Object created earlier. 
		myTest.setXmlClasses(myClasses);   

		//Create a list of XmlTests and put the Xmltest you created earlier to it.
		List<XmlTest> myTestList = new ArrayList<XmlTest>(); 
		myTestList.add(myTest);   

		//add the list of tests to your Suite. 
		myTestSuite.setTests(myTestList);   

		//Add the suite to the list of suites. 
		List<XmlSuite> mySuiteList = new ArrayList<XmlSuite>(); 
		mySuiteList.add(myTestSuite);   
	     
		//Set the list of Suites to the testNG object you created earlier. 
		testNGInstance.setXmlSuites(mySuiteList);
		
		String testngXmlName = testConfig.getConfigName() + "-regression-test.xml";
		myTestSuite.setFileName(testngXmlName);
		
		myTestSuite.setThreadCount(5);   
		if (runRegressionTest) testNGInstance.run();
		
		//Print the parameter values 
		System.out.println("List of global test parameters:");
		Map<String,String> params = myTest.getAllParameters(); 
		
		for(Map.Entry<String, String> entry : params.entrySet()) 
		{ 
			System.out.println(entry.getKey() + " = " + entry.getValue()); 
		}
		
		//Create physical XML file based on the virtual XML content 
		if (generateTestNGXML)
		{
			for(XmlSuite suite : mySuiteList) 
			{  
				createXmlFile(suite, testngXmlName); 
			} 
		}

	}

	//This method will create an Xml file based on the XmlSuite data 
	public void createXmlFile(XmlSuite mSuite, String testngXmlName) 
	{ 
		FileWriter writer; 
		try 
		{ 
			writer = new FileWriter(new File(testngXmlName)); 
			writer.write(mSuite.toXml()); 
			writer.flush(); 
			writer.close();
			
			System.out.println();
			System.out.println("Write test configurations to the following testNG.xml:");
			System.out.println(new File(testngXmlName).getAbsolutePath());
		} 
		catch (IOException e)
		{
			e.printStackTrace(); 
		}
	}
	
	//Main Method
	public static void main (String args[]) 
	{ 
		if (args.length >= 2)
		{
			List<String> projectNameList = Arrays.asList("iot", "jinzu", "iems", "snc");
			List<String> environmentList = Arrays.asList("dev", "test", "prod");
			
			if ((projectNameList.contains(args[0])) && (environmentList.contains(args[1])))
			{		
				// Read pilot name and the environment to run regression test
				String projectName = args[0];
				String envName = args[1];
				
				if (args.length > 2)
				{
					if (args[2].equals("noTest")) 
						runRegressionTest = false;
					else if (args[2].equals("noXML")) 
						generateTestNGXML = false;
					else
						System.out.println("Unknown input parameter - '" + args[2] + "', please use 'noTest' or 'noXML'");
				}
	
				testConfigurationClass testConfig = new testConfigurationClass();
				if (testConfig.loadConfigurations(projectName, envName))
				{
					// Set Global test parameters	
					Map<String,String> testParameters = new LinkedHashMap<String,String> ();
					
					testParameters.put("dataFileForConnectorTest", projectName+"-connector-test-data.xlsx");			
					testParameters.put("dataFileForApiEngineTest", projectName+"-api-engine-test-data.xlsx");
					
					if (testConfig.getRunApiServiceTest()) 
						testParameters.put("dataFileForApiServiceTest", projectName+"-api-service-test-data.xlsx");	
		
					if (testConfig.getRunEntityMgmtTest()) 
						testParameters.put("dataFileForEntityMgmtTest", projectName+"-entity-management-test-data.xlsx");	
					
					// Create testNG instance to execute tests
					automationTestApplication dt = new automationTestApplication(); 		
					dt.runSDLRegressionTests(testConfig, testParameters); 
				}
			}
			else
			{
				System.out.println("Error: The test environment '" + args[0] + "-" + args[1] + "' does not exist!");
			}
		}
		else
		{
			System.out.println("Please specify the test environment, e.g. 'iems test'");
		}
	}
}
