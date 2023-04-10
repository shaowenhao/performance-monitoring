package com.siemens.devops.monitoring.utils;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelFileReader {

	public static void readParamFromExcelFile(String fileName, String sheetName, Collection<Object[]> params)
			throws IOException, InvalidFormatException {

		// Creating a Workbook from an Excel file (.xls or .xlsx)
		Workbook workbook = WorkbookFactory
				.create(new File(System.getProperty("user.dir") + "/src/main/resources/test-data-xls/" + fileName));

		// For test
//		Workbook workbook = WorkbookFactory
//				.create(new File(System.getProperty("user.dir") + "/classes/test-data-xls/" + fileName));

		for (Sheet sheet : workbook) {

			if (sheet.getSheetName().contentEquals(sheetName)) {

				DataFormatter dataFormatter = new DataFormatter();

				if (sheet.getLastRowNum() > 0) {

					Row headLine = sheet.getRow(0);

					for (Row row : sheet) {

						if (row.getRowNum() == 0)
							continue;

						Map<String, String> readParam = new LinkedHashMap<String, String>();
						for (Cell cell : row) {

//							if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC) {
							String cellValue = dataFormatter.formatCellValue(cell);
							String dataFieldName = dataFormatter
									.formatCellValue(headLine.getCell(cell.getColumnIndex()));
							readParam.put(dataFieldName, cellValue);
//							}

						}

						params.add(new Object[] { readParam });
					}
				}
			}
		}

		// Closing the workbook
		workbook.close();
	}

}
