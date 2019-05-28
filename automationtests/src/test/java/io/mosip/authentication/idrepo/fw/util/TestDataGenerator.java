package io.mosip.authentication.idrepo.fw.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.yaml.snakeyaml.Yaml;

import io.mosip.testDataDTO.YamlDTO;

/**
 * @author Arjun chandramohan
 *
 */
public class TestDataGenerator {
	/**
	 * @param modulename
	 * @param apiname
	 * @param testData
	 * @param dataParam
	 * @return
	 */
		@SuppressWarnings("unchecked")
	public String getYamlData(String modulename, String apiname, String testData, String dataParam) {
		Yaml yaml = new Yaml();
		String testdata = null;
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(
					"src/test/resources/" + modulename + "/" + apiname + "/" + testData + ".yaml");
			YamlDTO obj = new YamlDTO();
			obj.setYamlObject((Map<String, List<Object>>) yaml.load(inputStream));
			List<Object> list = obj.getYamlObject().get(dataParam);
			Random random = new Random();
			testdata = (String) list.get(random.nextInt(list.size())).toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return testdata;
	}
}
