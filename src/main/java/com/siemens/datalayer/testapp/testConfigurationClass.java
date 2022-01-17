package com.siemens.datalayer.testapp;

public class testConfigurationClass {

	private String configName = null;
	private String domainName = null;
	
	private String connector_base_url = null;
	private String connector_port = null;

	private String connectorRealtime_base_url = null;
	private String connectorRealtime_port = null;

	private String connectorConfigure_base_url = null;
	private String connectorConfigure_port = null;

	private String apiEngine_base_url = null;
	private String apiEngine_port = null;

	private String apiEngine_https_base_url = null;
	private String apiEngine_https_port = null;

	private String mongodb_host = null;
	private String mongodb_port = null;
	private String mongodb_username = null;
	private String mongodb_password = null;
	private String mongodb_databasename = null;

	private String relationalDatabase_properties = null;

	private String apiService_base_url = null;
	private String apiService_port = null;
	private String pre_asset = null;
	private String pre_data = null;
	private String rabbitmq_host = null;
	private String rabbitmq_port = null;
	private String rabbitmq_username = null;
	private String rabbitmq_password = null;
	private String rabbitmq_virtual_host = null;
	private String rabbitmq_timeout = null;
	private String rabbitmq_exchange = null;

	private String entityManagement_base_url = null;
	private String entityManagement_port = null;

	private String subscriptionManagement_base_url = null;
	private String subscriptionManagement_port = null;

	Boolean runConnectorRealtimeTest = false;
	private String connectorRealtimeTestClass = null;

	Boolean runConnectorConfigureTest = false;
	private String connectorConfigureTestClass = null;

	Boolean runRelationalDatabaseTest = false;
	private String relationalDatabaseTestClass = null;

	Boolean runRestfulAsDataSourcesTest = false;
	private String restfulAsDataSourcesTestClass = null;

	Boolean runAuthForRestfulWriteTest = false;
	private String authForRestfulWriteTestClass = null;

	Boolean runAuthForRestfulReadTest = false;
	private String authForRestfulReadTestClass = null;

	Boolean runAuthForWebserviceTest = false;
	private String authForWebserviceTestClass = null;

	Boolean runWebServiceAsDataSourcesTest = false;
	private String webServiceAsDataSourcesTestClass = null;

	Boolean runAppClientAuthenticationForK8sTest = false;
	private String appClientAuthenticationForK8sTestClass = null;

	Boolean runUserQueryTest = false;
	private String userQueryTestClass = null;

	Boolean runApiServiceTest = false;
	
	Boolean runEntityMgmtTest = false;
	
	Boolean runSubscriptionMgmtTest = false;

	Boolean runApiEngineHttpsTest = false;

	Boolean runPublishGraphAndCheckTest = false;

	/***
	 * 以下为data-brain添加（包括desigoCC、enlighted等）：
	 */
	Boolean runDesigoCCTest = false;
	private String runDesigoCCTestClass = null;

	private String uiBackend_base_url = null;

	public String getUiBackend_base_url() {
		return uiBackend_base_url;
	}

	public String getUiBackend_port() {
		return uiBackend_port;
	}

	private String uiBackend_port = null;
	Boolean runUiBackendTest = false;
	private String runUiBackendTestClass = null;

	public Boolean getRunUiBackendTest() {
		return runUiBackendTest;
	}

	public String getRunUiBackendTestClass() {
		return runUiBackendTestClass;
	}

	public Boolean getRunPublishGraphAndCheckTest() {
		return runPublishGraphAndCheckTest;
	}

	public Boolean getRunApiEngineHttpsTest() {
		return runApiEngineHttpsTest;
	}

	public void setConfigName(String name) {
		this.configName = name;
	}
	
	public String getConfigName() {
		return this.configName;
	}
	
	public void setConnectorTest(String domainName, String base_url, String port) {
		this.domainName = domainName;
		this.connector_base_url = base_url;
		this.connector_port = port;
	}
	
	public String getDomainName() {
		return this.domainName;
	}
	
	public String getConnectorBaseURL() {
		return this.connector_base_url;
	}
	
	public String getConnectorPort() {
		return this.connector_port;
	}

	// setConnectorRealtimeTest、getConnectorRealtimeBaseURL、getConnectorRealtimePort
	// 这三个方法针对获取data-layer-connector-realtime的地址、端口号 而添加
	public void setConnectorRealtimeTest(String base_url,String port)
	{
		this.connectorRealtime_base_url = base_url;
		this.connectorRealtime_port = port;
	}

	public String getConnectorRealtimeBaseURL(){return this.connectorRealtime_base_url;}

	public String getConnectorRealtimePort(){return this.connectorRealtime_port;}

	// setConnectorConfigureTest、getConnectorConfigureBaseURL、getConnectorConfigurePort
	// 这三个方法针对获取data-layer-connector-configure的地址、端口号 而添加
	public void setConnectorConfigureTest(String base_url,String port){
		this.connectorConfigure_base_url = base_url;
		this.connectorConfigure_port = port;
	}

	public String getConnectorConfigureBaseURL(){
		return this.connectorConfigure_base_url;
	}

	public String getConnectorConfigurePort(){return this.connectorConfigure_port;}
	
	public void setApiEngineTest(String base_url, String port) {
		this.apiEngine_base_url = base_url;
		this.apiEngine_port = port;
	}

