package io.mosip.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;

/**
 * @author Arjun Chandramohan
 *
 */
public class GenerateJsonPath {

	/** this method generate the Json dynamic path in Api folder automatically
	 * @param modulename
	 * @param apiname
	 * @param requestjsonname
	 * @throws JSONException 
	 */
	public void generatePath(String modulename, String apiname, String requestjsonname) throws JSONException {
		String inputFilePath = "src/test/resources/" + modulename + "/" + apiname + "/" + requestjsonname + ".json";
		JsonPathGenerationIDRepo jsonPath;
		try {
			jsonPath = new JsonPathGenerationIDRepo(new String(Files.readAllBytes(Paths.get(inputFilePath))));
			jsonPath.generateJsonMappingDic("src/test/resources/" + modulename + "/" + apiname + "/" + requestjsonname
					+ "Path" + ".properties");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static void main(String args[]) throws JSONException {
		new GenerateJsonPath().generatePath("IdRepo", "StoreIdData", "RequestMasterJsonStructure");
	}
	

}
