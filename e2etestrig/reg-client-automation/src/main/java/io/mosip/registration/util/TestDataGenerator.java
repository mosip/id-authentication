package io.mosip.registration.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

/*import org.yaml.snakeyaml.Yaml;
*/
import io.mosip.registration.DTO.YamlDTO;

/**
 * @author Arjun chandramohan
 *
 */
@Service
public class TestDataGenerator {
	/**
	 * @param serviceName
	 * @param testDataFileName
	 * @param inputDataParameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getYamlData(String serviceName, String testDataFileName, String input, String property) {
		String inputDataParameter = input + "_" + property;
		Yaml yaml = new Yaml();
		String testdata = null;
		InputStream inputStream = null;
		String testDataFilePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator
				+ serviceName + File.separator + testDataFileName + ".yaml";
		try {
			inputStream = new FileInputStream(testDataFilePath);
			if (inputDataParameter.contains("_empty"))
				return "";
			if (inputDataParameter.contains("_space"))
				return " ";
			if (inputDataParameter.contains("_null"))
				return null;
			YamlDTO obj = new YamlDTO();
			obj.setYamlObject((Map<String, List<Object>>) yaml.load(inputStream));
			List<Object> list = obj.getYamlObject().get(inputDataParameter);
			Random random = new Random();
			testdata = (String) list.get(random.nextInt(list.size())).toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return testdata;

	}
}
