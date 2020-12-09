package com.siemens.datalayer.connector.test;

import java.util.*;

import io.qameta.allure.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.connector.model.*;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.siemens.datalayer.utils.CommonCheckFunctions;
import com.siemens.datalayer.utils.ExcelDataProviderClass;

import org.testng.Assert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Epic("SDL Connector")
@Feature("Rest API")
public class InterfaceTests {
	
	@Parameters({"base_url", "port", "domain_name"})
	@BeforeClass (description = "Configure the host address and communication port of data-layer-connector")
	public void setConnectorEndpoint(@Optional("http://localhost") String base_url, @Optional("9001") String port, String domain_name) {
	    ConnectorEndpoint.setBaseUrl(base_url);
	    ConnectorEndpoint.setPort(port);
	    ConnectorEndpoint.setDomain(domain_name);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("data-layer-connector", base_url + ":" + port);
	}
	 
	@Test (priority = 0, description = "Test connector interface: Get All Entities name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'Get All Entities name' request to read out all the available entity names.")
	@Story("Connctor Interface: Get all entities name")
	public void GetAllEntitiesName()
	{	
	  Response response = ConnectorEndpoint.getAllEntitiesName();
	  
	  // This is an example of processing the response message via POJO  
	  GetAllEntitiesNameResponse rspBody = response.getBody().as(GetAllEntitiesNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());

	}
  
	@Test (priority = 0, description = "Test entity interface: Get concept model definition by model name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'searchModelSchemaByName' request to read out the model schema of the entity specified in the name parameter.")
	@Story("Entity Interface: Search model schema by name")
  	public void SearchModelSchemaByName()
  	{
	  Response response = ConnectorEndpoint.getAllEntitiesName();

	  JsonPath jsonPathEvaluator = response.jsonPath();
	 
	  List<String> allEntities = jsonPathEvaluator.getList("data");
		  
	  response = ConnectorEndpoint.searchModelSchemaByName("default", allEntities.get(0));
	
	  // This is an example of extracting information from the response message via Json path  
	  Assert.assertEquals("Operate success.", response.jsonPath().getString("message"));
		
  	}
  
	@Test (priority = 0, description = "Test connector interface: Get all entities name and then check its concept model.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'Get All Entities name' request to get all the available entity names, then read out the model schema of every entity.")
	@Story("Entity Interface: Search model schema by name")
  	public void SearchModelSchemaForAllEntities()
  	{		
	  Response response = ConnectorEndpoint.getAllEntitiesName();
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
	  JsonPath jsonPathEvaluator = response.jsonPath();

	  List<String> allEntities = jsonPathEvaluator.getList("data");
	  
	  for(String entityItem : allEntities)
	  {
		  response = ConnectorEndpoint.searchModelSchemaByName("default", entityItem);
		  
		  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  }
  	}
  
