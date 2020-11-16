package com.siemens.datalayer.utils;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExcelFileReaderClass {
	
	public static void readParamFromExcelFile(String fileName, String sheetName, Collection<Object[]> queryParamCollection ) throws IOException, InvalidFormatException {
		
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(System.getProperty("user.dir") + "/src/main/resources/test-data-xls/" + fileName));
        
        for(Sheet sheet: workbook) {

            if (sheet.getSheetName().contentEquals(sheetName)) {            

	            DataFormatter dataFormatter = new DataFormatter();
	            
	            if (sheet.getLastRowNum()>1) {
	            	
		            Row headLine = sheet.getRow(0);
		            
		            for (Row row: sheet) {
		            	
		            	if (row.getRowNum() == 0) continue;
		            	
	                	Map<String, String> readParam = new HashMap<String, String>();
		            	for(Cell cell: row) {         	    
		
	                		switch (cell.getCellType()) {
	                		
	                		case STRING:
	                		case NUMERIC:
			                    String cellValue = dataFormatter.formatCellValue(cell);
			                    String dataFieldName = dataFormatter.formatCellValue(headLine.getCell(cell.getColumnIndex()));
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
}
