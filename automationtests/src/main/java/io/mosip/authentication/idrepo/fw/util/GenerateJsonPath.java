package io.mosip.authentication.idrepo.fw.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.mosip.util.JsonPathGen;

/**
 * @author Arjun Chandramohan
 *
 */
public class GenerateJsonPath {

	/** this method generate the Json dynamic path in Api folder automatically
	 * @param modulename
	 * @param apiname
	 * @param requestjsonname
	 */
	public void generatePath(String modulename, String apiname, String requestjsonname) {
		String inputFilePath = "src/test/resources/" + modulename + "/" + apiname + "/" + requestjsonname + ".json";
		JsonPathGen jsonPath;
		try {
			jsonPath = new JsonPathGen(new String(Files.readAllBytes(Paths.get(inputFilePath))));
			jsonPath.generateJsonMappingDic("src/test/resources/" + modulename + "/" + apiname + "/" + requestjsonname
					+ "Path" + ".properties");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
