package worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import pt.dto.idRepo.ResponseEntity;
import pt.util.CSVUtil;
import pt.util.JSONUtil;
import pt.util.PropertiesUtil;

public class IdvIdExtractor {

	public IdvIdExtractor() {

	}

	String path = PropertiesUtil.BASE_PATH + File.separator + "idvId.csv";

	public void extractIdvId() {
		Util util = new Util();
		String responseDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator + "responses";
		File[] responseJsons = util.getAllFilesInDirectory(responseDir);
		List<String> vIds = new ArrayList<>();
		List<String[]> data = new ArrayList<String[]>();
		data.add(new String[] { "idvId" });
		for (File file : responseJsons) {

			String responsePath = file.getAbsolutePath();
			// System.out.println(responsePath);
			ResponseEntity response = null;
			try {
				response = JSONUtil.mapExternalJsonToObject1(responsePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String entityUrl = response.getResponse().getEntity();
			String[] arr = entityUrl.split("/");
			String idvId = arr[arr.length - 1];
			// vIds.add(idvId);
			String[] el = { idvId };
			data.add(el);

		}

//		try {
//			CSVUtil.writeStringListToCsv(vIds, path);
//		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
//			e.printStackTrace();
//		}

		try {
			System.out.println("Writing vids to file " + path);
			CSVUtil.writeDataToCsv(data, path);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