	@Test (	priority = 0, 
			description = "Test connector interface: Get concept model data by condition.", 
			dataProvider = "connector-test-data-provider", 
			dataProviderClass = ExcelDataProviderClass.class)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'getConceptModelDataByCondition' request with specified parameters and check the response message.")
	@Story("Connector Interface: Get concept model data by condition")
  	public void getConceptModelDataByCondition(Map<String, String> paramMaps)
  	{  
	  HashMap<String, String> queryParameters = new HashMap<>();
	  
	  if (paramMaps.containsKey("condition")) 		queryParameters.put("condition", paramMaps.get("condition"));  
	  if (paramMaps.containsKey("domainName")) 		queryParameters.put("domainName", paramMaps.get("domainName"));
	  if (paramMaps.containsKey("name")) 			queryParameters.put("name", paramMaps.get("name"));
	  if (paramMaps.containsKey("fields")) 			queryParameters.put("fields", paramMaps.get("fields"));
	  if (paramMaps.containsKey("order")) 			queryParameters.put("order", paramMaps.get("order"));
	  if (paramMaps.containsKey("pageIndex")) 		queryParameters.put("pageIndex", paramMaps.get("pageIndex"));
	  if (paramMaps.containsKey("pageSize")) 		queryParameters.put("pageSize", paramMaps.get("pageSize"));
	  if (paramMaps.containsKey("timeout")) 		queryParameters.put("pageSize", paramMaps.get("timeout")); 
	  
	  Response response = ConnectorEndpoint.getConceptModelDataByCondition(queryParameters);
	  
	  checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));  
	  
	  if (paramMaps.get("description").contains("data retrieved"))
	  {	  
		  List<HashMap<String, String>> rspDataList;
		  
		  if (response.getBody().asString().contains("totalPages"))
			  rspDataList = response.jsonPath().getList("data.data");
		  else
			  rspDataList = response.jsonPath().getList("data");
		  
		  // Check if the returned data list is not empty
		  Assert.assertTrue(rspDataList.size() > 0);
		  
		  if (paramMaps.containsKey("fields")) 
		  {
			  if (paramMaps.get("fields").contains("*"))
				  checkDataFollowsModelSchema(paramMaps.get("name"), response);
			  else
				  // Check if data contains the required fields
				  CommonCheckFunctions.checkDataContainsSpecifiedFields(paramMaps.get("fields"), rspDataList);
		  }
		  else
		  {
			  // Check if data entry matches the model schema
			  checkDataFollowsModelSchema(paramMaps.get("name"), response);
		  }
		  
		  if (paramMaps.containsKey("order"))
		  {
			  // Check if the returned data list is sorted by specified order parameters
			  Assert.assertTrue(CommonCheckFunctions.checkDataIsSorted(paramMaps.get("order"), rspDataList));
		  }
		  
		  String pageIndex = "noInput", pageSize = "noInput";
		  if (paramMaps.containsKey("pageIndex")) pageIndex = String.valueOf(paramMaps.get("pageIndex"));
		  if (paramMaps.containsKey("pageSize")) pageSize = String.valueOf(paramMaps.get("pageSize"));
		  
		  // Check if the pagination format is correct
		  checkPaginationFormat(pageIndex, pageSize, response);
	  }
	  else 
	  {
		  Assert.assertNull(response.jsonPath().getList("data"));
	  }

  	}
	
	@Step("Verify the status code, operation code, and message")
	public static void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode, String actualMessage)
	{
		int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
		if (requestParameters.containsKey("rspStatus")) expStatusCode = Integer.valueOf(requestParameters.get("rspStatus")).intValue();
		Assert.assertEquals(actualStatusCode, expStatusCode, "The status code in response message matches the expected value.");
		  
		if ((requestParameters.containsKey("rspCode")))
		{
			Assert.assertEquals(actualCode, requestParameters.get("rspCode"), "The operation code in response message matches the expected value.");
		}
		else
		{
			if (requestParameters.get("description").contains("good request")) 
				Assert.assertEquals(actualCode, "0", "The operation code in response message matches the expected value.");
			else
				System.out.println("Operation code is not specified for test case： " + requestParameters.get("test-id"));
		}		  
		  
		if (requestParameters.containsKey("rspMessage"))
		{
			Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")), "The operation message contains the expected content.");
		}
		else
		{
			if (requestParameters.get("description").contains("good request")) 
				Assert.assertEquals(actualMessage, "Operate success.", "The message of 'operation success' is returned.");
			else
				System.out.println("Operation message is not specified for test case： " + requestParameters.get("test-id"));
		}
	}
	
	public void checkDataFollowsModelSchema(String schemaName, Response response)
	{
		String schemaTemplateFile = ConnectorEndpoint.getResourcePath() + "JasonModelSchemaFor" + schemaName;
		
		// If data is returned in pagination format
		if (response.getBody().asString().contains("totalPages")) schemaTemplateFile += "P";
		schemaTemplateFile += ".JSON";
		
		CommonCheckFunctions.verifyIfDataMatchesJsonSchemaTemplate(schemaTemplateFile, response.getBody().asString());
	}
	
	public static void checkPaginationFormat(String pageIndex, String pageSize, Response response)
	{
		if (((pageIndex.equals("noInput")) && (pageSize.equals("noInput")))	||
			((pageIndex.equals("noInput")) && (pageSize.equals("0"))) 	 	||	
			((pageIndex.equals("0")) && (pageSize.equals("noInput")))	 	||
			((pageIndex.equals("0")) && (pageSize.equals("0"))))
		{
			// Data is not in pagination format
			Assert.assertTrue((response.getBody().asString().contains("totalPages"))==false, "Data pagination is not used");
		}
		else 
		{
			Assert.assertTrue(response.getBody().asString().contains("totalPages"), "Data is in pagination format");
			
			int pageIndexInput=0, pageSizeInput=0;
			
			if ((!pageIndex.equals("noInput")) && (CommonCheckFunctions.isIntegerStr(pageIndex)))
			{
				pageIndexInput = Integer.parseInt(pageIndex);
				if (pageIndexInput<=0) pageIndexInput = 1;
				if (pageSize.equals("noInput")) pageSizeInput = 20;
			}
			
			if ((!pageSize.equals("noInput")) && (CommonCheckFunctions.isIntegerStr(pageSize)))
			{
				pageSizeInput = Integer.parseInt(pageSize);
				if (pageSizeInput<=0) pageSizeInput = 20;
				if (pageIndex.equals("noInput")) pageIndexInput = 1;
			}
			
			checkPageIndexAndPageSize(pageIndexInput, pageSizeInput, response);
		}
	}
	
	@Step("Verify if the data pagination format is correct")
	public static void checkPageIndexAndPageSize(int expectPageIndex, int expectPageSize, Response response)
	{
		int actualPageIndex = response.jsonPath().get("data.pageIndex");
		int actualPageSize = response.jsonPath().get("data.pageSize");
		
		Assert.assertTrue(actualPageIndex==expectPageIndex, "The page index is correct.");
		Assert.assertTrue(actualPageSize==expectPageSize, "The page size is correct.");
		
		boolean isFirstPage = response.jsonPath().get("data.first");
		boolean isLastPage = response.jsonPath().get("data.last");
		
		if (expectPageIndex==1) 
			Assert.assertTrue(isFirstPage, "The mark of first page is set.");
		else
			Assert.assertTrue(!isFirstPage, "The mark of first page is not set.");
		
		if (expectPageIndex==response.jsonPath().getShort("data.totalPages")) 
			Assert.assertTrue(isLastPage, "The mark of last page is set.");
		else
			Assert.assertTrue(!isLastPage, "The mark of last page is not set.");
	}
}

