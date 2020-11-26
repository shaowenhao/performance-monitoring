package com.siemens.datalayer.utils;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.testng.Assert;

import io.qameta.allure.Step;

public class CommonCheckFunctions {
	
	@Step("Verify the status code, response code, and message")
	public static void checkResponseCode(Map<String, String> requestParameters, int actualStatusCode, String actualCode, String actualMessage)
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
	public static void checkDataContainsSpecifiedFields(String fields, List<HashMap<String, String>> responseData)
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
	
	@Step("Verify if the data in response message matches the correct model schema")
	public static void verifyIfDataMatchesJsonSchemaTemplate(String schemaTemplateFile, String responseBody)
	{
		assertThat(responseBody, matchesJsonSchemaInClasspath(schemaTemplateFile));
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
	
	@Step("Verify if the data list in response message is sorted by the specified order")
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
				result = isMapSortedByKey(rspDataList, keyForOrder, sortOrder);
				break; // only check the 1st valid order parameter
			}
		}	
		  
		scanner.close();
		
		return result;
	}

}
