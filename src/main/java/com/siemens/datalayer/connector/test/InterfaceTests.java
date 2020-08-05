package com.siemens.datalayer.connector.test;

import java.util.*;

import io.qameta.allure.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.siemens.datalayer.connector.model.GetAllEntitiesNameResponse;
import com.siemens.datalayer.connector.model.SearchModelSchemaByNameResponse;

import org.testng.Assert;
import org.testng.Reporter;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Regression Tests")
@Feature("Connector Rest API Tests")
public class InterfaceTests {
	
	@Parameters({"base_url", "port"})
	@BeforeClass
	public void setConnectorEndpoint(@Optional("http://localhost") String base_url, @Optional("9001") String port) {
	    Endpoint.setBaseUrl(base_url);
	    Endpoint.setPort(port);
	}
	
	@DataProvider(name = "dataForGetConceptModelDataByCondition")
	Iterator<Object[]> dataForGetConceptModelDataByCondition() {

	    Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

	    List<Map<String, String>> listOfQueryParams = new ArrayList<Map<String, String>>();
	    
	    Map<String, String> goodQuery01 = new HashMap<String, String>();

	    goodQuery01.put("description", "good request, data retrieved");
	    goodQuery01.put("name", "Customer");
	    goodQuery01.put("pageIndex", "1");
	    goodQuery01.put("pageSize", "10");
	    
	    listOfQueryParams.add(goodQuery01);	
	    
	    Map<String, String> goodQuery02 = new HashMap<String, String>();

	    goodQuery02.put("description", "good request, data retrieved");
	    goodQuery02.put("name", "Site");
	    goodQuery02.put("pageIndex", "2");
	    goodQuery02.put("pageSize", "15");
	    
	    listOfQueryParams.add(goodQuery02);
	    
	    Map<String, String> goodQuery03 = new HashMap<String, String>();
	    
	    goodQuery03.put("description", "good request, data retrieved");	    
	    goodQuery03.put("name", "Site");
	    goodQuery03.put("condition", "id='P000000681'");
   
	    listOfQueryParams.add(goodQuery03);
	    
	    Map<String, String> goodQuery04 = new HashMap<String, String>();
	    
	    goodQuery04.put("description", "good request, data not found");	    
	    goodQuery04.put("name", "Site");
	    goodQuery04.put("condition", "id='?????'");
   
	    listOfQueryParams.add(goodQuery04);
	    
	    Map<String, String> goodQuery05 = new HashMap<String, String>();
	    
	    goodQuery05.put("description", "good request, data retrieved");	    
	    goodQuery05.put("name", "Site");
	    goodQuery05.put("condition", "capacity>=20");
   
	    listOfQueryParams.add(goodQuery05);	    
	    
	    Map<String, String> badQuery01 = new HashMap<String, String>();
	    
	    badQuery01.put("description", "bad request (name is empty)");
	    badQuery01.put("name", "");
	    badQuery01.put("pageIndex", "1");
	    badQuery01.put("pageSize", "10");
	    badQuery01.put("expectCode", "106601");
	    badQuery01.put("expectMessage", "bad request:name cannot be blank!");
	    
	    listOfQueryParams.add(badQuery01);
	    
	    Map<String, String> badQuery02 = new HashMap<String, String>();
	    
	    badQuery02.put("description", "bad request (name is not present)");
	    badQuery02.put("pageIndex", "1");
	    badQuery02.put("pageSize", "10");
	    badQuery02.put("expectCode", "106602");
	    badQuery02.put("expectMessage", "Required String parameter 'name' is not present");
	    
	    listOfQueryParams.add(badQuery02);
	    
	    Map<String, String> badQuery03 = new HashMap<String, String>();
	    
	    badQuery03.put("description", "bad request (name does not exist)");
	    badQuery03.put("name", "12345678");
	    badQuery03.put("pageIndex", "1");
	    badQuery03.put("pageSize", "10");
	    badQuery03.put("expectCode", "106101");
	    badQuery03.put("expectMessage", "entity 12345678 not found locally!");
	    
	    listOfQueryParams.add(badQuery03);
	    
	    Map<String, String> badQuery04 = new HashMap<String, String>();
	    
	    badQuery04.put("description", "bad request (name contains strange characters)");
	    badQuery04.put("name", "！@#￥%……&*（）——+？|");
	    badQuery04.put("pageIndex", "1");
	    badQuery04.put("pageSize", "10");
	    badQuery04.put("expectCode", "106101");
	    badQuery04.put("expectMessage", "entity ！@#￥%……&*（）——+？| not found locally!");
	    
	    listOfQueryParams.add(badQuery04);
	    
	    String long_name = "超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024"
	    				 + "超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024"
	    				 + "超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024"
	    				 + "超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024"
	    				 + "超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024"
	    				 + "超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024"
	    				 + "超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024超长字符1024"
	    				 + "超长字符1024超长字符1024";
	    
	    Map<String, String> badQuery05 = new HashMap<String, String>();
	    
	    badQuery05.put("description", "bad request (name is too long)");
	    badQuery05.put("name", long_name);
	    badQuery05.put("pageIndex", "1");
	    badQuery05.put("pageSize", "10");
	    badQuery05.put("expectCode", "106101");
	    badQuery05.put("expectMessage", "entity " + long_name + " not found locally!");
	    
	    listOfQueryParams.add(badQuery05);
	    
	    Map<String, String> badQuery06 = new HashMap<String, String>();
	    
	    badQuery06.put("description", "bad request (incorrect condition)");
	    badQuery06.put("name", "Site");
	    badQuery06.put("condition", "id=?????");
	    badQuery06.put("expectCode", "106119");
	    badQuery06.put("expectMessage", "cannot parse the sql");
	    
	    listOfQueryParams.add(badQuery06);
	    
	    for (Map<String, String> map : listOfQueryParams) 
	    {    		
	    	if (map.get("description").contains("good request")) 
	    	{
	    		map.put("expectCode", "0");
	    		map.put("expectMessage", "Operate success.");
	    	}
	    	
	        queryParamCollection.add(new Object[]{map});
	    }   

	    return queryParamCollection.iterator();
	}
	 
