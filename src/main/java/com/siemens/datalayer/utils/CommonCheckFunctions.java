package com.siemens.datalayer.utils;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import io.qameta.allure.Step;

public class CommonCheckFunctions {
		
	@Step("Verify if the response contains the required data fields")
	public static void checkDataContainsSpecifiedFields(String jsonPath, String fields, List<HashMap<String, String>> responseData)
	{	  
		if (responseData==null)
		{
			System.out.println("Error: null is returned when try to get data from jasonPath '" + jsonPath + "'");
			return;
		}
		
		Scanner scanner = new Scanner(fields);
		scanner.useDelimiter(",");
		  
		while (scanner.hasNext())
		{
			String keyToCompare = scanner.next();
			  
			for (HashMap<String, String> rspDataItem: responseData)
			{
				if (rspDataItem != null)
					assertThat(rspDataItem, hasKey(keyToCompare.trim()));
			}	
		}
		scanner.close();
	}

	@Step("Verify if the response only contains the required data fields")
	public static void checkDataOnlyContainsSpecifiedFields(String jsonPath,String fields,List<HashMap<String,String>> responseData)
	{
		if (responseData == null)
		{
			System.out.println("Error: null is returned when try to get data from jasonPath '" + jsonPath + "'");
		}

		for (HashMap<String,String> rspDataItem : responseData)
		{
			if (rspDataItem != null)
			{
				for (String key : rspDataItem.keySet())
				{
					assert(fields.contains(key));
				}
			}
		}
	}

