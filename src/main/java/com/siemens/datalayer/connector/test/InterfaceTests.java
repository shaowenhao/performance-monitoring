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
	    
	    Map<String, String> correctQueryParam1 = new HashMap<String, String>();

	    correctQueryParam1.put("description", "correct parameters (name=Customer/ pageIndex=1/ pageSize=10)");
	    correctQueryParam1.put("name", "Customer");
	    correctQueryParam1.put("pageIndex", "1");
	    correctQueryParam1.put("pageSize", "10");
	    correctQueryParam1.put("isValid", "true");
	    
	    listOfQueryParams.add(correctQueryParam1);	
	    
	    Map<String, String> correctQueryParam2 = new HashMap<String, String>();

	    correctQueryParam2.put("description", "correct parameters (name=Customer/ pageIndex=2/ pageSize=10)");
	    correctQueryParam2.put("name", "Customer");
	    correctQueryParam2.put("pageIndex", "2");
	    correctQueryParam2.put("pageSize", "10");
	    correctQueryParam2.put("isValid", "true");
	    
	    listOfQueryParams.add(correctQueryParam2);
	    
	    Map<String, String> incorrectQueryParam1 = new HashMap<String, String>();
	    
	    incorrectQueryParam1.put("description", "incorrect parameters (name is not present/ pageIndex=1/ pageSize=10)");
	    incorrectQueryParam1.put("pageIndex", "1");
	    incorrectQueryParam1.put("pageSize", "10");
	    incorrectQueryParam1.put("isValid", "false");
	    incorrectQueryParam1.put("expectCode", "106602");
	    incorrectQueryParam1.put("expectMessage", "Required String parameter 'name' is not present");
	    
	    listOfQueryParams.add(incorrectQueryParam1);

	    for (Map<String, String> map : listOfQueryParams) {
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
	  Reporter.log("Send a 'SearchModelDataByCondition' request with " + paramMaps.get("description"));
	  
	  HashMap<String, String> queryParameters = new HashMap<>();
	  
	  if (paramMaps.containsKey("name")) 
		  queryParameters.put("name", paramMaps.get("name"));
	  
	  if (paramMaps.containsKey("pageIndex")) 
		  queryParameters.put("pageIndex", paramMaps.get("pageIndex"));
	  
	  if (paramMaps.containsKey("pageSize"))
		  queryParameters.put("pageSize", paramMaps.get("pageSize"));
		
	  Response response = Endpoint.getConceptModelDataByCondition(queryParameters);
	  
	  Reporter.log("Response status is " + response.getStatusCode());
	  
	  Reporter.log("Response Body is =>  " + response.asString());
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
	  if (paramMaps.get("isValid")=="true") {
		  assertThat(response.getBody().asString(), matchesJsonSchemaInClasspath("JasonModelShemaForCustomer.JSON"));
	  }
	  else {
		  Assert.assertEquals(response.jsonPath().getString("code"), paramMaps.get("expectCode"));
		  Assert.assertEquals(response.jsonPath().getString("message"), paramMaps.get("expectMessage"));
		  Assert.assertNull(response.jsonPath().getList("data"));
	  }

  	}
}