	@Test (priority = 0, description="Test connector interface: Get All Entities name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT and verify if all the available entity names can be read out.")
	@Story("Connector Interface API design")
	public void GetAllEntitiesName()
	{
	  Reporter.log("Send a 'GetAllEntitiesName' request");
		
	  Response response = Endpoint.getAllEntitiesName();

	  Reporter.log("Response status is " + response.getStatusCode());
		
	  Reporter.log("Response Body is =>  " + response.getBody().asString());
		
	  GetAllEntitiesNameResponse rspBody = response.getBody().as(GetAllEntitiesNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());

	}
  
	@Test (priority = 0, description="Test connector interface: Get concept model definition by model name.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT to read out the model schema of entity 'Site'.")
	@Story("Connector Interface API design")
  	public void SearchModelSchemaByName()
  	{
	  Reporter.log("Send a 'SearchModelSchemaByName' request for entity 'Site'");	
	  
	  Response response = Endpoint.getConceptModelDefinitionByModelName("Site");
		
	  Reporter.log("Response status is " + response.getStatusCode());
		  
	  Reporter.log("Response Body is =>  " + response.asString());
		
	  SearchModelSchemaByNameResponse rspBody = response.getBody().as(SearchModelSchemaByNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());
		
  	}
  
	@Test (priority = 0, description="Test connector interface: Get all entities name and then check its concept model.")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT to get all the available entity names, then read out the model schema of every entity.")
	@Story("Connector Interface API design")
  	public void SearchModelSchemaForAllEntities()
  	{
	  Reporter.log("Send a 'GetAllEntitiesName' request");
		
	  Response response = Endpoint.getAllEntitiesName();
	  
	  Reporter.log("Response status is " + response.getStatusCode());
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
	  // First get the JsonPath object instance from the Response interface
	  JsonPath jsonPathEvaluator = response.jsonPath();
	 
	  // Read all the entities as a List of String. Each item in the list
	  // represent an entity in the REST service Response
	  List<String> allEntities = jsonPathEvaluator.getList("data");
	  
	  // Iterate over the entity list and query each entity's model schema
	  // via 'SearchModelSchemaByName' request 
	  for(String entityItem : allEntities)
	  {
		  Reporter.log("Send a 'SearchModelSchemaByName' request for entity '" + entityItem + "'");
		  
		  response = Endpoint.getConceptModelDefinitionByModelName(entityItem);
		  
		  Reporter.log("Response status is " + response.getStatusCode());
		  
		  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  }
  	}
  
	@Test (	priority = 0, 
			description="Test connector interface: Get concept model data by condition.", 
			dataProvider = "dataForGetConceptModelDataByCondition" )
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a 'SearchModelDataByCondition' request to SUT with specified parameters and check the response message.")
	@Story("Connector Interface API design")
  	public void SearchModelDataByCondition(Map<String, String> paramMaps)
  	{
	  Reporter.log("Send a 'SearchModelDataByCondition' request");
	  
	  HashMap<String, String> queryParameters = new HashMap<>();
	  
	  if (paramMaps.containsKey("name")) 
		  queryParameters.put("name", paramMaps.get("name"));
	  
	  if (paramMaps.containsKey("domainName")) 
		  queryParameters.put("domainName", paramMaps.get("domainName"));
	  
	  if (paramMaps.containsKey("pageIndex")) 
		  queryParameters.put("pageIndex", paramMaps.get("pageIndex"));
	  
	  if (paramMaps.containsKey("pageSize"))
		  queryParameters.put("pageSize", paramMaps.get("pageSize"));
	  
	  if (paramMaps.containsKey("condition")) 
		  queryParameters.put("condition", paramMaps.get("condition"));
	  
	  if (paramMaps.containsKey("fields")) 
		  queryParameters.put("fields", paramMaps.get("fields"));
	  
	  if (paramMaps.containsKey("order"))
		  queryParameters.put("order", paramMaps.get("order"));
		
	  Response response = Endpoint.getConceptModelDataByCondition(queryParameters);
	  
	  Reporter.log("Response status is " + response.getStatusCode());
	  
	  Reporter.log("Response Body is =>  " + response.asString());
	  
	  Assert.assertEquals(response.getStatusCode(), 200);
	  
	  if (paramMaps.containsKey("expectCode"))
		  Assert.assertEquals(response.jsonPath().getString("code"), paramMaps.get("expectCode"));
	  
	  if (paramMaps.containsKey("expectMessage"))
		  Assert.assertTrue(response.jsonPath().getString("message").contains(paramMaps.get("expectMessage")));
	  
	  if (paramMaps.get("description").contains("good request")) 
	  {	  
		  assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath("JasonModelSchemaFor" + paramMaps.get("name") + ".JSON"));
	  }
	  else 
	  {
		  Assert.assertNull(response.jsonPath().getList("data"));
	  }

  	}
}
