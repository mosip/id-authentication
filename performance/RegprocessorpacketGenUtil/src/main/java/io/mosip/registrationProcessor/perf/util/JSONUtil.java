/**
 * 
 */
package io.mosip.registrationProcessor.perf.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.registrationProcessor.perf.regPacket.dto.RegProcIdDto;

/**
 * @author Gaurav Sharan
 *
 */
public class JSONUtil {

	public static RegProcIdDto mapJsonFileToObject() {
		String jsonFile = "/sampleJson/ID.json";
		RegProcIdDto obj = null;
		Gson gson = new Gson();
		InputStream in = JSONUtil.class.getResourceAsStream(jsonFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		obj = gson.fromJson(br, RegProcIdDto.class);
		return obj;
	}

	public static void writeJsonToFile(String data, String filePath) {

		try (FileWriter file = new FileWriter(filePath)) {

			file.write(data);
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject loadJsonFromFile(String packetMetaInfoFile) {

		Gson gson = new Gson();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(packetMetaInfoFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		JSONObject json = gson.fromJson(br, JSONObject.class);

		return json;
	}

	public static void writeJSONToFile(String packetMetaInfoFile, JSONObject jsonObject) {

		try (FileWriter file = new FileWriter(packetMetaInfoFile)) {
			file.write(jsonObject.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
