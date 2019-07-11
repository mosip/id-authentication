package worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import com.google.gson.Gson;

import pt.dto.auth.AuthCSVEntity;
import pt.dto.auth.DemoAuthEntity;
import pt.dto.auth.EncryptionEntity;
import pt.dto.auth.IdentityRequest;
import pt.dto.auth.Data;
import pt.dto.auth.Identity;
import pt.dto.auth.UinDto;
import pt.dto.encrypted.AuthEntity;
import pt.dto.encrypted.Key;
import pt.dto.unencrypted.EncryptionResponse;
import pt.util.CSVUtil;
import pt.util.HTTPUtil;
import pt.util.JSONUtil;
import pt.util.PropertiesUtil;
import pt.util.SequentialNumberGenerator;
import pt.util.YMLUtil;

public class DemoAuthDataGenerator {

	// private static Integer counter = 100001;

	public DemoAuthDataGenerator() {

	}

	public void processData() throws FileNotFoundException {
		// D:/MOSIP/IDA/Data-Generation POC/Sprint 8_POC
		// String BASE_PATH = PropertiesUtil.BASE_PATH;
		String csvFile = PropertiesUtil.DATA_FILE;
		String csvFilepath = PropertiesUtil.BASE_PATH + File.separator + csvFile;
		Gson gson = new Gson();
		List<AuthCSVEntity> csvData = CSVUtil.loadCSVData(csvFilepath);
		int counter = 1;
		Util util = new Util();
		for (AuthCSVEntity authCsvEntity : csvData) {
			String filename = "authdata" + counter + ".json";
			System.out.println(gson.toJson("authCsvEntity"));
			System.out.println(gson.toJson(authCsvEntity));
			String JSONFILE = "/auth/demo_auth_data.json";
			DemoAuthEntity authEntity = JSONUtil.mapJSONToObject(JSONFILE);
			// System.out.println(gson.toJson(authEntity));

			authEntity.setRegistrationId(util.generateRegistrationId());
			authEntity.setTimestamp(util.generateCurrTimestamp());
			String uin = util.generateUin();
			System.out.println("Generated UIN is " + uin);
			IdentityRequest authRequest = authEntity.getRequest();
			Identity identity = authRequest.getIdentity();

			identity.getFullName();
			identity.getDateOfBirth();

			String dateOfBirth = authCsvEntity.getDateOfBirth();
			dateOfBirth = util.formatDob(dateOfBirth); // format to yyyy/mm/DD
			int age = util.calculateAge(dateOfBirth);

			identity.setDateOfBirth(dateOfBirth);
			identity.setAge(age);
			identity.setPostalCode(authCsvEntity.getPostalCode());
			identity.setPhone(authCsvEntity.getPhone1() + authCsvEntity.getPhone2());
			identity.setEmail(authCsvEntity.getEmail() + "@mail.com");

			List<Data> fullName = identity.getFullName();
			Data fullNameData = fullName.get(0);
			fullNameData.setValue(authCsvEntity.getFirstName() + " " + authCsvEntity.getLastName());
			fullName.set(0, fullNameData);
			identity.setFullName(fullName);

			List<Data> addressLine1 = identity.getAddressLine1();
			Data addressLine1Data = addressLine1.get(0);
			addressLine1Data.setValue(authCsvEntity.getAddressLine1());
			addressLine1.set(0, addressLine1Data);
			identity.setAddressLine1(addressLine1);

			List<Data> addressLine2 = identity.getAddressLine2();
			Data addressLine2Data = addressLine2.get(0);
			addressLine2Data.setValue(authCsvEntity.getAddressLine2());
			addressLine2.set(0, addressLine2Data);
			identity.setAddressLine2(addressLine2);

			List<Data> addressLine3 = identity.getAddressLine3();
			Data addressLine3Data = addressLine3.get(0);
			addressLine3Data.setValue(authCsvEntity.getAddressLine3());
			addressLine3.set(0, addressLine3Data);
			identity.setAddressLine3(addressLine3);

			List<Data> region = identity.getRegion();
			Data regionData = region.get(0);
			regionData.setValue(authCsvEntity.getRegion());
			region.set(0, regionData);
			identity.setRegion(region);

			System.out.println("region");
			System.out.println(gson.toJson(identity.getRegion()));

			List<Data> province = identity.getProvince();
			Data provinceData = province.get(0);
			provinceData.setValue(authCsvEntity.getProvince());
			province.set(0, provinceData);
			identity.setProvince(province);

			System.out.println("province");
			System.out.println(gson.toJson(province));

			List<Data> city = identity.getCity();
			Data cityData = city.get(0);
			cityData.setValue(authCsvEntity.getCity());
			city.set(0, cityData);
			identity.setCity(city);

			System.out.println("city");
			System.out.println(gson.toJson(city));

			System.out.println("identity");
			System.out.println(gson.toJson(identity));

			authRequest.setIdentity(identity);
			authEntity.setRequest(authRequest);

			String requestFolder = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator
					+ "requests";
			checkIfDirectoryExists(requestFolder);
			String filepath = requestFolder + File.separator + filename;
			JSONUtil.writeJSONToFile(gson.toJson(authEntity), filepath);

			String responseFileDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator
					+ "responses";
			checkIfDirectoryExists(responseFileDir);
			String responseFilePath = responseFileDir + File.separator + filename;
			String idvId = util.addIdentity(authEntity, responseFilePath, uin);
			System.out.println("idvId generated is " + idvId);
			EncryptionResponse encryptedData = util.encryptAddressAuthRequest(identity);

			String authRequestDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator
					+ "authRequests";

			checkIfDirectoryExists(authRequestDir);

			String authRequestFilePath = authRequestDir + File.separator + filename;
			try {
				generateAuthData(idvId, encryptedData, authRequestFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (counter++ == 1)
				break;
		}

	}

	private void checkIfDirectoryExists(String directory) {
		File f = new File(directory);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	private void generateAuthData(String idvId, EncryptionResponse encryptedData, String authRequestFilePath)
			throws Exception {
		Gson gson = new Gson();
		AuthEntity authEntity = YMLUtil.loadRequestData();
		authEntity.setIdvId(idvId);
		Key key = authEntity.getKey();
		key.setSessionKey(encryptedData.getKey());
		authEntity.setKey(key);
		authEntity.setRequest(encryptedData.getData());
		authEntity.setReqTime(getCurrTime());
		authEntity.setTspID(PropertiesUtil.TSPID); // TODO ; Read from properties file
		JSONUtil.writeJSONToFile(gson.toJson(authEntity), authRequestFilePath);
	}

	private String getCurrTime() {
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSS+05:30";
		Calendar now = Calendar.getInstance();
		Date date = now.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDate = dateFormat.format(date);
		// System.out.println(strDate);
		return strDate;
	}

}
