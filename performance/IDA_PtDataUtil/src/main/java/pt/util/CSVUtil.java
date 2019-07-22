package pt.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import pt.dto.auth.demo.AuthCSVEntity;

/**
 * 
 * @author M1030608
 * 
 *         http://zetcode.com/articles/opencsv/
 * 
 *
 */
public class CSVUtil {

	public static List<AuthCSVEntity> loadCSVData(String csvFilepath) throws FileNotFoundException {

		Gson gson = new Gson();
//		CsvToBean<AuthCSVEntity> csv = new CsvToBean();
//		FileReader fr = new FileReader(filepath);
//		CSVReader csvReader = new CSVReader(fr);

		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("firstName", "firstName");
		mapping.put("lastName", "lastName");
		mapping.put("dateOfBirth", "dateOfBirth");
		mapping.put("addressLine1", "addressLine1");
		mapping.put("addressLine2", "addressLine2");
		mapping.put("addressLine3", "addressLine3");
		mapping.put("region", "region");
		mapping.put("state", "state");
		mapping.put("province", "province");
		mapping.put("addressLine3", "addressLine3");
		mapping.put("city", "city");
		mapping.put("postalCode", "postalCode");
		mapping.put("phone1", "phone1");
		mapping.put("phone2", "phone2");
		mapping.put("email", "email");

		HeaderColumnNameTranslateMappingStrategy<AuthCSVEntity> strategy = new HeaderColumnNameTranslateMappingStrategy<AuthCSVEntity>();
		strategy.setType(AuthCSVEntity.class);
		strategy.setColumnMapping(mapping);

		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(csvFilepath));
		} catch (FileNotFoundException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CsvToBean<AuthCSVEntity> csvToBean = new CsvToBean<>();

		List<AuthCSVEntity> list = csvToBean.parse(strategy, csvReader);
		for (AuthCSVEntity aE : list) {
			// System.out.println(gson.toJson(aE));
		}

		return list;

	}

	public static List<AuthCSVEntity> loadCSVData1() throws FileNotFoundException {
		Gson gson = new Gson();

		String[] columns = new String[] { "firstName", "middleName", "lastName", "dateOfBirth", "gender",
				"addressLine1", "addressLine2", "addressLine3" };
		CSVReader csvReader = null;
		String folder = "D:\\MOSIP\\IDA\\Data-Generation POC";
		String filename = "address_data.csv";
		String filePath = folder + File.separator + filename;

		csvReader = new CSVReader(new FileReader(filePath), ',', '"', 1);
		// mapping of columns with their positions
		ColumnPositionMappingStrategy<AuthCSVEntity> mappingStrategy = new ColumnPositionMappingStrategy<AuthCSVEntity>();
		// Set mappingStrategy type to Employee Type
		mappingStrategy.setType(AuthCSVEntity.class);
		mappingStrategy.setColumnMapping(columns);
		CsvToBean<AuthCSVEntity> ctb = new CsvToBean<AuthCSVEntity>();
		List<AuthCSVEntity> list = ctb.parse(mappingStrategy, csvReader);
		for (AuthCSVEntity e : list) {
			// System.out.println(gson.toJson(e));
		}
		return list;
	}

	public static void writeStringListToCsv(List<String> vIds, String path)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

//		try (Writer writer = Files.newBufferedWriter(Paths.get(path));) {
//
//			StatefulBeanToCsv<String> beanToCsv = new StatefulBeanToCsvBuilder(writer)
//					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();
//			System.out.println("Length of vIds list " + vIds.size());
//			System.out.println("Path where CSV data is saved " + path);
//			beanToCsv.write(vIds);
//		}

	}

	public static void writeDataToCsv(List<String[]> data, String path) throws IOException {

		File file = new File(path);
		FileWriter outputfile = new FileWriter(file);
		CSVWriter writer = new CSVWriter(outputfile);
		writer.writeAll(data);
		writer.close();

	}

}
