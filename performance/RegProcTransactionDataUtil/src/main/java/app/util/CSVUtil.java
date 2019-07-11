package app.util;

import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CSVUtil {

	public static List<String> loadRegIds(String filePath) {

		List<String> list = new ArrayList<String>();
		try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
				CSVReader csvReader = new CSVReader(reader);) {

			String[] nextRecord = null;
			while ((nextRecord = csvReader.readNext()) != null) {
				list.add(nextRecord[0]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(list.size());
		return list;

	}

}
