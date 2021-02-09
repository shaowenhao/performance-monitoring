package com.siemens.datalayer.jinzu.test;

import io.qameta.allure.*;
import io.restassured.response.Response;

import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.apiengine.test.ApiEngineEndpoint;
import com.siemens.datalayer.apiengine.test.QueryEndPointTests;
import com.siemens.datalayer.utils.CommonCheckFunctions;
import com.siemens.datalayer.utils.ExcelDataProviderClass;

@Epic("User Query Scenarios")
public class UserQueryTests {
	
	@Parameters({"base_url", "port"})
	@BeforeClass (description = "Configure the host address and communication port of data-layer-api-engine")
	public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("31059") String port) 
	{
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
	}

    @Test(priority = 0, description = "查看某个租赁物的详细信息",
    	  dataProvider = "api-engine-test-data-provider", 
    	  dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("租赁物查询")
    @Story("详细信息查询")
    public void getLeaseDetails(Map<String, String> paramMaps) 
    {
    	sendQueryAndCheckResults(paramMaps);
    }

    @Test(priority = 0, description = "根据project id获取租赁物信息列表",
    	  dataProvider = "api-engine-test-data-provider", 
      	  dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("租赁物查询")
    @Story("查询租赁物列表")
    public void getLeaseList(Map<String, String> paramMaps) 
    {
    	sendQueryAndCheckResults(paramMaps);
    }

    @Test(priority = 0, description = "获取指定项目的详细信息",
    	  dataProvider = "api-engine-test-data-provider", 
      	  dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("项目查询")
    @Story("获取项目详细信息")
    public void getProjectDetails(Map<String, String> paramMaps) 
    {
    	sendQueryAndCheckResults(paramMaps);
    }

    @Test(priority = 0, description = "通过项目类型对项目进行过滤",
    	  dataProvider = "api-engine-test-data-provider", 
      	  dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("项目查询")
    @Story("项目信息的按条件查询")
    public void getProjectByCondition(Map<String, String> paramMaps) 
    {
    	sendQueryAndCheckResults(paramMaps);
    }

    @Test(priority = 0, description = "查看电站的逆变器信息",
    	  dataProvider = "api-engine-test-data-provider", 
      	  dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("电站查询")
    @Story("查询电站的详细信息")
    public void getSiteDetails(Map<String, String> paramMaps) 
    {
    	sendQueryAndCheckResults(paramMaps);
    }
    
    public static void sendQueryAndCheckResults(Map<String, String> paramMaps)
    {
    	if (paramMaps.containsKey("queryString"))
        {
        	Response response = ApiEngineEndpoint.postGraphql(paramMaps.get("queryString"));
        	
        	QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
        	
        	if (paramMaps.containsKey("jasonTemplate"))
        		CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate("json-model-schema/jinzu/" + paramMaps.get("jasonTemplate"), response.getBody().asString());
        }    
    }
    
}
