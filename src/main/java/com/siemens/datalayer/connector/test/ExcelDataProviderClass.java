package com.siemens.datalayer.connector.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.testng.ITestContext;
import org.testng.annotations.*;

import com.siemens.datalayer.utils.ExcelFileReaderClass;

public class ExcelDataProviderClass {
	
	@DataProvider(name = "connector-test-data-provider")
	public static Iterator<Object[]> xmlDataProviderMethod(Method m, ITestContext iTestContext)
	{
	    Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
	    
	    String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForConnectorTest");
		
		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		} 
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}
		
		return queryParamCollection.iterator();
	}

}
