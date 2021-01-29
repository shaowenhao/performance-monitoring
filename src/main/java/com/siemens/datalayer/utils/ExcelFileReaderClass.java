package com.siemens.datalayer.utils;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelFileReaderClass {
	
	public static void readParamFromExcelFile(String fileName, String sheetName, Collection<Object[]> queryParamCollection ) throws IOException, InvalidFormatException {
		
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(System.getProperty("user.dir") + "/src/main/resources/test-data-xls/" + fileName));
        
        for(Sheet sheet: workbook) {

            if (sheet.getSheetName().contentEquals(sheetName)) {            

	            DataFormatter dataFormatter = new DataFormatter();
	            
	            if (sheet.getLastRowNum() > 0) {
	            	
		            Row headLine = sheet.getRow(0);
		            
		            for (Row row: sheet) {
		            	
		            	if (row.getRowNum() == 0) continue;
		            	
	                	Map<String, String> readParam = new LinkedHashMap<String, String>();
		            	for(Cell cell: row) {         	    
		
	                		switch (cell.getCellType()) {
	                		
	                		case STRING:
	                		case NUMERIC:
			                    String cellValue = dataFormatter.formatCellValue(cell);
			                    String dataFieldName = dataFormatter.formatCellValue(headLine.getCell(cell.getColumnIndex()));
			                    
			                    if ((dataFieldName.contains("query")) && (cellValue.contains("\n")))
			                    {
			                    	cellValue = formatQueryString(cellValue);
			                    }
			                    readParam.put(dataFieldName, cellValue);
			                    
	                		case BOOLEAN:
	                		case FORMULA:
	                		case BLANK:
			                
	                		default:
			                    break;	                    	
	                		}
		                }
		                
		            	queryParamCollection.add(new Object[]{readParam});
	            	}		                           
	            }
	        }
        }
        
        // Closing the workbook
        workbook.close();
	}
	
	public static String formatQueryString(String inputStr)
	{	
		inputStr = inputStr.replaceAll("\\n", "");
		inputStr = inputStr.replaceAll("\\s+", " ");
		inputStr = inputStr.replaceAll(" \\(cond:", "\\(cond:");
		
		inputStr = inputStr.replace(" {", "{");
		inputStr = inputStr.replace(" }", "}");
		inputStr = inputStr.replace("( cond:", "(cond:");
		inputStr = inputStr.replace("\", order:", "\",order:");
		if (inputStr.contains("(cond:"))
		{
			int index1 = inputStr.indexOf("{", inputStr.indexOf("{")+1);
			int index2 = inputStr.indexOf("(cond:");
			if (index2<index1) inputStr = inputStr.replaceFirst("\"\\)\\{", "\"\\) \\{");
		}
		
		if (inputStr.contains(" [")) inputStr = inputStr.replace(" [", "[");
		if (inputStr.contains(" ]")) inputStr = inputStr.replace(" ]", "]");
		
		return inputStr;
	}
}
