package com.siemens.datalayer.testapp;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class autoRun {
	
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

		// Add test class for connectorRealtimeTest
		if (testConfig.getRunConnectorRealtimeTest())
		{
			XmlClass connectorRealtimeTests = new XmlClass(testConfig.getConnectorRealtimeTestClass());

			Map<String,String> connectorRealtimeTestParams = new HashMap<>();
			connectorRealtimeTestParams.put("base_url",testConfig.getConnectorRealtimeBaseURL());
			connectorRealtimeTestParams.put("port",testConfig.getConnectorRealtimePort());
			connectorRealtimeTestParams.put("mongodb_host",testConfig.getMongoDBHost());
			connectorRealtimeTestParams.put("mongodb_port",testConfig.getMongoDBPort());
			connectorRealtimeTestParams.put("mongodb_username",testConfig.getMongoDBUserName());
			connectorRealtimeTestParams.put("mongodb_password",testConfig.getMongoDBPassword());
			connectorRealtimeTestParams.put("mongodb_databasename",testConfig.getMongoDBDatabaseName());

			connectorRealtimeTests.setParameters(connectorRealtimeTestParams);
			myClasses.add(connectorRealtimeTests);
		}

		//Add test class for connectorConfigureTest
		if (testConfig.getRunConnectorConfigureTest())
		{
			XmlClass connectorConfigureTests = new XmlClass(testConfig.getConnectorConfigureTestClass());

			Map<String,String> connectorConfigureTestParams = new HashMap<>();
			connectorConfigureTestParams.put("base_url",testConfig.getConnectorConfigureBaseURL());
			connectorConfigureTestParams.put("port",testConfig.getConnectorConfigurePort());
			connectorConfigureTestParams.put("domain_name",testConfig.getDomainName());
			connectorConfigureTestParams.put("baseUrlOfConnector",testConfig.getConnectorBaseURL());
			connectorConfigureTestParams.put("portOfConnector",testConfig.getConnectorPort());
			connectorConfigureTestParams.put("mongodb_host",testConfig.getMongoDBHost());
			connectorConfigureTestParams.put("mongodb_port",testConfig.getMongoDBPort());
			connectorConfigureTestParams.put("mongodb_username",testConfig.getMongoDBUserName());
			connectorConfigureTestParams.put("mongodb_password",testConfig.getMongoDBPassword());
			connectorConfigureTestParams.put("mongodb_databasename",testConfig.getMongoDBDatabaseName());
			connectorConfigureTests.setParameters(connectorConfigureTestParams);
			myClasses.add(connectorConfigureTests);
		}


		//Add test class for ApiEngine
		// jinzu-test apiengine only use https to access
		XmlClass apiEngineTest = new XmlClass("com.siemens.datalayer.apiengine.test.QueryEndPointTests");
		
		Map<String,String> apiEngineTestParams = new LinkedHashMap<String,String> ();
		if(!testConfig.getRunApiEngineHttpsTest()) {
			apiEngineTestParams.put("base_url", testConfig.getApiEngineBaseURL());
			apiEngineTestParams.put("port", testConfig.getApiEnginePort());
			//Exclude getDataGraphQLHttps (https) test methods
			List<String> excludeMethods = new ArrayList<String>();
			excludeMethods.add("getDataGraphQLHttps");
			apiEngineTest.setExcludedMethods(excludeMethods);
			apiEngineTest.setParameters(apiEngineTestParams);
			myClasses.add(apiEngineTest);
		}else{
			apiEngineTestParams.put("base_url", testConfig.getApiEngineHttpsBaseURL());
			apiEngineTestParams.put("port", testConfig.getApiEngineHttpsPort());
			//Exclude getDataGraphQL (http) test methods
			List<String> excludeMethods = new ArrayList<String>();
			excludeMethods.add("getDataGraphQL");
			apiEngineTest.setExcludedMethods(excludeMethods);
			apiEngineTest.setParameters(apiEngineTestParams);
			myClasses.add(apiEngineTest);
		}

		//Add test class for userQueryTest
		if (testConfig.getRunUserQueryTest())
		{
			XmlClass userQueryTest = new XmlClass(testConfig.getUserQueryTestClass());
			userQueryTest.setParameters(apiEngineTestParams);
			myClasses.add(userQueryTest);
		}

		// Add test class for JDBCDatabasesTests
		if (testConfig.getRunJDBCDatabasesTest())
		{
			XmlClass JDBCDatabasesTest = new XmlClass(testConfig.getJDBCDatabasesTestClass());

			Map<String,String> JDBCDatabasesTestParams = new HashMap<>();
			JDBCDatabasesTestParams.put("base_url", testConfig.getApiEngineBaseURL());
			JDBCDatabasesTestParams.put("port", testConfig.getApiEnginePort());

			JDBCDatabasesTest.setParameters(JDBCDatabasesTestParams);
			myClasses.add(JDBCDatabasesTest);
		}

		// Add test class for OtherJDBCDatabasesTests
		if (testConfig.getRunOtherJDBCDatabasesTest())
		{
			XmlClass otherJDBCDatabasesTest = new XmlClass(testConfig.getOtherJDBCDatabasesTestClass());

			Map<String,String> otherJDBCDatabaseTestParams = new HashMap<>();
			otherJDBCDatabaseTestParams.put("base_url", testConfig.getApiEngineBaseURL());
			otherJDBCDatabaseTestParams.put("port", testConfig.getApiEnginePort());
			otherJDBCDatabaseTestParams.put("db_properties",testConfig.getMySQLProperties());

			otherJDBCDatabasesTest.setParameters(otherJDBCDatabaseTestParams);
			myClasses.add(otherJDBCDatabasesTest);
		}

		// Add test class for RestfulAsDataSourcesTests
		if (testConfig.getRunRestfulAsDataSourcesTest())
		{
			XmlClass restfulAsDataSourcesTest = new XmlClass(testConfig.getRestfulAsDataSourcesTestClass());

			Map<String,String> restfulAsDataSourcesTestParams = new HashMap<>();
			restfulAsDataSourcesTestParams.put("base_url", testConfig.getApiEngineBaseURL());
			restfulAsDataSourcesTestParams.put("port", testConfig.getApiEnginePort());

			restfulAsDataSourcesTest.setParameters(restfulAsDataSourcesTestParams);
			myClasses.add(restfulAsDataSourcesTest);
		}

		// Add test class for WebServiceAsDataSourcesTests
		 if (testConfig.getRunWebServiceAsDataSourcesTest()){
		 	 XmlClass webServiceAsDataSourcesTest = new XmlClass(testConfig.getWebServiceAsDataSourcesTestClass());
			 Map<String,String> webServiceAsDataSourcesTestParams = new HashMap<>();
			 webServiceAsDataSourcesTestParams.put("base_url",testConfig.getConnectorBaseURL());
			 webServiceAsDataSourcesTestParams.put("port",testConfig.getConnectorPort());
			 webServiceAsDataSourcesTest.setParameters(webServiceAsDataSourcesTestParams);
			 myClasses.add(webServiceAsDataSourcesTest);
		 }

		 // Add test class for AuthForRestfulWriteTests
		 if (testConfig.getRunAuthForRestfulWriteTest())
		 {
		 	XmlClass authForRestfulWriteTest = new XmlClass(testConfig.getAuthForRestfulWriteTestClass());

		 	Map<String,String> authForRestfulWriteTestParams = new HashMap<>();
		 	authForRestfulWriteTestParams.put("base_url", testConfig.getApiEngineBaseURL());
		 	authForRestfulWriteTestParams.put("port", testConfig.getApiEnginePort());

		 	authForRestfulWriteTest.setParameters(authForRestfulWriteTestParams);
		 	myClasses.add(authForRestfulWriteTest);
		 }

		 // And test class for restfulAsDataSourcesEnhanceTests
		if (testConfig.getRunRestfulAsDataSourcesEnhanceTest())
		{
			XmlClass restfulAsDataSourcesEnhanceTest = new XmlClass(testConfig.getRestfulAsDataSourcesEnhanceTestClass());

			Map<String,String> restfulAsDataSourcesEnhanceTestParams = new HashMap<>();
			restfulAsDataSourcesEnhanceTestParams.put("base_url", testConfig.getApiEngineBaseURL());
			restfulAsDataSourcesEnhanceTestParams.put("port", testConfig.getApiEnginePort());

			restfulAsDataSourcesEnhanceTest.setParameters(restfulAsDataSourcesEnhanceTestParams);
			myClasses.add(restfulAsDataSourcesEnhanceTest);
		}

		// Add test class for AuthForRestfulReadTests
		if (testConfig.getRunAuthForRestfulReadTest())
		{
			XmlClass authForRestfulReadTest = new XmlClass(testConfig.getAuthForRestfulReadTestClass());

			Map<String,String> authForRestfulReadTestParams = new HashMap<>();
			authForRestfulReadTestParams.put("base_url",testConfig.getConnectorBaseURL());
			authForRestfulReadTestParams.put("port",testConfig.getConnectorPort());
			authForRestfulReadTestParams.put("baseUrlOfConnectorConfigure",testConfig.getConnectorConfigureBaseURL());
			authForRestfulReadTestParams.put("portOfConnectorConfigure",testConfig.getConnectorConfigurePort());

			authForRestfulReadTest.setParameters(authForRestfulReadTestParams);
			myClasses.add(authForRestfulReadTest);
		}

		// Add test class for AuthForWebserviceTests
		if (testConfig.getRunAuthForWebserviceTest())
		{
			XmlClass authForWebserviceTest = new XmlClass(testConfig.getAuthForWebserviceTestClass());

			Map<String,String> authForWebserviceTestParams = new HashMap<>();
			authForWebserviceTestParams.put("base_url",testConfig.getConnectorBaseURL());
			authForWebserviceTestParams.put("port",testConfig.getConnectorPort());

			authForWebserviceTest.setParameters(authForWebserviceTestParams);
			myClasses.add(authForWebserviceTest);
		}

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
		// snc-test run publish graph and check case
		if (testConfig.getRunEntityMgmtTest())
		{
			XmlClass entityMgmtTest = new XmlClass("com.siemens.datalayer.entitymanagement.test.EntityManagementTests");
			if(!testConfig.getRunPublishGraphAndCheckTest()) {
				Map<String, String> entityMgmtTestParams = new LinkedHashMap<String, String>();
				entityMgmtTestParams.put("base_url", testConfig.getEntityManagementBaseURL());
				entityMgmtTestParams.put("port", testConfig.getEntityManagementPort());
				//Exclude test methods
				List<String> excludeMethods = new ArrayList<String>();
				excludeMethods.add("publishGraph");
				excludeMethods.add("updateCheck");
				entityMgmtTest.setExcludedMethods(excludeMethods);
				entityMgmtTest.setParameters(entityMgmtTestParams);
				myClasses.add(entityMgmtTest);
			}else{
				Map<String, String> entityMgmtTestParams = new LinkedHashMap<String, String>();
				entityMgmtTestParams.put("base_url", testConfig.getEntityManagementBaseURL());
				entityMgmtTestParams.put("port", testConfig.getEntityManagementPort());
				entityMgmtTest.setParameters(entityMgmtTestParams);
				myClasses.add(entityMgmtTest);
			}
		}
		
		//Add test class for SubscriptionManagement
		if (testConfig.getRunSubscriptionMgmtTest())
		{
			XmlClass subMgmtTest = new XmlClass("com.siemens.datalayer.subscriptionmanagement.test.SubscriptionManagementTests");
			
			Map<String,String> subMgmtTestParams = new LinkedHashMap<String,String> ();
			subMgmtTestParams.put("base_url", testConfig.getSubscriptionManagementBaseURL());
			subMgmtTestParams.put("port", testConfig.getSubscriptionManagementPort());
			
			subMgmtTest.setParameters(subMgmtTestParams);
			myClasses.add(subMgmtTest); 
		}

		// Add test class for 'App client authentication for k8s'
		if (testConfig.getRunAppClientAuthenticationForK8sTest())
		{
			XmlClass appClientAuthenticationForK8sTest = new XmlClass(testConfig.getAppClientAuthenticationForK8sTestClass());

			Map<String,String> appClientAuthenticationForK8sTestParams = new LinkedHashMap<>();

			appClientAuthenticationForK8sTest.setParameters(appClientAuthenticationForK8sTestParams);
			myClasses.add(appClientAuthenticationForK8sTest);
		}

		/***
		 * 以下为data-brain添加：
		 */
		// Add test calss for 'read DesigoCC/Enlighted history/realtime data from connector'
		if (testConfig.getRunDataBrainFromConnectorTest())
		{
			XmlClass dataBrainFromConnectorTest = new XmlClass(testConfig.getDataBrainFromConnectorTestClass());

			Map<String,String> dataBrainFromConnectorTestParams = new HashMap<>();
			dataBrainFromConnectorTestParams.put("base_url",testConfig.getConnectorBaseURL());
			dataBrainFromConnectorTestParams.put("port",testConfig.getConnectorPort());

			dataBrainFromConnectorTest.setParameters(dataBrainFromConnectorTestParams);
			myClasses.add(dataBrainFromConnectorTest);
		}

		// Add test class for 'read/write DesigoCC/Enlighted history/realtime data from api-engine'
		if (testConfig.getRunDataBrainFromApiEngineTest())
		{
			XmlClass dataBrainFromApiEngineTest = new XmlClass(testConfig.getDataBrainFromApiEngineTestClass());

			Map<String,String> dataBrainFromApiEngineTestParams = new HashMap<>();
			dataBrainFromApiEngineTestParams.put("base_url",testConfig.getApiEngineBaseURL());
			dataBrainFromApiEngineTestParams.put("port",testConfig.getApiEnginePort());
			dataBrainFromApiEngineTestParams.put("baseUrlOfConnector",testConfig.getConnectorBaseURL());
			dataBrainFromApiEngineTestParams.put("portOfConnector",testConfig.getConnectorPort());
			dataBrainFromApiEngineTestParams.put("baseUrlOfConnectorConfigure",testConfig.getConnectorConfigureBaseURL());
			dataBrainFromApiEngineTestParams.put("portOfConnectorConfigure",testConfig.getConnectorConfigurePort());
			dataBrainFromApiEngineTestParams.put("db_properties",testConfig.getMySQLProperties());

			dataBrainFromApiEngineTest.setParameters(dataBrainFromApiEngineTestParams);
			myClasses.add(dataBrainFromApiEngineTest);
		}

		if(testConfig.getRunLpgTransformLoadTest()){
			XmlClass lpgTransformLoadTest = new XmlClass(testConfig.getRunLpgTransformTestClass());

			Map<String,String> lpgTransformLoadTestTestParams = new HashMap<>();
			lpgTransformLoadTestTestParams.put("base_url",testConfig.getLpgTransformLoad_base_url());
			lpgTransformLoadTestTestParams.put("port",testConfig.getLpgTransformLoad_port());
			lpgTransformLoadTest.setParameters(lpgTransformLoadTestTestParams);
			myClasses.add(lpgTransformLoadTest);
		}

		if(testConfig.getRunDynamicGraphTest()){
			XmlClass dynamicGraphTest = new XmlClass(testConfig.getRunDynamicGraphTestClass());
			Map<String,String> dynamicGraphTestParams = new HashMap<>();
			dynamicGraphTestParams.put("base_url",testConfig.getLpgTransformLoad_base_url());
			dynamicGraphTestParams.put("port",testConfig.getLpgTransformLoad_port());
			dynamicGraphTest.setParameters(dynamicGraphTestParams);
			myClasses.add(dynamicGraphTest);
		}

       // Add test class for UiBackendTests
		if(testConfig.getRunUiBackendTest()){
			XmlClass uiBackendTest = new XmlClass(testConfig.getRunUiBackendTestClass());
			Map<String,String> uiBackendTestParams = new HashMap<>();
			uiBackendTestParams.put("base_url",testConfig.getUiBackend_base_url());
			uiBackendTestParams.put("port",testConfig.getUiBackend_port());
            uiBackendTestParams.put("entitymgt_port",testConfig.getEntityManagementPort());
			uiBackendTest.setParameters(uiBackendTestParams);
			myClasses.add(uiBackendTest);
		}

		// Add test class for 'read/write PostgresSQL(data source) Scenarios'
		if (testConfig.getRunPostgreSQLAsDataSourceTest())
		{
			XmlClass postgreSQLAsDataSourceTest = new XmlClass(testConfig.getPostgreSQLAsDataSourceTestClass());

			Map<String,String> postgreSQLAsDataSourceTestParams = new HashMap<>();
			postgreSQLAsDataSourceTestParams.put("base_url",testConfig.getApiEngineBaseURL());
			postgreSQLAsDataSourceTestParams.put("port",testConfig.getApiEnginePort());

			postgreSQLAsDataSourceTest.setParameters(postgreSQLAsDataSourceTestParams);
			myClasses.add(postgreSQLAsDataSourceTest);
		}

		// Add test class for 'verify ansteel function'
		if (testConfig.getRunAnsteelFromApiEngineTest())
		{
			XmlClass ansteelFromApiEngineTest = new XmlClass(testConfig.getAnsteelFromApiEngineTestClass());

			Map<String,String> ansteelFromApiEngineTestParams = new HashMap<>();
			ansteelFromApiEngineTestParams.put("base_url",testConfig.getApiEngineBaseURL());
			ansteelFromApiEngineTestParams.put("port",testConfig.getApiEnginePort());

			ansteelFromApiEngineTest.setParameters(ansteelFromApiEngineTestParams);
			myClasses.add(ansteelFromApiEngineTest);
		}

		// add test class for 'api engine cache controller'
		if (testConfig.getRunApiEngineCacheControllerTest())
		{
			XmlClass apiEngineCacheControllerTest = new XmlClass(testConfig.getApiEngineCacheControllerTestClass());

			Map<String,String> apiEngineCacheControllerTestParams = new HashMap<>();
			apiEngineCacheControllerTestParams.put("base_url",testConfig.getApiEngineBaseURL());
			apiEngineCacheControllerTestParams.put("port",testConfig.getApiEnginePort());
			apiEngineCacheControllerTestParams.put("baseUrlOfEntityManagement",testConfig.getEntityManagementBaseURL());
			apiEngineCacheControllerTestParams.put("portOfEntityManagement",testConfig.getEntityManagementPort());


			apiEngineCacheControllerTest.setParameters(apiEngineCacheControllerTestParams);
			myClasses.add(apiEngineCacheControllerTest);
		}

		// add test class for 'connector realtime cache controller'
		if (testConfig.getRunConnectorRealtimeCacheControllerTest())
		{
			XmlClass connectorRealtimeCacheControllerTest = new XmlClass(testConfig.getConnectorRealtimeCacheControllerTestClass());

			Map<String,String> connectorRealtimeCacheControllerTestParams = new HashMap<>();
			connectorRealtimeCacheControllerTestParams.put("base_url",testConfig.getConnectorRealtimeBaseURL());
			connectorRealtimeCacheControllerTestParams.put("port",testConfig.getConnectorRealtimePort());
			connectorRealtimeCacheControllerTest.setParameters(connectorRealtimeCacheControllerTestParams);
			myClasses.add(connectorRealtimeCacheControllerTest);
		}

		// add test class for 'connector cache controller'
		if (testConfig.getRunConnectorCacheControllerTest())
		{
			XmlClass connectorCacheControllerTest = new XmlClass(testConfig.getConnectorCacheControllerTestClass());

			Map<String,String> connectorCacheControllerTestParams = new HashMap<>();
			connectorCacheControllerTestParams.put("base_url",testConfig.getConnectorBaseURL());
			connectorCacheControllerTestParams.put("port",testConfig.getConnectorPort());
			connectorCacheControllerTest.setParameters(connectorCacheControllerTestParams);
			myClasses.add(connectorCacheControllerTest);
		}

		// Add test class for 'test which verify rspdata'
		if (testConfig.getRunTestWhichVerifyRspdataTest())
		{
			XmlClass testWhichVerifyRspdataTest = new XmlClass(testConfig.getTestWhichVerifyRspdataTestClass());

			Map<String,String> testWhichVerifyRspdataTestParams = new HashMap<>();
			testWhichVerifyRspdataTestParams.put("base_url",testConfig.getApiEngineBaseURL());
			testWhichVerifyRspdataTestParams.put("port",testConfig.getApiEnginePort());

			testWhichVerifyRspdataTest.setParameters(testWhichVerifyRspdataTestParams);
			myClasses.add(testWhichVerifyRspdataTest);
		}

		//Add test class for 'query on ClickHouse DB'
		if(testConfig.getRunClickhouseAsDataSourcesTest()){
			XmlClass clickhouseAsDataSourceTest = new XmlClass(testConfig.getClickhouseAsDataSourcesTestClass());

			Map<String,String> clickhouseAsDataSourceTestParams = new HashMap<>();
			clickhouseAsDataSourceTestParams.put("base_url",testConfig.getConnectorBaseURL());
			clickhouseAsDataSourceTestParams.put("port",testConfig.getConnectorPort());

			clickhouseAsDataSourceTest.setParameters(clickhouseAsDataSourceTestParams);
			myClasses.add(clickhouseAsDataSourceTest);
		}

		//Add test class for 'modbus test'
		if(testConfig.getRunModbusTest()){
			XmlClass modbusTest = new XmlClass(testConfig.getModbusTestClass());

			Map<String,String> modbusTestParams = new HashMap<>();
			modbusTestParams.put("base_url",testConfig.getConnectorBaseURL());
			modbusTestParams.put("port",testConfig.getConnectorPort());
			modbusTest.setParameters(modbusTestParams);
			myClasses.add(modbusTest);
		}

		//Attach the list of test classes to the XmlTest Object created earlier. 
		myTest.setXmlClasses(myClasses);   

		//Create a list of XmlTests and put the XmlTest Object created earlier to it.
		List<XmlTest> myTestList = new ArrayList<XmlTest>(); 
		myTestList.add(myTest);   

		//Add the above list of XmlTests to the XML Suite. 
		myTestSuite.setTests(myTestList);   

		//Create a list of XML Suite and add the XML Suite into it. 
		List<XmlSuite> mySuiteList = new ArrayList<XmlSuite>(); 
		mySuiteList.add(myTestSuite);   
	     
		//Set the above list of XML Suites into the testNG object created earlier. 
		testNGInstance.setXmlSuites(mySuiteList);
		
		//Set the testNG XML file name
		String testngXmlName = testConfig.getConfigName() + "-regression-test.xml";
		myTestSuite.setFileName(testngXmlName);
		
		//Execute the tests
		myTestSuite.setThreadCount(5);   
		if (runRegressionTest) testNGInstance.run();
		
		//Print the global parameters 
		System.out.println("List of global test parameters:");
		Map<String,String> params = myTest.getAllParameters(); 
		
		for(Map.Entry<String, String> entry : params.entrySet()) 
		{ 
			System.out.println(entry.getKey() + " = " + entry.getValue()); 
		}
		
		//Create a physical XML file based on the virtual XML content 
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
		if (args.length >= 2) // 如果输入的参数的个数大于或等于两个
		{
			// asList方法还提供了一个创建固定长度的列表的便捷方法，该列表被初始化为包含多个元素
			List<String> projectNameList = Arrays.asList("iot", "jinzu", "iems", "snc","databrain","ansteel");
			List<String> environmentList = Arrays.asList("dev", "test", "prod");
			
			if ((projectNameList.contains(args[0].toLowerCase())) && (environmentList.contains(args[1].toLowerCase())))
			{		
				// Read pilot name and the environment to run regression test
				String projectName = args[0].toLowerCase();
				String envName = args[1].toLowerCase();
				
				if (args.length > 2)
				{
					if (args[2].equals("noTest")) 
						runRegressionTest = false;
					else if (args[2].equals("noXML")) 
						generateTestNGXML = false;
					else
						System.out.println("Unknown input parameter - '" + args[2] + "', please use 'noTest' or 'noXML'");
				}
	
				testConfigurationClass testConfig = new testConfigurationClass(); //创建类testConfigurationClass的一个实例
				//方法loadConfigurations（传参：projectName, envName）作用：
				// 1、返回布尔值，表示环境是否在jinzu/iems/snc + dev/test/prod中
				// 2、设置各种变量，如domain、url、port
				// 3、设置测试项，通过赋值runApiServiceTest、runEntityMgmtTest和runSubscriptionMgmtTest
				if (testConfig.loadConfigurations(projectName, envName))
				{
					// Set Global test parameters	
					Map<String,String> testParameters = new LinkedHashMap<String,String> ();
					
					testParameters.put("dataFileForConnectorTest", projectName+"-connector-test-data.xlsx");			
					testParameters.put("dataFileForApiEngineTest", projectName+"-api-engine-test-data.xlsx");

					if (testConfig.getRunConnectorRealtimeTest())
						testParameters.put("dataFileForConnectorRealtimeTest",projectName+"-connector-realtime-test-data.xlsx");

					if(testConfig.getRunConnectorConfigureTest())
						testParameters.put("dataFileForConnectorConfigureTest",projectName+"-connector-configure-test-data.xlsx");
					
					if (testConfig.getRunApiServiceTest()) 
						testParameters.put("dataFileForApiServiceTest", projectName+"-api-service-test-data.xlsx");	
		
					if (testConfig.getRunEntityMgmtTest()) 
						testParameters.put("dataFileForEntityMgmtTest", projectName+"-entity-management-test-data.xlsx");
					
					if (testConfig.getRunSubscriptionMgmtTest())
						testParameters.put("dataFileForSubMgmtTest", projectName+"-subscription-management-test-data.xlsx");

					if (testConfig.getRunUiBackendTest())
					    testParameters.put("dataFileForUiBackendTest",projectName+"-ui-backend-test-data.xlsx");
					// Create testNG instance to execute tests
					autoRun dt = new autoRun();
					// main函数走到这一步，各配置已经set完成
					dt.runSDLRegressionTests(testConfig, testParameters); 
					System.exit(0);
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
