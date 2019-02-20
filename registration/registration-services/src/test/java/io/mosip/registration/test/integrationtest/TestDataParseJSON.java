package io.mosip.registration.test.integrationtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/*public class TestDataParseJSON 
{

	private JSONObject file;

    public TestDataParseJSON(String fileName) throws IOException {
                   try {
                                 this.file = (JSONObject) new JSONParser()
                                                              .parse(new FileReader(new File(fileName)));
                   } catch (FileNotFoundException e) {
                                 e.printStackTrace();
                   } catch (ParseException e) {
                                 e.printStackTrace();
                   }
    }

    public String getDataFromJsonViaKey(String keyName) {
                   try {
					return this.file.get(keyName).toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return keyName;
    }

	
}*/

public class TestDataParseJSON {

	private JSONArray file;

	public TestDataParseJSON(String fileName) {
		super();
		try {
			this.file = (JSONArray) new JSONParser().parse(new FileReader(new File(fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getDataFromJsonViaKey(String keyName) {

		Random rand = new Random();
		int n = rand.nextInt(this.file.size());
		JSONObject data = (JSONObject) this.file.get(n);
		return data.getAsString(keyName);

	}

	/*
	 * public static void main(String[] args) throws IOException { TestDataParseJSON
	 * testdataparsejson=new TestDataParseJSON(
	 * "src/test/resources/testData/MasterSyncServiceData/testData.json"); String
	 * result = testdataparsejson.getDataFromJsonViaKey("hierarchyCode");
	 * System.out.println(result); }
	 */

}