	public String getApiEngineBaseURL() {
		return this.apiEngine_base_url;
	}

	public String getApiEnginePort() {
		return this.apiEngine_port;
	}

	public void setApiEngineHttpsTest(String base_url, String port) {
		this.runApiEngineHttpsTest = true;
		this.apiEngine_https_base_url = base_url;
		this.apiEngine_https_port = port;
	}
	public String getApiEngineHttpsBaseURL() {
		return this.apiEngine_https_base_url;
	}
	public String getApiEngineHttpsPort() {
		return this.apiEngine_https_port;
	}

	// setRelationalDatabaseTest、getRelationalDatabaseProperties
	// 这两个方法针对获取数据库的配置 而添加
	public void setRelationalDatabaseTest(String properties){
		this.relationalDatabase_properties = properties;
	}

	public String getRelationalDatabaseProperties()
	{
		return this.relationalDatabase_properties;
	}

	// setMongoDBHost、getMongoDBHost、setMongoDBPort、getMongoDBPort、setMongoDBUserName、getMongoDBUserName
	// setMongoDBPassword、getMongoDBPassword、setMongoDBDatabaseName、getMongoDBDatabaseName
	// 这10个方法针对mongodb的连接而创建
	public void setMongoDBHost(String host){
		this.mongodb_host = host;
	}

	public String getMongoDBHost(){
		return this.mongodb_host;
	}

	public void setMongoDBPort(String port){
		this.mongodb_port = port;
	}

	public String getMongoDBPort(){
		return this.mongodb_port;
	}

	public void setMongoDBUserName(String username){
		this.mongodb_username = username;
	}

	public String getMongoDBUserName(){
		return this.mongodb_username;
	}

	public void setMongoDBPassword(String password){
		this.mongodb_password = password;
	}

	public String getMongoDBPassword(){
		return this.mongodb_password;
	}

	public void setMongoDBDatabaseName(String databasename){
		this.mongodb_databasename = databasename;
	}

	public String getMongoDBDatabaseName(){
		return this.mongodb_databasename;
	}

	// getRunConnectorRealtimeTest、getConnectorRealtimeTestClass、getRunConnectorConfigureTest
	// 这三个方法针对connector-realtime接口的测试 而添加
	public Boolean getRunConnectorRealtimeTest()
	{
		return this.runConnectorRealtimeTest;
	}

	public void setConnectorRealtimeTestClass(String connectorRealtimeTestClassName)
	{
		this.runConnectorRealtimeTest = true;
		this.connectorRealtimeTestClass = connectorRealtimeTestClassName;
	}

	public String getConnectorRealtimeTestClass()
	{
		return this.connectorRealtimeTestClass;
	}

	// getRunConnectorConfigureTest、setConnectorConfigureTestClass、getConnectorConfigureTestClass
	// 这三个方法针对connector-configure接口的测试 而添加
	public Boolean getRunConnectorConfigureTest()
	{
		return this.runConnectorConfigureTest;
	}

	public void setConnectorConfigureTestClass(String connectorConfigureTestClassName)
	{
		this.runConnectorConfigureTest = true;
		this.connectorConfigureTestClass = connectorConfigureTestClassName;
	}

	public String getConnectorConfigureTestClass()
	{
		return this.connectorConfigureTestClass;
	}

	public Boolean getRunUserQueryTest()
	{
		return this.runUserQueryTest;
	}
	
	public void setUserQueryTestClass(String userQueryTestClassName)
	{
		this.runUserQueryTest = true;
		this.userQueryTestClass = userQueryTestClassName;
	}
	
	public String getUserQueryTestClass() {
		return this.userQueryTestClass;
	}

	public void setApiServiceTest(String base_url, String port) {
		this.runApiServiceTest = true;
		this.apiService_base_url = base_url;
		this.apiService_port = port;
	}
	
	public Boolean getRunApiServiceTest()
	{
		return this.runApiServiceTest;
	}
	
	public String getApiServiceBaseURL() {
		return this.apiService_base_url;
	}	
	
	public String getApiServicePort() {
		return this.apiService_port;
	}
	
	public void setPreAsset(String pre_asset) {
		this.pre_asset = pre_asset;
	}
	
	public String getPreAsset() {
		return this.pre_asset;
	}
	
	public void setPreData(String pre_data) {
		this.pre_data = pre_data;
	}
	
	public String getPreData() {
		return this.pre_data;
	}
	
	public void setRabbitMQHost(String host) {
		this.rabbitmq_host = host;
	}
	
	public String getRabbitMQHost() {
		return this.rabbitmq_host;
	}
	
	public void setRabbitMQPort(String port) {
		this.rabbitmq_port = port;
	}	
	
	public String getRabbitMQPort() {
		return this.rabbitmq_port;
	}

	public void setRabbitMQUserName(String username) {
		this.rabbitmq_username = username;
	}	
	
	public String getRabbitMQUserName() {
		return this.rabbitmq_username;
	}
	
	public void setRabbitMQPassword(String password) {
		this.rabbitmq_password = password;
	}	
		
	public String getRabbitMQPassword() {
		return this.rabbitmq_password;
	}	
	
	public void setRabbitMQVirtualHost(String virtualHost) {
		this.rabbitmq_virtual_host = virtualHost;
	}
		
