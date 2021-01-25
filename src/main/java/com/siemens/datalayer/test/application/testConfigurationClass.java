package com.siemens.datalayer.test.application;

public class testConfigurationClass {

	private String configName = null;
	private String domainName = null;
	
	private String connector_base_url = null;
	private String connector_port = null;
	
	private String apiEngine_base_url = null;
	private String apiEngine_port = null;
	
	Boolean runApiServiceTest = false;
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
	
	Boolean runEntityMgmtTest = false;
	private String entityManagement_base_url = null;
	private String entityManagement_port = null;	
	
	Boolean runSubscriptionMgmtTest = false;
	private String subscriptionManagement_base_url = null;
	private String subscriptionManagement_port = null;	
	
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
	
	public String getSubscriptionManagementBaseURL() {
		return this.subscriptionManagement_base_url;
	}
	
	public String getSubscriptionManagementPort() {
		return this.subscriptionManagement_port;
	}
	
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
				
				setEntityMgmtTest(testEnvironmentConstants.IEMS_DEV_ENTITY_MANAGEMENT_BASE_URL,
								  testEnvironmentConstants.IEMS_DEV_ENTITY_MANAGEMENT_PORT);			
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
			
				break;
			
			default:
				System.out.println("Error: Unknown test environment '" + testEnvName + "'!");	
				return false;
		}
		
		setConfigName(testEnvName);
		return true;
	}
	
}
