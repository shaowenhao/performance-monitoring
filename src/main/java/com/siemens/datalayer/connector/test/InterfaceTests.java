package com.siemens.datalayer.connector.test;

import java.util.*;
import java.util.Scanner;

import io.qameta.allure.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.connector.model.*;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;

import org.testng.Assert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("SDL Connector")
@Feature("Rest API")
public class InterfaceTests {
	
	@Parameters({"base_url", "port", "domain_name"})
	@BeforeClass (description = "Configure the host address and communication port of Connector Interface")
	public void setConnectorEndpoint(@Optional("http://localhost") String base_url, @Optional("9001") String port, String domain_name) {
	    Endpoint.setBaseUrl(base_url);
	    Endpoint.setPort(port);
	    Endpoint.setDomain(domain_name);
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("Connector Address", base_url + ":" + port);
	}
	 
	@Test (priority = 0, description = "Test connector interface: Get All Entities name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'Get All Entities name' request to read out all the available entity names.")
	@Story("Connctor Interface: Get all entities name")
	public void GetAllEntitiesName()
	{	
	  Response response = Endpoint.getAllEntitiesName();
	  
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
	  Response response = Endpoint.getAllEntitiesName();

	  JsonPath jsonPathEvaluator = response.jsonPath();
	 
	  List<String> allEntities = jsonPathEvaluator.getList("data");
		  
	  response = Endpoint.searchModelSchemaByName("default", allEntities.get(0));
	
	  // This is an example of extracting information from the response message via Json path  
	  Assert.assertEquals("Operate success.", response.jsonPath().getString("message"));
		
  	}
  
	@Test (priority = 0, description = "Test connector interface: Get all entities name and then check its concept model.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'Get All Entities name' request to get all the available entity names, then read out the model schema of every entity.")
	@Story("Entity Interface: Search model schema by name")
  	public void SearchModelSchemaForAllEntities()
  	{		
	  Response response = Endpoint.getAllEntitiesName();
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
	  JsonPath jsonPathEvaluator = response.jsonPath();

	  List<String> allEntities = jsonPathEvaluator.getList("data");
	  
	  for(String entityItem : allEntities)
	  {
		  response = Endpoint.searchModelSchemaByName("default", entityItem);
		  
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
	  
	  Response response = Endpoint.getConceptModelDataByCondition(queryParameters);
	  
	  checkResponseCode(paramMaps, response.getStatusCode(), response.jsonPath().getString("code"), response.jsonPath().getString("message"));  
	  
	  if (paramMaps.get("description").contains("data retrieved"))
	  {	  
		  List<HashMap<String, String>> rspDataList;
		  
		  if (response.getBody().asString().contains("totalPages"))
			  rspDataList = response.jsonPath().getList("data.data");
		  else
			  rspDataList = response.jsonPath().getList("data");
		  
		  Assert.assertTrue(rspDataList.size() > 0);
		  
		  if (paramMaps.containsKey("fields")) 
		  {
			  if (paramMaps.get("fields").contains("*"))
				  checkDataFollowsModelSchema(paramMaps.get("name"), response);
			  else
				  checkDataContainsSpecifiedFields(paramMaps.get("fields"), rspDataList);
		  }
		  else
		  {
			  checkDataFollowsModelSchema(paramMaps.get("name"), response);
		  }
		  
		  if (paramMaps.containsKey("order"))
		  {
			  Assert.assertTrue(checkDataIsSorted(paramMaps.get("order"), rspDataList));
		  }
		  
		  String pageIndex = "noInput", pageSize = "noInput";
		  if (paramMaps.containsKey("pageIndex")) pageIndex = String.valueOf(paramMaps.get("pageIndex"));
		  if (paramMaps.containsKey("pageSize")) pageSize = String.valueOf(paramMaps.get("pageSize"));
		  checkPaginationFormat(pageIndex, pageSize, response);
	  }
	  else 
	  {
		  Assert.assertNull(response.jsonPath().getList("data"));
	  }

  	}
	
	@Step("Verify the response code and message")
	public void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode, String actualMessage)
	{
		  int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
		  if (requestParameters.containsKey("rspStatus")) expStatusCode = Integer.valueOf(requestParameters.get("rspStatus")).intValue();
		  Assert.assertEquals(actualStatusCode, expStatusCode, "The status code in response message matches the expected value.");
		  
		  if ((requestParameters.containsKey("rspCode")))
		  {
			  Assert.assertEquals(actualCode, requestParameters.get("rspCode"));
		  }
		  else
		  {
			  if (requestParameters.get("description").contains("good request")) 
				  Assert.assertEquals(actualCode, "0");
			  else
				  System.out.println("No error code is specified for test case： " + requestParameters.get("test-id"));
		  }		  
		  
		  if (requestParameters.containsKey("rspMessage"))
		  {
			  Assert.assertTrue(actualMessage.contains(requestParameters.get("rspMessage")));
		  }
		  else
		  {
			  if (requestParameters.get("description").contains("good request")) 
				  Assert.assertEquals(actualMessage, "Operate success.");
			  else
				  System.out.println("No response message is specified for test case： " + requestParameters.get("test-id"));
		  }
	}
	