	public String getRabbitMQVirtualHost() {
		return this.rabbitmq_virtual_host;
	}
	
	public void setRabbitMQTimeout(String timeout) {
		this.rabbitmq_timeout = timeout;
	}
		
	public String getRabbitMQTimeout() {
		return this.rabbitmq_timeout;
	}
	
	public void setRabbitMQExchange(String exchange) {
		this.rabbitmq_exchange = exchange;
	}
		
	public String getRabbitMQExchange() {
		return this.rabbitmq_exchange;
	}	
	
	public void setEntityMgmtTest(String base_url, String port) {
		this.runEntityMgmtTest = true;
		this.entityManagement_base_url = base_url;
		this.entityManagement_port = port;
	}
	
	public Boolean getRunEntityMgmtTest()
	{
		return this.runEntityMgmtTest;
	}
	
	public String getEntityManagementBaseURL() {
		return this.entityManagement_base_url;
	}
	
	public String getEntityManagementPort() {
		return this.entityManagement_port;
	}
	
	public void setSubscriptionMgmtTest(String base_url, String port) {
		this.runSubscriptionMgmtTest = true;
		this.subscriptionManagement_base_url = base_url;
		this.subscriptionManagement_port = port;
	}
	
	public Boolean getRunSubscriptionMgmtTest() {
		return this.runSubscriptionMgmtTest;
	}
	
	public String getSubscriptionManagementBaseURL() {
		return this.subscriptionManagement_base_url;
	}
	
	public String getSubscriptionManagementPort() {
		return this.subscriptionManagement_port;
	}

	//这三个方法针对Web服务作为数据源的测试 而添加
	public Boolean getRunWebServiceAsDataSourcesTest() {
		return this.runWebServiceAsDataSourcesTest;
	}

	public String getWebServiceAsDataSourcesTestClass() {
		return this.webServiceAsDataSourcesTestClass;
	}

	public void setWebServiceAsDataSourcesTestClass(String webServiceAsDataSourcesTestClassName) {
		this.runWebServiceAsDataSourcesTest = true;
		this.webServiceAsDataSourcesTestClass = webServiceAsDataSourcesTestClassName;
	}

	// getRunRelationalDatabaseTest、setRelationalDatabaseTestClass、getRelationalDatabaseTestClass
	// 这三个方法针对“关系型数据库做为数据源”的测试 而添加
	public Boolean getRunRelationalDatabaseTest()
	{
		return this.runRelationalDatabaseTest;
	}

	public void setRelationalDatabaseTestClass(String relationalDatabaseTestClassName)
	{
		this.runRelationalDatabaseTest = true;
		this.relationalDatabaseTestClass = relationalDatabaseTestClassName;
	}

	public String getRelationalDatabaseTestClass()
	{
		return this.relationalDatabaseTestClass;
	}

	// getRunRestfulAsDataSourcesTests、setRestfulAsDataSourcesTestClass、getRestfulAsDataSourcesTestClass
	// 这三个方法针对“restful做为数据源”的测试 而添加
	public Boolean getRunRestfulAsDataSourcesTest()
	{
		return this.runRestfulAsDataSourcesTest;
	}

	public void setRestfulAsDataSourcesTestClass(String restfulAsDataSourcesTestClassName)
	{
		this.runRestfulAsDataSourcesTest = true;
		this.restfulAsDataSourcesTestClass = restfulAsDataSourcesTestClassName;
	}

	public String getRestfulAsDataSourcesTestClass() {return this.restfulAsDataSourcesTestClass;}

	// getRunAuthForRestfulWriteTest、setAuthForRestfulWriteTestClass、getAuthForRestfulWriteTestClass
	// 这三个方法针对“auth for restful write”的测试 而添加
	public Boolean getRunAuthForRestfulWriteTest(){return this.runAuthForRestfulWriteTest;}

	public void setAuthForRestfulWriteTestClass(String authForRestfulWriteTestClassName)
	{
		this.runAuthForRestfulWriteTest = true;
		this.authForRestfulWriteTestClass = authForRestfulWriteTestClassName;
	}

	public String getAuthForRestfulWriteTestClass(){return this.authForRestfulWriteTestClass;}

	// getRunAuthForRestfulReadTest、setAuthForRestfulReadTestClass、getAuthForRestfulReadTestClass
	// 这三个方法针对“auth for restful read”的测试 而添加
	public Boolean getRunAuthForRestfulReadTest(){return this.runAuthForRestfulReadTest;}

	public void setAuthForRestfulReadTestClass(String authForRestfulReadTestClassName)
	{
		this.runAuthForRestfulReadTest = true;
		this.authForRestfulReadTestClass = authForRestfulReadTestClassName;
	}

	public String getAuthForRestfulReadTestClass(){return this.authForRestfulReadTestClass;}

	// getRunAuthForWebserviceTest、setAuthForWebserviceTestClass、getAuthForWebserviceTestClass
	// 这三个方法针对“auth for webservice”的测试 而添加
	public Boolean getRunAuthForWebserviceTest(){return this.runAuthForWebserviceTest;}

	public void setAuthForWebserviceTestClass(String authForWebserviceTestClassName)
	{
		this.runAuthForWebserviceTest = true;
		this.authForWebserviceTestClass = authForWebserviceTestClassName;
	}

