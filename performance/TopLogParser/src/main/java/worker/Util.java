package worker;

import java.io.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Util {

	public static void writeToExcel(Object[][] dataSet, String file_path, String sheetName) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);

		int rowNum = 0;
		//System.out.println("Creating excel");

		for (Object[] data : dataSet) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;
			for (Object field : data) {
				// System.out.println(field);
				Cell cell = row.createCell(colNum++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Float) {
					cell.setCellValue((Float) field);
				} else if (field instanceof Long) {
					cell.setCellValue((Long) field);
				}
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(file_path);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println("Done");

	}

	/**
	 * 
	 * @param dataSet
	 * @param file_path
	 * @param sheetName
	 * 
	 *                  This method creates a new excel sheet if there is not one in
	 *                  the path else will load an existing one and put the contents
	 *                  to the same file by creating new sheets.
	 */
	public static void writeToExistingExcel(Object[][] dataSet, String file_path, String sheetName) {
		HSSFWorkbook workbook;
		try {
			File file = new File(file_path);
			if (file.exists() == false) {
				System.out.println("Creating a new workbook '" + file + "'");
				workbook = new HSSFWorkbook();
			} else {
				System.out.println("Appending to existing workbook '" + file + "'");
				final InputStream is = new FileInputStream(file);
				try {
					workbook = new HSSFWorkbook(is);
				} finally {
					is.close();
				}
			}
			if (workbook.getSheet(sheetName) == null) {
				HSSFSheet sheet = workbook.createSheet(sheetName);
				int rowNum = 0;
				System.out.println("Creating excel");

				for (Object[] data : dataSet) {
					Row row = sheet.createRow(rowNum++);
					int colNum = 0;
					for (Object field : data) {
						// System.out.println(field);
						Cell cell = row.createCell(colNum++);
						if (field instanceof String) {
							cell.setCellValue((String) field);
						} else if (field instanceof Float) {
							cell.setCellValue((Float) field);
						} else if (field instanceof Long) {
							cell.setCellValue((Long) field);
						}
					}
				}
				
				try {
					FileOutputStream outputStream = new FileOutputStream(file_path);
					workbook.write(outputStream);
					workbook.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
