package com.siemens.datalayer.utils;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ExcelDataProviderClass {
	
	@DataProvider(name = "connector-test-data-provider")
	public static Iterator<Object[]> connectorTestDataProvider(Method m, ITestContext iTestContext)
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

	@DataProvider(name = "connector-realtime-test-data-provider")
	public static Iterator<Object[]> connectorRealtimeTestDataProvider(Method m, ITestContext iTestContext)
	{
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

		String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForConnectorRealtimeTest");

		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		}
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}

		return queryParamCollection.iterator();
	}

	@DataProvider(name = "connector-configure-test-data-provider")
	public static Iterator<Object[]> connectorConfigureTestDataProvider(Method m, ITestContext iTestContext)
	{
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

		String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForConnectorConfigureTest");

		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		}
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}

		return queryParamCollection.iterator();
	}
	
	@DataProvider(name = "api-engine-test-data-provider")
	public static Iterator<Object[]> apiEngineTestDataProvider(Method m, ITestContext iTestContext)
	{
	    Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
	    
	    String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForApiEngineTest");
	    
		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		} 
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}
		
		return queryParamCollection.iterator();
	}
	
	@DataProvider(name = "api-service-test-data-provider")
	public static Iterator<Object[]> apiServiceTestDataProvider(Method m, ITestContext iTestContext)
	{
	    Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
	    
	    String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForApiServiceTest");
	    
		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		} 
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}
		
		return queryParamCollection.iterator();
	}
	
	@DataProvider(name = "entity-management-test-data-provider")
	public static Iterator<Object[]> entityManagementTestDataProvider(Method m, ITestContext iTestContext)
	{
	    Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

	    // 获取调用此方法的testcase（带@Test）的函数名称，如createEntity
	    String methodName = m.getName();
	    // 获取xml文件中的变量“dataFileForEntityMgmtTest”的值，即iems-entity-management-test-data.xlsx
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForEntityMgmtTest");
	    
		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		} 
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}
		
		return queryParamCollection.iterator();
	}

	@DataProvider(name = "subscription-management-test-data-provider")
	public static Iterator<Object[]> subscriptionManagementTestDataProvider(Method m, ITestContext iTestContext)
	{
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

		String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForSubMgmtTest");

		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		}
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}

		return queryParamCollection.iterator();
	}

	@DataProvider(name = "ui-backend-test-data-provider")
	public static Iterator<Object[]> uiBackendTestDataProvider(Method m, ITestContext iTestContext)
	{
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();

		String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForUiBackendTest");

		try {
			ExcelFileReaderClass.readParamFromExcelFile(dataFileName, methodName, queryParamCollection);
		}
		catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}

		return queryParamCollection.iterator();
	}
}
