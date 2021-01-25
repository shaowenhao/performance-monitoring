package com.siemens.datalayer.test.application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class automationTestApplication {
	
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
	  
		//Add any parameters that you want to set to the Test. 
		myTest.setParameters(testParams); 

		//Create a list which can contain the classes that you want to run.
		List<XmlClass> myClasses = new ArrayList<XmlClass>();
	    
		if (testConfig.getRunEntityMgmtTest())
		{
			XmlClass entityMgmtTest = new XmlClass("com.siemens.datalayer.entitymanagement.test.EntityManagementTests");
			
			Map<String,String> entityMgmtTestParams = new HashMap<String,String> ();
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
		String testngXmlName = testConfig.getConfigName() + "-regression-test.xml";
		testNGInstance.setXmlSuites(mySuiteList);
		myTestSuite.setFileName(testngXmlName); 
		myTestSuite.setThreadCount(5);   
		testNGInstance.run();

		//Create physical XML file based on the virtual XML content 
		for(XmlSuite suite : mySuiteList) 
		{  
			createXmlFile(suite, testngXmlName); 
		}   
	 
		//Print the parameter values 
		Map<String,String> params = myTest.getAllParameters(); 
		for(Map.Entry<String, String> entry : params.entrySet()) 
		{ 
			System.out.println(entry.getKey() + " => " + entry.getValue()); 
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
		List<String> projectNameList = Arrays.asList("iot", "jinzu", "iems", "snc");
		List<String> environmentList = Arrays.asList("dev", "test", "prod");
		
		if ((projectNameList.contains(args[0])) && (environmentList.contains(args[1])))
		{		
			// Read pilot name and the environment to run regression test
			String projectName = args[0];
			String envName = args[1];

			testConfigurationClass testConfig = new testConfigurationClass();
			if (testConfig.loadConfigurations(projectName, envName))
			{
				// Set Global test parameters	
				Map<String,String> testParameters = new HashMap<String,String> ();
				
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
}
