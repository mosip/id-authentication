package app.util;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	private static String excelFile = PropertiesUtil.EXCEL_FILE;

	public ExcelUtil() {

	}

	public static void processDataToWriteToExcel(Map<String, Map<String, Long>> regIdsMappedByStartTime,
			Set<String> activities) {
		Object[][] records = new Object[activities.size() + 1][regIdsMappedByStartTime.size() + 1];
		regIdsMappedByStartTime.entrySet();
		int row = 0, col = 0;
		records[row][col] = "Activities";
		Set<String> set = regIdsMappedByStartTime.keySet();
		addTimesToTable(records, set);
		addActivitiesTotable(records, activities);
		addTimeDifferencesTotable(records, regIdsMappedByStartTime);

		writeFirstColumnToExcel(records);

	}

	private static void writeFirstColumnToExcel(Object[][] records) {
		// Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("Activities Time");
		int rowNum = 0;
		for (Object[] rowData : records) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : rowData) {
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Long) {
					cell.setCellValue((Long) field);
				}
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(excelFile);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addTimeDifferencesTotable(Object[][] records,
			Map<String, Map<String, Long>> regIdsMappedByStartTime) {
		int row = 1, col = 1;
		for (Entry<String, Map<String, Long>> entry : regIdsMappedByStartTime.entrySet()) {
			// String time = entry.getKey();
			Map<String, Long> activitiesMap = entry.getValue();
			activitiesMap.entrySet();
			row = 1;
			for (Entry<String, Long> activity : activitiesMap.entrySet()) {

				// String activityName = activity.getKey();
				Long activityTime = activity.getValue();
				records[row][col] = activityTime;
				row++;
			}
			col++;
		}
	}

	private static void addActivitiesTotable(Object[][] records, Set<String> activities) {

		int row = 1;
		int col = 0;

		for (String activity : activities) {
			records[row++][col] = activity;
		}

	}

	private static void addTimesToTable(Object[][] records, Set<String> times) {

		int row = 0;
		int col = 1;

		for (String time : times) {
			records[row][col] = time;
			col++;
		}
	}

	private void writeFirstColumnToExcel(Set<String> activities) {

	}

}
