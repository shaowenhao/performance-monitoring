package com.siemens.devops.monitoring.utils;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ExcelDataProvider {

	@DataProvider(name = "api-engine-test-data-provider")
	public static Iterator<Object[]> testDataProvider(Method m, ITestContext iTestContext) {
		Collection<Object[]> params = new ArrayList<Object[]>();

		String methodName = m.getName();
		String dataFileName = iTestContext.getCurrentXmlTest().getParameter("dataFileForApiEngineTest");

		try {
			ExcelFileReader.readParamFromExcelFile(dataFileName, methodName, params);
		} catch (Exception e) {
			System.out.println("Error occurs when try to open the excel file： " + e.getMessage());
		}

		String projectName = iTestContext.getCurrentXmlTest().getParameter("projectName");
		String envName = iTestContext.getCurrentXmlTest().getParameter("envName");

		Collection<Object[]> paramsByFilter = new ArrayList<Object[]>();
		for (Object[] o : params) {

			@SuppressWarnings("unchecked")
			Map<String, String> param = (Map<String, String>) o[0];
			if (CasePropertyUtils.isRun(param, envName)) {
				paramsByFilter.add(new Object[] { param });
			} else {
				System.out.println("Don't need to run this case [caseId=" + CasePropertyUtils.getCaseId(param) + "] for project ["
						+ projectName + "] in environment [" + envName + "]");
			}
		}

		return paramsByFilter.iterator();
	}

}