	@Step("Verify if the response contains the required data fields")
	public void checkDataContainsSpecifiedFields(String fields, List<HashMap<String, String>> responseData)
	{	  
		Scanner scanner = new Scanner(fields);
		scanner.useDelimiter(",");
		  
		while (scanner.hasNext())
		{
			String keyToCompare = scanner.next();
			  
			for (HashMap<String, String> rspDataItem: responseData)
			{
				assertThat(rspDataItem, hasKey(keyToCompare));
			}	
		}	
		  
		scanner.close();
	}
	
	public void checkDataFollowsModelSchema(String schemaName, Response response)
	{
		String schemaTemplateFile = Endpoint.getResourcePath() + "JasonModelSchemaFor" + schemaName;
		
		// If data is returned in pagination format
		if (response.getBody().asString().contains("totalPages")) schemaTemplateFile += "P";
		schemaTemplateFile += ".JSON";
		
		verifyIfDataMatchesJsonSchemaTemplate(schemaTemplateFile, response);
	}
	
	@Step("Verify if the data in response message matches the correct model schema")
	public void verifyIfDataMatchesJsonSchemaTemplate(String schemaTemplateFile, Response response)
	{
		assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath(schemaTemplateFile));
	}
	
	public static boolean isIntegerStr(String input) 
	{
	    try 
	    {
	        Integer.parseInt(input);
	        return true;
	    }
	    catch(Exception e) 
	    {
	        return false;
	    }
	}
	
	public static boolean isFloatStr(String input) 
	{
	    try 
	    {
	        Float.parseFloat(input);
	        return true;
	    }
	    catch(Exception e) 
	    {
	        return false;
	    }
	}
	
	public static boolean compareOrderFieldValue(String valueShouldBeSmall, String valueShouldBeLarge)
	{       
		if (isIntegerStr(valueShouldBeSmall)) // Compare integer values
		{
			if (Integer.parseInt(valueShouldBeSmall) > Integer.parseInt(valueShouldBeLarge)) return false;
		}
		else if (isFloatStr(valueShouldBeSmall)) // Compare float values
		{
			if (Float.parseFloat(valueShouldBeSmall) > Float.parseFloat(valueShouldBeLarge)) return false;
		}
		else // Compare string in alphabetical order
		{
			if (valueShouldBeLarge.compareTo(valueShouldBeSmall) < 0) return false;
		}
			
		return true;        	
	}
	
	public static boolean isMapSortedByKey(List<HashMap<String, String>> listOfHashMaps, String key, String order) 
	{
	    if (listOfHashMaps.isEmpty() || listOfHashMaps.size() == 1) return true;
	 
	    Iterator<HashMap<String, String>> iter = listOfHashMaps.iterator();
	    HashMap<String, String> current, previous = iter.next();
	    
	    while (iter.hasNext()) 
	    {
	        current = iter.next();
	        
	        String valueShouldBeSmall, valueShouldBeLarge;
	        if (order.equals("ascending"))
	        {
	        	valueShouldBeSmall = String.valueOf(previous.get(key));
	        	valueShouldBeLarge = String.valueOf(current.get(key));
	        }
	        else
	        {
	        	valueShouldBeSmall = String.valueOf(current.get(key));
	        	valueShouldBeLarge = String.valueOf(previous.get(key));
	        }
	        
	        if (!compareOrderFieldValue(valueShouldBeSmall, valueShouldBeLarge)) return false;

	        previous = current;
	    }
	    
	    return true;
	}
	
	@Step("Verify if the data list in response message is sorted by the specified order parameters")
	public static boolean checkDataIsSorted(String inputOrderParameters, List<HashMap<String, String>> rspDataList)
	{
		Scanner scanner = new Scanner(inputOrderParameters);
		scanner.useDelimiter(",");
		
		boolean result = true;
		  
		while (scanner.hasNext())
		{
			String keyForOrder = scanner.next();
			String sortOrder = "ascending";
			
			if (keyForOrder.contains("+")) keyForOrder = keyForOrder.replace("+", "");
			
			if (keyForOrder.contains("-"))
			{
				keyForOrder = keyForOrder.replace("-", "");
				sortOrder = "descending";
			}
			  
			if (rspDataList.toString().contains(keyForOrder))
			{
//				System.out.println("Check if the field '" + keyForOrder + "' is used for ordering");
//				System.out.println("Sort order is " + sortOrder);

				result = isMapSortedByKey(rspDataList, keyForOrder, sortOrder);
				break; // only check the 1st valid order parameter
			}
		}	
		  
		scanner.close();
		
		return result;
	}
	
	@Step("Verify if the data pagination format is correct")
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
			
			if ((!pageIndex.equals("noInput")) && (isIntegerStr(pageIndex)))
			{
				pageIndexInput = Integer.parseInt(pageIndex);
				if (pageIndexInput<=0) pageIndexInput = 1;
				if (pageSize.equals("noInput")) pageSizeInput = 20;
			}
			
			if ((!pageSize.equals("noInput")) && (isIntegerStr(pageSize)))
			{
				pageSizeInput = Integer.parseInt(pageSize);
				if (pageSizeInput<=0) pageSizeInput = 20;
				if (pageIndex.equals("noInput")) pageIndexInput = 1;
			}
			
