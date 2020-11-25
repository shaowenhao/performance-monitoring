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
	  
	  checkResponseCode(paramMaps, response);  
	  
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
	  }
	  else 
	  {
		  Assert.assertNull(response.jsonPath().getList("data"));
	  }

  	}
	
	public void checkResponseCode(Map<String, String> paramMaps, Response response)
	{
		  int expStatusCode = 200;	// If not specified, the expected status code is set to 200 (OK)
		  if (paramMaps.containsKey("rspStatus")) expStatusCode = Integer.valueOf(paramMaps.get("rspStatus")).intValue();
		  Assert.assertEquals(response.getStatusCode(), expStatusCode, "The status code in response message matches the expected value.");
		  
		  if ((paramMaps.containsKey("rspCode")))
		  {
			  Assert.assertEquals(response.jsonPath().getString("code"), paramMaps.get("rspCode"));
		  }
		  else
		  {
			  if (paramMaps.get("description").contains("good request")) 
				  Assert.assertEquals(response.jsonPath().getString("code"), "0");
			  else
				  System.out.println("No error code is specified for test case： " + paramMaps.get("test-id"));
		  }		  
		  
		  if (paramMaps.containsKey("rspMessage"))
		  {
			  Assert.assertTrue(response.jsonPath().getString("message").contains(paramMaps.get("rspMessage")));
		  }
		  else
		  {
			  if (paramMaps.get("description").contains("good request")) 
				  Assert.assertEquals(response.jsonPath().getString("message"), "Operate success.");
			  else
				  System.out.println("No response message is specified for test case： " + paramMaps.get("test-id"));
		  }
	}
	
	public void checkDataContainsSpecifiedFields(String fieldsContent, List<HashMap<String, String>> rspDataList)
	{	  
		Scanner scanner = new Scanner(fieldsContent);
		scanner.useDelimiter(",");
		  
		while (scanner.hasNext())
		{
			String keyToCompare = scanner.next();
			  
			for (HashMap<String, String> rspDataItem: rspDataList)
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
	
	public static boolean checkDataIsSorted(String orderContent, List<HashMap<String, String>> rspDataList)
	{
		Scanner scanner = new Scanner(orderContent);
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
				System.out.println("Check if the field '" + keyForOrder + "' is used for ordering");
				System.out.println("Sort order is " + sortOrder);

				result = isMapSortedByKey(rspDataList, keyForOrder, sortOrder);
				break; // only check the 1st valid order parameter
			}
		}	
		  
		scanner.close();
		
		return result;
	}
	
}