	@Step("Verify if the response contains the specified number of items")
	public static void checkDataContainsNumOfItems(String jsonPath, String NumOfItems, List<HashMap<String, String>> responseData)
	{
		if (responseData==null)
		{
			System.out.println("Error: null is returned when try to get data from jasonPath '" + jsonPath + "'");
			return;
		}
		int actualNumOfItems = responseData.size();
		assertThat(NumOfItems,is(equalTo(String.valueOf(actualNumOfItems))));

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
	
	public static boolean isValidDateStr(String input)	
	{


		if(!input.contains("T")) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setLenient(false);
			if (input.length() < 5) return false;
			if (!isIntegerStr(input.substring(0, 4))) input = input.substring(1, input.length() - 1);

			try {
				dateFormat.parse(input);
			} catch (Exception e) {
				return false;
			}

			return true;
		}
		else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			dateFormat.setLenient(false);
			if (input.length() < 5) return false;
			if (!isIntegerStr(input.substring(0, 4))) input = input.substring(1, input.length() - 1);

			try {
				dateFormat.parse(input);
			} catch (Exception e) {
				return false;
			}

			return true;
		}
	}
	
	public static boolean compareOrderFieldValue(String valueShouldBeSmall, String valueShouldBeLarge)
	{       
		if ((isIntegerStr(valueShouldBeSmall)) && (isIntegerStr(valueShouldBeLarge))) // Compare integer values
		{
			if (Integer.parseInt(valueShouldBeSmall) > Integer.parseInt(valueShouldBeLarge)) return false;
		}
		else if ((isFloatStr(valueShouldBeSmall)) && (isFloatStr(valueShouldBeLarge))) // Compare float values
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
			else
			{
				System.out.println("Error: The specified order field '" + keyForOrder + "' is not found!");
			}
		}	
		  
		scanner.close();
		
		return result;
	}
	
	@Step("Verify if the data satisfies the given condition")
	public static boolean ifDataSatisfiesCondition(String jasonPath, String fieldToCompare, String compareType, String valueToCompare, List<HashMap<String, String>> dataList)
	{	
		if (dataList==null) return false;
		if (dataList.isEmpty()) return false;
	    
	    String compareValue = String.valueOf(valueToCompare);
		 
	    Iterator<HashMap<String, String>> iter = dataList.iterator();
	    HashMap<String, String> currentEntry;

	    while (iter.hasNext()) 
	    {
	    	currentEntry = iter.next();	
	    	
	    	if (currentEntry.containsKey(fieldToCompare))
	    	{
		        String currentFieldValue = String.valueOf(currentEntry.get(fieldToCompare));
		        
		        if (checkCondition(currentFieldValue, compareType, compareValue)==false) 
		        {
		        	System.out.println("condition check failed: value '" + currentFieldValue + "' does not satisfy condition '" + compareType + ": " + valueToCompare + "'");
		        	return false;
		        }
	    	}
	    }
		
		return true;
	}
	
	@Step("Verify if the data satisfies at least one of the given joint conditions")
	public static boolean ifDataSatisfiesJointCondition(String fieldToCompare1, String compareType1, String valueToCompare1, 
														String fieldToCompare2, String compareType2, String valueToCompare2,
														List<HashMap<String, String>> dataList)
	{	
		if (dataList == null || dataList.isEmpty()) {
			return false;
		}
	    
	    String compareValue1 = String.valueOf(valueToCompare1);
	    String compareValue2 = String.valueOf(valueToCompare2);
		 
	    Iterator<HashMap<String, String>> iter = dataList.iterator();
	    HashMap<String, String> currentEntry;

	    while (iter.hasNext()) 
	    {
	    	currentEntry = iter.next();	    	
	        String currentFieldValue1 = String.valueOf(currentEntry.get(fieldToCompare1));
	        String currentFieldValue2 = String.valueOf(currentEntry.get(fieldToCompare2));
	        
	        if ((checkCondition(currentFieldValue1, compareType1, compareValue1)==false) &&
	        	(checkCondition(currentFieldValue2, compareType2, compareValue2)==false))	
	        {
	        	System.out.println("Joint condition check failed: value '" + currentFieldValue1 + "' does not satisfy condition '" + compareType1 + ": " + valueToCompare1 + "'; and"
	        					 + "value '" + currentFieldValue2 + "' does not satisfy condition '" + compareType2 + ": " + valueToCompare2 + "'");

	        	return false;
	        }
	    }
		
		return true;
	}
	
	public static boolean checkCondition(String valueStr, String compareType, String valueToCompare)
	{
		Boolean result = true;
		
		try 
		{
			switch (compareType) // Reference: https://hasura.io/docs/1.0/graphql/core/queries/query-filters.html#comparision-operators
			{
				case "eq":
					result = ifFieldValueEqualsTo(valueStr, valueToCompare);
					break;
					
				case "neq":
					result = !ifFieldValueEqualsTo(valueStr, valueToCompare);
					break;
					
				case "gt":
					System.out.println("code into gt");
					result = ifFieldValueGreaterThan(valueStr, valueToCompare);
					break;
					
				case "lt":
					result = ifFieldValueLessThan(valueStr, valueToCompare);
					break;
					
				case "gte":
					result = ifFieldValueGreaterThan(valueStr, valueToCompare) || ifFieldValueEqualsTo(valueStr, valueToCompare);
					break;
					
				case "lte":
					result = ifFieldValueLessThan(valueStr, valueToCompare) || ifFieldValueEqualsTo(valueStr, valueToCompare);
					break;
					
				case "in":
					result = ifFieldValueInTheList(valueStr, valueToCompare);
					break;
					
				case "nin":
					result = !ifFieldValueInTheList(valueStr, valueToCompare);
					break;
			}
		} 
		catch (ParseException e) 
		{
			System.out.println("Error: data parse operation failed");
	    }
		
		return result;
	}
	
	public static boolean ifFieldValueGreaterThan(String valueStr, String valueToCompare) throws ParseException 
	{
		if (isIntegerStr(valueToCompare)) // Compare integer values
		{
			if (Integer.parseInt(valueToCompare) >= Integer.parseInt(valueStr)) return false;
		}
		else if (isFloatStr(valueToCompare)) // Compare float values
		{
			if (Float.parseFloat(valueToCompare) >= Float.parseFloat(valueStr)) return false;
		}
		else // Compare string 
		{
			if (isValidDateStr(valueToCompare))
			{
				SimpleDateFormat valueDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				valueDateFormat.setLenient(false);
				Date dateValue = valueDateFormat.parse(valueStr.trim());

				SimpleDateFormat compareDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				compareDateFormat.setLenient(false);
				
				if (!isIntegerStr(valueToCompare.substring(0, 4))) 
					valueToCompare = valueToCompare.substring(1, valueToCompare.length()-1);
				
				Date dateValueToCompare = compareDateFormat.parse(valueToCompare.trim());
				return (dateValue.after(dateValueToCompare));
			}
			else
			{
				if (valueStr.compareTo(valueToCompare) <= 0) return false;
			}
		}
			
		return true; 
	}

	public static boolean ifFieldValueLessThan(String valueStr, String valueToCompare) throws ParseException
	{   	
		if (isIntegerStr(valueToCompare)) // Compare integer values
		{
			if (Integer.parseInt(valueStr) >= Integer.parseInt(valueToCompare)) return false;
		}
		else if (isFloatStr(valueToCompare)) // Compare float values
		{
			if (Float.parseFloat(valueStr) >= Float.parseFloat(valueToCompare)) return false;
		}
		else // Compare string 
		{
			if (isValidDateStr(valueToCompare))
			{
				SimpleDateFormat valueDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				valueDateFormat.setLenient(false);
				Date dateValue = valueDateFormat.parse(valueStr.trim());
				
				SimpleDateFormat compareDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				compareDateFormat.setLenient(false);
				
				if (!isIntegerStr(valueToCompare.substring(0, 4))) 
					valueToCompare = valueToCompare.substring(1, valueToCompare.length()-1);
				
				Date dateValueToCompare = compareDateFormat.parse(valueToCompare.trim());
				
				return (dateValue.before(dateValueToCompare));
			}
			else
			{
				if (valueToCompare.compareTo(valueStr) <= 0) return false;
			}
		}
			
		return true; 
	}
	
	public static boolean ifFieldValueEqualsTo(String valueStr, String valueToCompare) throws ParseException
	{   	
		if (isIntegerStr(valueToCompare)) // Compare integer values
		{
			if (Integer.parseInt(valueStr) != Integer.parseInt(valueToCompare)) return false;
		}
		else if (isFloatStr(valueToCompare)) // Compare float values
		{
			if (Float.parseFloat(valueStr) != Float.parseFloat(valueToCompare)) return false;
		}
		else // Compare string 
		{
			if (isValidDateStr(valueToCompare))
			{
				SimpleDateFormat valueDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				valueDateFormat.setLenient(false);
				Date dateValue = valueDateFormat.parse(valueStr.trim());
				
				SimpleDateFormat compareDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				compareDateFormat.setLenient(false);
				
				if (!isIntegerStr(valueToCompare.substring(0, 4))) 
					valueToCompare = valueToCompare.substring(1, valueToCompare.length()-1);
				
				Date dateValueToCompare = compareDateFormat.parse(valueToCompare.trim());
				
				return (dateValue.equals(dateValueToCompare));
			}
			else
			{
				if (valueToCompare.compareTo(valueStr) != 0) return false;				
			}
		}
			
		return true; 
	}
	
	public static boolean ifFieldValueInTheList(String valueStr, String valueListToCompare)
	{
		Boolean result = false;
		
		valueListToCompare = valueListToCompare.replace("[", "");
		valueListToCompare = valueListToCompare.replace("]", "");
		
		Scanner scanner = new Scanner(valueListToCompare);
		scanner.useDelimiter(",");
		  
		while (scanner.hasNext())
		{
			String valueToCompare = scanner.next();
			
			try 
			{
				if (ifFieldValueEqualsTo(valueStr, valueToCompare)) 
				{
					result = true; 
					break;
				}
				
			} 
			catch (ParseException e) 
			{
				System.out.println("Error: data parse operation failed");
		    }
		}	
		  
		scanner.close();
		
		return result;	
	}
	
	public static String addStringField(String value, String key)
	{
		String result = "  \"" + key + "\": " + value;	
		return result;
	}
	
	public static String addTimeStampField(String timeStr, String key)
	{
		String result = "";
 
		try
		{				
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date date = format.parse(timeStr);
			long timeStamp = date.getTime();
			
			result += "  \"" + key + "\": " + String.valueOf(timeStamp);
		}
		catch (ParseException e) 
		{
			System.out.println("Error: time parse operation failed");
	    }
		
		return result;
	}
	
	public static String timeStampToDate(String s)
	{		
		long timeStamp = new Long(s);
		
		Date date = new Date(timeStamp);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String result = simpleDateFormat.format(date); 
		
		return result;
	}
	
	public static String addListField(String list, String listName, String entryName)
	{
		String result = "  \"" + listName + "\": [\r\n";
		
		Scanner scanner = new Scanner(list);
		scanner.useDelimiter(",");
		
		boolean isFirst = true;
		while (scanner.hasNext())
		{
			if (!isFirst) result += ", \r\n";
				
			String entryValue = scanner.next();					  
			result += "    {" + " \"" + entryName + "\": " + entryValue + " }";
			
			if (isFirst) isFirst = false;
		}	

		if (!isFirst) result += "\r\n  ]";
		
		scanner.close();
		
		return result;
	}
	
	public static String delRedundentBlank(String inputStr)
	{
		return inputStr.replaceAll("\\s+", " ");
	}
	
	public static String removeBlankBeforeToken(String inputStr)
	{
		if (inputStr.contains("\\\"")) inputStr = inputStr.replace("\\\"", "");
		if (inputStr.contains(" [")) inputStr = inputStr.replace(" [", "[");
		if (inputStr.contains(" ]")) inputStr = inputStr.replace(" ]", "]");
		
		return inputStr;
	}
}