//			System.out.println("Check pagination format: pageIndex=" + pageIndexInput + ", pageSize=" + pageSizeInput);
			checkPageIndexAndPageSize(pageIndexInput, pageSizeInput, response);
		}
	}
	
	public static void checkPageIndexAndPageSize(int expPageIndex, int expPageSize, Response response)
	{
		int actualPageIndex = response.jsonPath().get("data.pageIndex");
		int actualPageSize = response.jsonPath().get("data.pageSize");
//		System.out.println("Actual pagination format: pageIndex=" + actualPageIndex + ", pageSize=" + actualPageSize);
		
		Assert.assertTrue(actualPageIndex==expPageIndex, "The page index is correct.");
		Assert.assertTrue(actualPageSize==expPageSize, "The page size is correct.");
		
		boolean isFirstPage = response.jsonPath().get("data.first");
		boolean isLastPage = response.jsonPath().get("data.last");
		
		if (expPageIndex==1) 
			Assert.assertTrue(isFirstPage, "The mark of first page is set.");
		else
			Assert.assertTrue(!isFirstPage, "The mark of first page is not set.");
		
		if (expPageIndex==response.jsonPath().getShort("data.totalPages")) 
			Assert.assertTrue(isLastPage, "The mark of last page is set.");
		else
			Assert.assertTrue(!isLastPage, "The mark of last page is not set.");
	}
}

