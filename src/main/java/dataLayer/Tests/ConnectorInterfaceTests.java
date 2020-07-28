package dataLayer.Tests;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import org.testng.Assert;
import org.testng.Reporter;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import apiEngine.endpoint.*;
import apiEngine.model.*;

public class ConnectorInterfaceTests {

	@Parameters({"base_url", "port"})
	@BeforeTest
	public void beforeTest(@Optional("http://localhost") String base_url, @Optional("9001") String port) {
	    dlConnector.setBaseUrl(base_url);
	    dlConnector.setPort(port);
	}
    
  @Test
	public void GetAllEntitiesName()
	{
	  Reporter.log("Send a 'GetAllEntitiesName' request");
		
	  Response response = dlConnector.getAllEntitiesName();

	  Reporter.log("Response status is " + response.getStatusCode());
		
	  Reporter.log("Response Body is =>  " + response.getBody().asString());
		
	  GetAllEntitiesNameResponse rspBody = response.getBody().as(GetAllEntitiesNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());

	}
  
  @Test
  	public void SearchModelSchemaByName()
  	{
	  Reporter.log("Send a 'SearchModelSchemaByName' request for entity 'Site'");	
	  
	  Response response = dlConnector.searchModelSchemaByName("Site");
		
	  Reporter.log("Response status is " + response.getStatusCode());
		  
	  Reporter.log("Response Body is =>  " + response.asString());
		
	  SearchModelSchemaByNameResponse rspBody = response.getBody().as(SearchModelSchemaByNameResponse.class);
		
	  Assert.assertEquals("Operate success.", rspBody.getMessage());
		
  	}
  
  @Test
  	public void SearchModelSchemaForAllEntities()
  	{
	  Reporter.log("Send a 'GetAllEntitiesName' request");
		
	  Response response = dlConnector.getAllEntitiesName();
	  
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
		  
		  response = dlConnector.searchModelSchemaByName(entityItem);
		  
		  Reporter.log("Response status is " + response.getStatusCode());
		  
		  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  }
  	}
  
  @Test
  	public void SearchModelDataByCondition()
  	{
	  Reporter.log("Send a 'SearchModelDataByCondition' request");
	  
	  HashMap<String, String> queryParameters = new HashMap<>();
	  queryParameters.put("name", "Customer");
	  queryParameters.put("pageIndex", "1");
	  queryParameters.put("pageSize", "10");
		
	  Response response = dlConnector.getConceptModelDataByCondition(queryParameters);
	  
	  Reporter.log("Response status is " + response.getStatusCode());
	  
	  Reporter.log("Response Body is =>  " + response.asString());
	  
	  Assert.assertEquals(response.getStatusCode(), 200, "Correct status code returned");
	  
  	}
}