	public String getAuthForWebserviceTestClass(){return this.authForWebserviceTestClass;};

	// getRunAppClientAuthenticationForK8sTest、setAppClientAuthenticationForK8sTestClass、getAppClientAuthenticationForK8sTestClass
	// 这三个方法针对“App client authentication for k8s”的测试 而添加
	public Boolean getRunAppClientAuthenticationForK8sTest(){return this.runAppClientAuthenticationForK8sTest;}

	public void setAppClientAuthenticationForK8sTestClass(String appClientAuthenticationForK8sTestClassName)
	{
		this.runAppClientAuthenticationForK8sTest = true;
		this.appClientAuthenticationForK8sTestClass = appClientAuthenticationForK8sTestClassName;
	}

	public String getAppClientAuthenticationForK8sTestClass(){return this.appClientAuthenticationForK8sTestClass;}

	/***
	 * 以下为data-brain添加：
	 */
	// getRunDesigoCCTest、setRunDesigoCCTestClass、getRunDesigoCCTestClass
	// 这三个方法针对“read desigoCC history/realtime data”的测试 而添加
	public Boolean getRunDesigoCCTest(){return this.runDesigoCCTest;}

	public void setDesigoCCTestClass(String runDesigoCCTestClassName)
	{
		this.runDesigoCCTest = true;
		this.runDesigoCCTestClass = runDesigoCCTestClassName;
	}

	public String getDesigoCCTestClass(){return this.runDesigoCCTestClass;}

	public void setPublishGraphAndCheckTest(Boolean isRun){
		this.runPublishGraphAndCheckTest = isRun;
	}

	private void setRunUiBackendTest(String base_url, String port,String runUiBackendTestClass,String entitymgt_port) {
		this.runUiBackendTest = true;
		this.runUiBackendTestClass = runUiBackendTestClass;
		this.uiBackend_base_url = base_url;
		this.uiBackend_port = port;
		this.entityManagement_port = entitymgt_port;
	}

