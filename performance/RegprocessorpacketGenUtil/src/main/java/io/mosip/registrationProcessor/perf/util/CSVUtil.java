package io.mosip.registrationProcessor.perf.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.mosip.kernel.core.exception.FileNotFoundException;
import io.mosip.registrationProcessor.perf.dto.RegDataCSVDto;

public class CSVUtil {

	public static List<String> loadCSVData(String csvFilepath) throws FileNotFoundException {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void writeObjectListToCsv(List<RegDataCSVDto> list, String path)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		Charset charset = Charset.forName("Us-ASCII");

		try (Writer writer = Files.newBufferedWriter(Paths.get(path), StandardOpenOption.APPEND);) {

			StatefulBeanToCsv<RegDataCSVDto> beanToCsv = new StatefulBeanToCsvBuilder(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();
			beanToCsv.write(list);
		}

	}

	public static void writeObjectsToCsv(List<String[]> csvDtoData, String filePath) throws IOException {

		File file = new File(filePath);
		FileWriter outputfile = new FileWriter(file, true);
		CSVWriter writer = new CSVWriter(outputfile);
		writer.writeAll(csvDtoData);
		writer.close();
	}

	@SuppressWarnings("deprecation")
	public static List<RegDataCSVDto> loadObjectsFromCSV(String filePath) {
		List<RegDataCSVDto> list = new ArrayList<RegDataCSVDto>();
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("fullName", "fullName");
		mapping.put("dateOfBirth", "dateOfBirth");
		mapping.put("age", "age");
		mapping.put("gender", "gender");
		mapping.put("residenceStatus", "residenceStatus");
		mapping.put("addressLine1", "addressLine1");
		mapping.put("addressLine2", "addressLine2");
		mapping.put("addressLine3", "addressLine3");
		mapping.put("region", "region");
		mapping.put("province", "province");
		mapping.put("city", "city");
		mapping.put("localAdministrativeAuthority", "localAdministrativeAuthority");
		mapping.put("postalCode", "postalCode");
		mapping.put("phone", "phone");
		mapping.put("email", "email");
		mapping.put("cnieNumber", "cnieNumber");

		HeaderColumnNameTranslateMappingStrategy<RegDataCSVDto> strategy = new HeaderColumnNameTranslateMappingStrategy<RegDataCSVDto>();
		strategy.setType(RegDataCSVDto.class);
		strategy.setColumnMapping(mapping);

		CSVReader csvReader = null;

		try {
			csvReader = new CSVReader(new FileReader(filePath));
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}

		CsvToBean<RegDataCSVDto> csvToBean = new CsvToBean<>();

		list = csvToBean.parse(strategy, csvReader);
		// System.out.println("Size of list " + list.size());
//		for (RegDataCSVDto o : list) {
//			System.out.println(o.getFullName());
//		}

		return list;
	}

}
