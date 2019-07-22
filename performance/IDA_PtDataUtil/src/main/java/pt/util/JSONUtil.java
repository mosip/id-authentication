package pt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import pt.dto.auth.demo.DemoAuthEntity;
import pt.dto.encrypted.AuthEntity;
import pt.dto.encrypted.PersonalIdAuthEntity;
import pt.dto.idRepo.ResponseEntity;

public class JSONUtil {

//	public static void writeJSONToFile(AuthEntity authEntity) {
//		Gson gson = new Gson();
//		String folder = "D:\\MOSIP\\IDA\\Data-Generation POC\\JSONs";
//		String filename = "addressAuth.json";
//		String path = folder + File.separator + filename;
//
//		try (FileWriter file = new FileWriter(path)) {
//
//			file.write(gson.toJson(authEntity));
//			file.flush();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	public static void writeJSONToFile(String data, String path) {
		// System.out.println(data);
		// System.out.println(path);
		try (FileWriter file = new FileWriter(path)) {

			file.write(data);
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static DemoAuthEntity mapJSONToObject_0() throws FileNotFoundException {
		Gson gson = new Gson();
		BufferedReader br = null;
		pt.dto.auth.demo.DemoAuthEntity authEntity = null;
		String folder = "D:\\MOSIP\\IDA\\Data-Generation POC";
		String filename = "address_data.json";
		String filepath = folder + File.separator + filename;
		// System.out.println(filepath);

		br = new BufferedReader(new FileReader(filepath));

		authEntity = gson.fromJson(br, pt.dto.auth.demo.DemoAuthEntity.class);
		// System.out.println(gson.toJson(authEntity));

		return authEntity;
	}

	public static DemoAuthEntity mapJSONToObject(String jsonFile) {
		DemoAuthEntity authEntity = null;
		try {
			Gson gson = new Gson();
			InputStream in = JSONUtil.class.getResourceAsStream(jsonFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			authEntity = gson.fromJson(br, pt.dto.auth.demo.DemoAuthEntity.class);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return authEntity;
	}

	public static DemoAuthEntity mapExternalJsonToObject(String filepath) throws FileNotFoundException {
		DemoAuthEntity authEntity = null;
		Gson gson = new Gson();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(filepath));

		authEntity = gson.fromJson(br, pt.dto.auth.demo.DemoAuthEntity.class);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return authEntity;
	}

	public static ResponseEntity mapExternalJsonToObject1(String filepath) throws FileNotFoundException {
		ResponseEntity authEntity = null;
		Gson gson = new Gson();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(filepath));

		authEntity = gson.fromJson(br, ResponseEntity.class);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return authEntity;
	}

	public static AuthEntity mapExternalJsonToAddressAuthEntity(String filepath) throws FileNotFoundException {
		AuthEntity authEntity = null;
		Gson gson = new Gson();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(filepath));

		authEntity = gson.fromJson(br, AuthEntity.class);
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return authEntity;
	}

	public static PersonalIdAuthEntity mapExternalJsonToPersonalIdAuthEntity(String filepath)
			throws FileNotFoundException {
		PersonalIdAuthEntity authEntity = null;
		Gson gson = new Gson();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(filepath));

		authEntity = gson.fromJson(br, PersonalIdAuthEntity.class);

		return authEntity;
	}

}