	/* 设置配置，包括各pilot对应的dev/test/prod三个环境的connector/api-engine/api-service/entity-management/
	subscription-management/RabbitMq/
	返回布尔值，如果testEnvName不在iems-dev/iems-test/iems-prod/snc-dev/snc-test/snc-prod/jinzu-dev/jinzu-test/jinzu-prod中
	则返回false，否者true
	赋值：private String configName = testEnvName，如iems-dev*/
	public Boolean loadConfigurations(String pilotName, String envName)
	{
		String testEnvName = pilotName + "-" + envName;
		
		switch(testEnvName)
		{
			case ("iems-dev"):
				setConnectorTest(testEnvironmentConstants.IEMS_DOMAIN_NAME,
								 testEnvironmentConstants.IEMS_DEV_CONNECTOR_BASE_URL,
								 testEnvironmentConstants.IEMS_DEV_CONNECTOR_PORT);
				
				setApiEngineTest(testEnvironmentConstants.IEMS_DEV_APIENGINE_BASE_URL, 
								 testEnvironmentConstants.IEMS_DEV_APIENGINE_PORT);
				
				// 赋值：this.runApiServiceTest = true
				setApiServiceTest(testEnvironmentConstants.IEMS_DEV_APISERVICE_BASE_URL,
								  testEnvironmentConstants.IEMS_DEV_APISERVICE_PORT);
				
				setPreAsset(testEnvironmentConstants.IEMS_PRE_ASSET);
				setPreData(testEnvironmentConstants.IEMS_PRE_DATA);
				
				setRabbitMQHost(testEnvironmentConstants.IEMS_DEV_RABBITMQ_HOST);
				setRabbitMQPort(testEnvironmentConstants.IEMS_DEV_RABBITMQ_PORT);
				setRabbitMQUserName(testEnvironmentConstants.IEMS_DEV_RABBITMQ_USERNAME);
				setRabbitMQPassword(testEnvironmentConstants.IEMS_DEV_RABBITMQ_PASSWORD);
				setRabbitMQVirtualHost(testEnvironmentConstants.IEMS_DEV_RABBITMQ_VIRTUALHOST);
				setRabbitMQTimeout(testEnvironmentConstants.IEMS_DEV_RABBITMQ_TIMEOUT);
				setRabbitMQExchange(testEnvironmentConstants.IEMS_DEV_RABBITMQ_EXCHANGE);

				// 赋值：this.runEntityMgmtTest = true;
				setEntityMgmtTest(testEnvironmentConstants.IEMS_DEV_ENTITY_MANAGEMENT_BASE_URL,
								  testEnvironmentConstants.IEMS_DEV_ENTITY_MANAGEMENT_PORT);
				// 赋值：this.runSubscriptionMgmtTest = true;
				setSubscriptionMgmtTest(testEnvironmentConstants.IEMS_DEV_SUBSCRIPTION_MANAGEMENT_BASE_URL,
										testEnvironmentConstants.IEMS_DEV_SUBSCRIPTION_MANAGEMENT_PORT);
				
				break;
			
			case ("iems-test"):
				setConnectorTest(testEnvironmentConstants.IEMS_DOMAIN_NAME,
						 testEnvironmentConstants.IEMS_TEST_CONNECTOR_BASE_URL, 
						 testEnvironmentConstants.IEMS_TEST_CONNECTOR_PORT);
		
				setApiEngineTest(testEnvironmentConstants.IEMS_TEST_APIENGINE_BASE_URL, 
								 testEnvironmentConstants.IEMS_TEST_APIENGINE_PORT);
				
				setApiServiceTest(testEnvironmentConstants.IEMS_TEST_APISERVICE_BASE_URL, 
								  testEnvironmentConstants.IEMS_TEST_APISERVICE_PORT);
				
				setPreAsset(testEnvironmentConstants.IEMS_PRE_ASSET);
				setPreData(testEnvironmentConstants.IEMS_PRE_DATA);
				
				setRabbitMQHost(testEnvironmentConstants.IEMS_TEST_RABBITMQ_HOST);
				setRabbitMQPort(testEnvironmentConstants.IEMS_TEST_RABBITMQ_PORT);
				setRabbitMQUserName(testEnvironmentConstants.IEMS_TEST_RABBITMQ_USERNAME);
				setRabbitMQPassword(testEnvironmentConstants.IEMS_TEST_RABBITMQ_PASSWORD);
				setRabbitMQVirtualHost(testEnvironmentConstants.IEMS_TEST_RABBITMQ_VIRTUALHOST);
				setRabbitMQTimeout(testEnvironmentConstants.IEMS_TEST_RABBITMQ_TIMEOUT);
				setRabbitMQExchange(testEnvironmentConstants.IEMS_TEST_RABBITMQ_EXCHANGE);
				
				setEntityMgmtTest(testEnvironmentConstants.IEMS_TEST_ENTITY_MANAGEMENT_BASE_URL,
								  testEnvironmentConstants.IEMS_TEST_ENTITY_MANAGEMENT_PORT);
				
				setSubscriptionMgmtTest(testEnvironmentConstants.IEMS_TEST_SUBSCRIPTION_MANAGEMENT_BASE_URL,
										testEnvironmentConstants.IEMS_TEST_SUBSCRIPTION_MANAGEMENT_PORT);
			
				break;
			
			case ("iems-prod"):
				setConnectorTest(testEnvironmentConstants.IEMS_DOMAIN_NAME,
								 testEnvironmentConstants.IEMS_PROD_CONNECTOR_BASE_URL, 
								 testEnvironmentConstants.IEMS_PROD_CONNECTOR_PORT);
				
				setApiEngineTest(testEnvironmentConstants.IEMS_PROD_APIENGINE_BASE_URL, 
								 testEnvironmentConstants.IEMS_PROD_APIENGINE_PORT);
				
				setApiServiceTest(testEnvironmentConstants.IEMS_PROD_APISERVICE_BASE_URL, 
								  testEnvironmentConstants.IEMS_PROD_APISERVICE_PORT);
				
				setPreAsset(testEnvironmentConstants.IEMS_PRE_ASSET);
				setPreData(testEnvironmentConstants.IEMS_PRE_DATA);
				
				setRabbitMQHost(testEnvironmentConstants.IEMS_PROD_RABBITMQ_HOST);
				setRabbitMQPort(testEnvironmentConstants.IEMS_PROD_RABBITMQ_PORT);
				setRabbitMQUserName(testEnvironmentConstants.IEMS_PROD_RABBITMQ_USERNAME);
				setRabbitMQPassword(testEnvironmentConstants.IEMS_PROD_RABBITMQ_PASSWORD);
				setRabbitMQVirtualHost(testEnvironmentConstants.IEMS_PROD_RABBITMQ_VIRTUALHOST);
				setRabbitMQTimeout(testEnvironmentConstants.IEMS_PROD_RABBITMQ_TIMEOUT);
				setRabbitMQExchange(testEnvironmentConstants.IEMS_PROD_RABBITMQ_EXCHANGE);
				
				setEntityMgmtTest(testEnvironmentConstants.IEMS_PROD_ENTITY_MANAGEMENT_BASE_URL,
								  testEnvironmentConstants.IEMS_PROD_ENTITY_MANAGEMENT_PORT);
				
				setSubscriptionMgmtTest(testEnvironmentConstants.IEMS_PROD_SUBSCRIPTION_MANAGEMENT_BASE_URL,
										testEnvironmentConstants.IEMS_PROD_SUBSCRIPTION_MANAGEMENT_PORT);
			
				break;
				
			case ("snc-dev"):
				setConnectorTest(testEnvironmentConstants.SNC_DOMAIN_NAME,
								 testEnvironmentConstants.SNC_DEV_CONNECTOR_BASE_URL, 
								 testEnvironmentConstants.SNC_DEV_CONNECTOR_PORT);
			
				setApiEngineTest(testEnvironmentConstants.SNC_DEV_APIENGINE_BASE_URL, 
						 		 testEnvironmentConstants.SNC_DEV_APIENGINE_PORT);
				// 赋值：this.runUserQueryTest = true;
				setUserQueryTestClass("com.siemens.datalayer.snc.test.UserQueryTests");
				// 赋值：this.runEntityMgmtTest = true;
				setEntityMgmtTest(testEnvironmentConstants.SNC_DEV_ENTITY_MANAGEMENT_BASE_URL,
						  		  testEnvironmentConstants.SNC_DEV_ENTITY_MANAGEMENT_PORT);
				
				break;
			
			case ("snc-test"):
				setConnectorTest(testEnvironmentConstants.SNC_DOMAIN_NAME,
						 testEnvironmentConstants.SNC_TEST_CONNECTOR_BASE_URL, 
						 testEnvironmentConstants.SNC_TEST_CONNECTOR_PORT);
	
				setApiEngineTest(testEnvironmentConstants.SNC_TEST_APIENGINE_BASE_URL, 
						 		 testEnvironmentConstants.SNC_TEST_APIENGINE_PORT);
				
				setUserQueryTestClass("com.siemens.datalayer.snc.test.UserQueryTests");
				
				setEntityMgmtTest(testEnvironmentConstants.SNC_TEST_ENTITY_MANAGEMENT_BASE_URL,
						  		  testEnvironmentConstants.SNC_TEST_ENTITY_MANAGEMENT_PORT);	

				setPublishGraphAndCheckTest(true);
				setRunUiBackendTest(testEnvironmentConstants.SNC_TEST_UI_BACKEND_BASE_URL,
						            testEnvironmentConstants.SNC_TEST_UI_BACKEND_PORT,
						            "com.siemens.datalayer.uibackend.test.UiBackendTests",
						            testEnvironmentConstants.SNC_TEST_ENTITY_MANAGEMENT_PORT);
				break;
			
			case ("snc-prod"):
				setConnectorTest(testEnvironmentConstants.SNC_DOMAIN_NAME,
								 testEnvironmentConstants.SNC_PROD_CONNECTOR_BASE_URL, 
								 testEnvironmentConstants.SNC_PROD_CONNECTOR_PORT);
			
				setApiEngineTest(testEnvironmentConstants.SNC_PROD_APIENGINE_BASE_URL, 
						 		 testEnvironmentConstants.SNC_PROD_APIENGINE_PORT);
				
				setUserQueryTestClass("com.siemens.datalayer.snc.test.UserQueryTests");
				
				setEntityMgmtTest(testEnvironmentConstants.SNC_PROD_ENTITY_MANAGEMENT_BASE_URL,
						  		  testEnvironmentConstants.SNC_PROD_ENTITY_MANAGEMENT_PORT);
				
				break;
				
			case ("jinzu-dev"):
				setConnectorTest(testEnvironmentConstants.JINZU_DOMAIN_NAME,
								 testEnvironmentConstants.JINZU_DEV_CONNECTOR_BASE_URL, 
								 testEnvironmentConstants.JINZU_DEV_CONNECTOR_PORT);

				setConnectorConfigureTest(testEnvironmentConstants.JINZU_DEV_CONNECTOR_CONFIGURE_BASE_URL,
						                  testEnvironmentConstants.JINZU_DEV_CONNECTOR_CONFIGURE_PORT);
			
				setApiEngineTest(testEnvironmentConstants.JINZU_DEV_APIENGINE_BASE_URL, 
						 		 testEnvironmentConstants.JINZU_DEV_APIENGINE_PORT);
				// 赋值：this.runUserQueryTest = true;
				setUserQueryTestClass("com.siemens.datalayer.jinzu.test.UserQueryTests");
				// 赋值：this.runEntityMgmtTest = true;
				setEntityMgmtTest(testEnvironmentConstants.JINZU_DEV_ENTITY_MANAGEMENT_BASE_URL,
						  		  testEnvironmentConstants.JINZU_DEV_ENTITY_MANAGEMENT_PORT);

				// 赋值：this.runAuthForRestfulReadTest = true;
				setAuthForRestfulReadTestClass("com.siemens.datalayer.iot.test.AuthForRestfulReadTests");

				// 赋值：this.runAuthForWebserviceTest = true;
				setAuthForWebserviceTestClass("com.siemens.datalayer.iot.test.AuthForWebserviceTests");

				// 赋值：this.runConnectorConfigureTest = true;
				setConnectorConfigureTestClass("com.siemens.datalayer.connector.test.ConnectorConfigureTests");
				
				break;
			
			case ("jinzu-test"):
				setConnectorTest(testEnvironmentConstants.JINZU_DOMAIN_NAME,
						 testEnvironmentConstants.JINZU_TEST_CONNECTOR_BASE_URL, 
						 testEnvironmentConstants.JINZU_TEST_CONNECTOR_PORT);

				// 注释掉http请求的部分
				//				setApiEngineTest(testEnvironmentConstants.JINZU_TEST_APIENGINE_BASE_URL,
				//						 		 testEnvironmentConstants.JINZU_TEST_APIENGINE_PORT);

				//https请求
				setApiEngineHttpsTest(testEnvironmentConstants.JINZU_TEST_APIENGINE_HTTPS_BASE_URL,
						testEnvironmentConstants.JINZU_TEST_APIENGINE_HTTPS_PORT);

				setUserQueryTestClass("com.siemens.datalayer.jinzu.test.UserQueryTests");

				setEntityMgmtTest(testEnvironmentConstants.JINZU_TEST_ENTITY_MANAGEMENT_BASE_URL,
						  		  testEnvironmentConstants.JINZU_TEST_ENTITY_MANAGEMENT_PORT);

				// 赋值：this.runAppClientAuthenticationForK8sTest = true;
				setAppClientAuthenticationForK8sTestClass("com.siemens.datalayer.iot.test.AppClientAuthenticationForK8sTests");



				break;
			
			case ("jinzu-prod"):
				setConnectorTest(testEnvironmentConstants.JINZU_DOMAIN_NAME,
								 testEnvironmentConstants.JINZU_PROD_CONNECTOR_BASE_URL, 
								 testEnvironmentConstants.JINZU_PROD_CONNECTOR_PORT);
			
				setApiEngineTest(testEnvironmentConstants.JINZU_PROD_APIENGINE_BASE_URL, 
						 		 testEnvironmentConstants.JINZU_PROD_APIENGINE_PORT);
				
				setUserQueryTestClass("com.siemens.datalayer.jinzu.test.UserQueryTests");
				
				setEntityMgmtTest(testEnvironmentConstants.JINZU_PROD_ENTITY_MANAGEMENT_BASE_URL,
						  		  testEnvironmentConstants.JINZU_PROD_ENTITY_MANAGEMENT_PORT);
				
				break;

			case("iot-dev"):
				setConnectorTest(testEnvironmentConstants.IOT_DOMAIN_NAME,
						         testEnvironmentConstants.IOT_DEV_CONNECTOR_BASE_URL,
						         testEnvironmentConstants.IOT_DEV_CONNECTOR_PORT);

				setMongoDBHost(testEnvironmentConstants.IOT_DEV_MONGODB_HOST);
				setMongoDBPort(testEnvironmentConstants.IOT_DEV_MONGODB_PORT);
				setMongoDBUserName(testEnvironmentConstants.IOT_DEV_MONGODB_USERNAME);
				setMongoDBPassword(testEnvironmentConstants.IOT_DEV_MONGODB_PASSWORD);
				setMongoDBDatabaseName(testEnvironmentConstants.IOT_DEV_MONGODB_DATABASENAME);

				setConnectorRealtimeTest(testEnvironmentConstants.IOT_DEV_CONNECTOR_REALTIME_BASE_URL,
						testEnvironmentConstants.IOT_DEV_CONNECTOR_REALTIME_PORT);

				setConnectorConfigureTest(testEnvironmentConstants.IOT_DEV_CONNECTOR_CONFIGURE_BASE_URL,
						                  testEnvironmentConstants.IOT_DEV_CONNECTOR_CONFIGURE_PORT);

				setApiEngineTest(testEnvironmentConstants.IOT_DEV_APIENGINE_BASE_URL,
						         testEnvironmentConstants.IOT_DEV_APIENGINE_PORT);

				setRabbitMQHost(testEnvironmentConstants.IOT_DEV_RABBITMQ_HOST);
				setRabbitMQPort(testEnvironmentConstants.IOT_DEV_RABBITMQ_PORT);
				setRabbitMQUserName(testEnvironmentConstants.IOT_DEV_RABBIT_USERNAME);
				setRabbitMQPassword(testEnvironmentConstants.IOT_DEV_RABBIT_PASSWORD);
				setRabbitMQVirtualHost(testEnvironmentConstants.IOT_DEV_RABBIT_VIRTUALHOST);
				setRabbitMQTimeout(testEnvironmentConstants.IOT_DEV_RABBIT_TIMEOUT);
				setRabbitMQExchange(testEnvironmentConstants.IOT_DEV_RABBIT_EXCHANGE);

				// 赋值：this.runEntityMgmtTest = true;
				setEntityMgmtTest(testEnvironmentConstants.IOT_DEV_ENTITY_MANAGEMENT_BASE_URL,
						          testEnvironmentConstants.IOT_DEV_ENTITY_MANAGEMENT_PORT);

				// 赋值：this.runSubscriptionMgmtTest = true;
				setSubscriptionMgmtTest(testEnvironmentConstants.IOT_DEV_SUBSCRIPTION_MANAGEMENT_BASE_URL,
						                testEnvironmentConstants.IOT_DEV_SUBSCRIPTION_MANAGEMENT_PORT);

				// 赋值：this.runRelationalDatabaseTest = true;
				setRelationalDatabaseTestClass("com.siemens.datalayer.iot.test.RelationalDatabaseTests");
				setRelationalDatabaseTest(testEnvironmentConstants.IOT_DEV_DB_PROPERTIES);

				// 赋值：this.runConnectorRealtimeTest = true;
				setConnectorRealtimeTestClass("com.siemens.datalayer.connector.test.ConnectorRealtimeTests");

				// 赋值：this.runConnectorConfigureTest = true;
				setConnectorConfigureTestClass("com.siemens.datalayer.connector.test.ConnectorConfigureTests");

				break;

			case("iot-test"):
				setConnectorTest(testEnvironmentConstants.IOT_DOMAIN_NAME,
						testEnvironmentConstants.IOT_TEST_CONNECTOR_BASE_URL,
						testEnvironmentConstants.IOT_TEST_CONNECTOR_PORT);

				setApiEngineTest(testEnvironmentConstants.IOT_TEST_APIENGINE_BASE_URL,
						testEnvironmentConstants.IOT_TEST_APIENGINE_PORT);

				setRabbitMQHost(testEnvironmentConstants.IOT_TEST_RABBITMQ_HOST);
				setRabbitMQPort(testEnvironmentConstants.IOT_TEST_RABBITMQ_PORT);
				setRabbitMQUserName(testEnvironmentConstants.IOT_TEST_RABBIT_USERNAME);
				setRabbitMQPassword(testEnvironmentConstants.IOT_TEST_RABBIT_PASSWORD);
				setRabbitMQVirtualHost(testEnvironmentConstants.IOT_TEST_RABBIT_VIRTUALHOST);
				setRabbitMQTimeout(testEnvironmentConstants.IOT_TEST_RABBIT_TIMEOUT);
				setRabbitMQExchange(testEnvironmentConstants.IOT_TEST_RABBIT_EXCHANGE);

				// 赋值：this.runEntityMgmtTest = true;
				setEntityMgmtTest(testEnvironmentConstants.IOT_TEST_ENTITY_MANAGEMENT_BASE_URL,
						testEnvironmentConstants.IOT_TEST_ENTITY_MANAGEMENT_PORT);

				// 赋值：this.runSubscriptionMgmtTest = true;
				setSubscriptionMgmtTest(testEnvironmentConstants.IOT_TEST_SUBSCRIPTION_MANAGEMENT_BASE_URL,
						testEnvironmentConstants.IOT_TEST_SUBSCRIPTION_MANAGEMENT_PORT);

				// 赋值：this.runRelationalDatabaseTest = true;
				setRelationalDatabaseTestClass("com.siemens.datalayer.iot.test.RelationalDatabaseTests");
				setRelationalDatabaseTest(testEnvironmentConstants.IOT_TEST_DB_PROPERTIES);

				// 赋值：this.runWebServiceAsDataSourcesTest = true;
				setWebServiceAsDataSourcesTestClass("com.siemens.datalayer.iot.test.WebServiceAsDataSourcesTests");

				// 赋值：this.runRestfulAsDataSourcesTest = true;
				setRestfulAsDataSourcesTestClass("com.siemens.datalayer.iot.test.RestfulAsDataSourcesTests");

				// 赋值：this.runAuthForRestfulWriteTest = true
				setAuthForRestfulWriteTestClass("com.siemens.datalayer.iot.test.AuthForRestfulWriteTests");

				break;

			case("iot-prod"):
				setConnectorTest(testEnvironmentConstants.IOT_DOMAIN_NAME,
						testEnvironmentConstants.IOT_PROD_CONNECTOR_BASE_URL,
						testEnvironmentConstants.IOT_PROD_CONNECTOR_PORT);

				setApiEngineTest(testEnvironmentConstants.IOT_PROD_APIENGINE_BASE_URL,
						testEnvironmentConstants.IOT_PROD_APIENGINE_PORT);

				setRabbitMQHost(testEnvironmentConstants.IOT_PROD_RABBITMQ_HOST);
				setRabbitMQPort(testEnvironmentConstants.IOT_PROD_RABBITMQ_PORT);
				setRabbitMQUserName(testEnvironmentConstants.IOT_PROD_RABBIT_USERNAME);
				setRabbitMQPassword(testEnvironmentConstants.IOT_PROD_RABBIT_PASSWORD);
				setRabbitMQVirtualHost(testEnvironmentConstants.IOT_PROD_RABBIT_VIRTUALHOST);
				setRabbitMQTimeout(testEnvironmentConstants.IOT_PROD_RABBIT_TIMEOUT);
				setRabbitMQExchange(testEnvironmentConstants.IOT_PROD_RABBIT_EXCHANGE);

				// 赋值：this.runEntityMgmtTest = true;
				setEntityMgmtTest(testEnvironmentConstants.IOT_PROD_ENTITY_MANAGEMENT_BASE_URL,
						testEnvironmentConstants.IOT_PROD_ENTITY_MANAGEMENT_PORT);

				// 赋值：this.runSubscriptionMgmtTest = true;
				setSubscriptionMgmtTest(testEnvironmentConstants.IOT_PROD_SUBSCRIPTION_MANAGEMENT_BASE_URL,
						testEnvironmentConstants.IOT_PROD_SUBSCRIPTION_MANAGEMENT_PORT);

				// 赋值：this.runRelationalDatabaseTest = true;
				setRelationalDatabaseTestClass("com.siemens.datalayer.iot.test.RelationalDatabaseTests");
				setRelationalDatabaseTest(testEnvironmentConstants.IOT_PROD_DB_PROPERTIES);

			case("databrain-dev"):
				setConnectorTest(testEnvironmentConstants.DATABRAIN_DOMAIN_NAME,
						testEnvironmentConstants.DATABRAIN_DEV_CONNECTOR_BASE_URL,
						testEnvironmentConstants.DATABRAIN_DEV_CONNECTOR_PORT);

				setApiEngineTest(testEnvironmentConstants.DATABRAIN_DEV_APIENGINE_BASE_URL,
						testEnvironmentConstants.DATABRAIN_DEV_APIENGINE_PORT);

				// 赋值：this.runDesigoCCTest = true;
				setDesigoCCTestClass("com.siemens.datalayer.databrain.test.DesigoCCTests");

				break;
			
			default:
				System.out.println("Error: Unknown test environment '" + testEnvName + "'!");	
				return false;
		}
		
		setConfigName(testEnvName); // private String configName = testEnvName，如iems-dev
		return true;
	}


}
