package com.siemens.datalayer.snc.test;

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
	public void setApiEngineEndpoint(@Optional("http://140.231.89.85") String base_url, @Optional("30169") String port) 
	{
        ApiEngineEndpoint.setBaseUrl(base_url);
        ApiEngineEndpoint.setPort(port);
	}

    @Test(priority = 0, description = "查看生产订单信息",
    	  dataProvider = "api-engine-test-data-provider", 
    	  dataProviderClass = ExcelDataProviderClass.class)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("生产订单查询")
    @Story("详细信息查询")
    public void queryProductOrderByGraphQL(Map<String, String> paramMaps) 
    {
    	sendQueryAndCheckResults(paramMaps);
    }
    
    @Test(priority = 0, description = "查看物料信息",
      	  dataProvider = "api-engine-test-data-provider", 
      	  dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Feature("物料信息查询")
	@Story("详细信息查询")
	public void queryMaterialByGraphQL(Map<String, String> paramMaps) 
	{
    	sendQueryAndCheckResults(paramMaps);
	}
    
    @Test(priority = 0, description = "查看工作中心信息",
        	  dataProvider = "api-engine-test-data-provider", 
        	  dataProviderClass = ExcelDataProviderClass.class)
  	@Severity(SeverityLevel.BLOCKER)
  	@Feature("工作中心信息查询")
  	@Story("详细信息查询")
  	public void queryWorkCenterByGraphQL(Map<String, String> paramMaps) 
  	{
      	sendQueryAndCheckResults(paramMaps);
  	}
    
    @Test(priority = 0, description = "查看排程信息",
        	  dataProvider = "api-engine-test-data-provider", 
        	  dataProviderClass = ExcelDataProviderClass.class)
  	@Severity(SeverityLevel.BLOCKER)
  	@Feature("排程信息查询")
  	@Story("详细信息查询")
  	public void queryPreactorOrderByGraphQL(Map<String, String> paramMaps) 
  	{
      	sendQueryAndCheckResults(paramMaps);
  	}  
    
    public static void sendQueryAndCheckResults(Map<String, String> paramMaps)
    {
    	if (paramMaps.containsKey("query"))
        {
        	Response response = ApiEngineEndpoint.postGraphql(paramMaps.get("query"));
        	
        	QueryEndPointTests.checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));
        	
        	if (paramMaps.containsKey("schema"))
        		CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate("json-model-schema/snc/" + paramMaps.get("schema"), response.getBody().asString());
        }    
    }

}
