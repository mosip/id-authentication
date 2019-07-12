package worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;

import pt.dto.auth.demo.AddressIdentity;
import pt.dto.auth.demo.AddressIdentityRequest;
import pt.dto.auth.demo.Data;
import pt.dto.auth.demo.DemoAuthEntity;
import pt.dto.auth.demo.EncryptionEntity;
import pt.dto.auth.demo.Identity;
import pt.dto.auth.demo.IdentityRequest;
import pt.dto.auth.demo.PersonalIdAuthEntity;
import pt.dto.encrypted.AuthEntity;
import pt.dto.encrypted.Key;
import pt.dto.idRepo.ResponseData;
import pt.dto.idRepo.ResponseEntity;
import pt.dto.idvid.UinDto;
import pt.dto.idvid.VidDto;
import pt.dto.unencrypted.AddressAuthEntity;
import pt.dto.unencrypted.EncryptionResponse;
import pt.util.HTTPUtil;
import pt.util.JSONUtil;
import pt.util.PropertiesUtil;
import pt.util.SequentialNumberGenerator;
import pt.util.YMLUtil;

public class Util {

	public String generateCurrTimestamp() {
		String timestamp = "";
		String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		Date currDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		cal.add(Calendar.HOUR, -6);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
		timestamp = sdf.format(date);
		// System.out.println(timestamp);
		timestamp = timestamp + "Z";
		return timestamp;
	}

	public String generateUin() {
		Gson gson = new Gson();
		String uin = "";
		String type = "GET";
		String url = "https://integ.mosip.io/uingenerator/v1.0/uin";
		String response = "";
		try {
			response = HTTPUtil.sendHttpsRequest(url, "", type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print("Response of UIN generation request: ");
		System.out.println(response);
		if (!response.isEmpty()) {
			UinDto uinDto = gson.fromJson(response, UinDto.class);
			uin = uinDto.getUin();
			// System.out.println("uin generated is: " + uin);
		}
		return uin;

	}

	public String generateVidFromUin(String uin) {
		String vid = "";
		Gson gson = new Gson();
		String type = "GET";
		String url = "https://integ.mosip.io/identity/0.8/vid/" + uin;
		String errorMessage = "errorMessage";
		String response = "";
		try {
			response = HTTPUtil.sendHttpsRequest(url, "", type);
			System.out.println("Response of vid generation request is");
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print("Response of VID generation request: ");
		System.out.println(response);
		if (!response.isEmpty() && !response.contains(errorMessage)) {

			VidDto vidDto = gson.fromJson(response, VidDto.class);
			vid = vidDto.getVid();

		}
		return vid;
	}

	public String generateRegistrationId() {

		String regId = "";
		String part0 = "123456789";
		String part1 = "200001";
		part1 = sixCharRandomNumber();
		String part2 = currYYYY();
		String part3 = currMM();
		String part4 = currDD();

		String part5 = currentHHmmss();
		regId = part0 + part1 + part2 + part3 + part4 + part5;
		// System.out.println(regId);
		return regId;

	}

	public String sixCharRandomNumber() {
		Integer number = 0;
		number = SequentialNumberGenerator.generateNext();
		// System.out.print(number + " ");
		return number.toString();
	}

	public String currentHHmmss() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String currTime = sdf.format(date);
		// currTime = currTime.replaceAll(":", "");
		// System.out.println(currTime);
		return currTime;
	}

	public String currDD() {
		Date date = new Date();
		System.out.println(date.toString());
		SimpleDateFormat sdf = new SimpleDateFormat("DD");
		String currDate = sdf.format(date);
		Integer date1 = Calendar.getInstance().get(Calendar.DATE);
		if (date1 < 10)
			currDate = "0" + date1;
		else
			currDate = String.valueOf(date1);
//		System.out.println(date1);
		return currDate;
	}

	public String currMM() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		String month = sdf.format(date);
		// Integer month = Calendar.getInstance().get(Calendar.MONTH);
		// System.out.print(month);
		return month;
	}

	public String currYYYY() {
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		// System.out.print(year);
		return year.toString();
	}

	public String addIdentity(DemoAuthEntity authEntity, String responseFile, String uin) {

		Gson gson = new Gson();

		String response = "";
		String requestData = gson.toJson(authEntity);
		String url = "https://integ.mosip.io/idrepo/identity/v1.0/" + uin;
		System.out.println("URL for creating an identity:-");
		System.out.println(url);
		String type = "POST";
		System.out.println("Request data of create request");
		System.out.println(requestData);
		try {
			response = HTTPUtil.sendHttpsRequest(url, requestData, type);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// String responseFileDir = "D:\\MOSIP\\IDA\\Data-Generation
		// POC\\JSONs\\Generated\\Responses";

		JSONUtil.writeJSONToFile(response, responseFile);
		System.out.println("response of create request ");
		System.out.println(response);
		ResponseEntity responseEntity = gson.fromJson(response, ResponseEntity.class);
		ResponseData rData = responseEntity.getResponse();
		if (rData != null) {
			String entity = rData.getEntity();
			// System.out.println(entity);
			String[] arr = entity.split("/");
			String idvId = arr[arr.length - 1];
			return idvId;
		} else {
			return "ERROR";
		}

	}

	public EncryptionResponse encryptAuthData(IdentityRequest authRequest, String tspId) {

		Gson gson = new Gson();

		EncryptionEntity entity = new EncryptionEntity();
		entity.setIdentityRequest(authRequest);
		entity.setTspID(tspId);
		EncryptionResponse encResponse = null;
		String url = "http://localhost:8081/identity/identity/encrypt";
		String type = "POST";
		String response = "";
		System.out.println("Encryption request data is ");
		// System.out.println(gson.toJson(entity));
		String encReqDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator
				+ "encryptionRequest";

		File f = new File(encReqDir);
		if (!f.exists()) {
			f.mkdirs();
		}

		String file = encReqDir + File.separator + "encRequest.json";
		JSONUtil.writeJSONToFile(gson.toJson(entity), file);

		try {
			response = HTTPUtil.sendHttpRequest(url, gson.toJson(entity), type);
			// System.out.println("Encryption response is ");
			// System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}

		file = encReqDir + File.separator + "encResponse.json";
		JSONUtil.writeJSONToFile(response, file);

		encResponse = gson.fromJson(response, EncryptionResponse.class);
//		System.out.println("Key is :- " + encResponse.getKey());
//		System.out.println("Data is :- " + encResponse.getData());

		return encResponse;
	}

	public String formatDob(String dateOfBirth) {
		// original format MM/dd/yyyy
		// target format: yyyy/MM/dd
		String formattedDate = "";
//		String format = "MM/dd/yyyy";
//		String targetFormat = "yyyy/MM/dd";
//		SimpleDateFormat sdf = new SimpleDateFormat(format);
//		Date date =sdf.parse(dateOfBirth);

		String[] dateComponents = dateOfBirth.split("/");
		DecimalFormat formatter = new DecimalFormat("00");
		System.out.println(dateComponents[0]);
//		String MM = String.format("%02d", dateComponents[0]);
//		String dd = String.format("%02d", dateComponents[1]);

		String MM = formatter.format(new Integer(dateComponents[0]));
		String dd = formatter.format(new Integer(dateComponents[1]));

		String yyyy = dateComponents[2];
		formattedDate = yyyy + "/" + MM + "/" + dd;

		return formattedDate;
	}

	public int calculateAge(String dateOfBirth) {
		String format = "yyyy/MM/dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(dateOfBirth);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		Integer currYear = Calendar.getInstance().get(Calendar.YEAR);
		int age = currYear - year;
		System.out.println(currYear + " - " + year + " = " + age);

		return age;
	}

	public EncryptionResponse encryptAddressAuthRequest(Identity identity) {

		Gson gson = new Gson();
		List<Data> addressLine1 = identity.getAddressLine1();
		List<Data> addressLine2 = identity.getAddressLine2();
		List<Data> addressLine3 = identity.getAddressLine3();

		AddressIdentity addIdentity = new AddressIdentity();
		addIdentity.setAddressLine1(addressLine1);
		addIdentity.setAddressLine2(addressLine2);
		addIdentity.setAddressLine3(addressLine3);

		AddressIdentityRequest identityRequest = new AddressIdentityRequest(addIdentity);
		String TSPID = PropertiesUtil.TSPID;

		pt.dto.auth.demo.AddressAuthEntity authEntity = new pt.dto.auth.demo.AddressAuthEntity(identityRequest, TSPID);

		System.out.println(gson.toJson(authEntity));

		EncryptionResponse encResponse = null;
		String url = "http://localhost:8081/identity/identity/encrypt";
		String type = "POST";
		String response = "";
		// System.out.println("Encryption request data is ");
		// System.out.println(gson.toJson(entity));
		String encReqDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator + "encrypted";

		File f = new File(encReqDir);
		if (!f.exists()) {
			f.mkdirs();
		}

		String file = encReqDir + File.separator + "encRequest.json";
		JSONUtil.writeJSONToFile(gson.toJson(authEntity), file);

		try {
			response = HTTPUtil.sendHttpRequest(url, gson.toJson(authEntity), type);
			// System.out.println("Encryption response is ");
			// System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}

		file = encReqDir + File.separator + "encResponse.json";
		JSONUtil.writeJSONToFile(response, file);

		encResponse = gson.fromJson(response, EncryptionResponse.class);

		return encResponse;

	}

	public File[] getAllFilesInDirectory(String directory) {
		File file = new File(directory);
		File[] files = file.listFiles();
//        for(File f: files){
//            System.out.println(f.getName());
//        }
		return files;
	}

	public EncryptionResponse encryptPersonalIdentityData(PersonalIdAuthEntity personalIdAuthEntity, String filename) {

		Gson gson = new Gson();
		EncryptionResponse encResponse = null;

		String url = "http://localhost:8081/identity/identity/encrypt";
		String type = "POST";
		String response = "";

		String basePath = PropertiesUtil.BASE_PATH;
		String encReqDir = basePath + File.separator + "Generated" + File.separator + "personalIdAuth" + File.separator
				+ "encrypted" + File.separator + "requests";
		File f = new File(encReqDir);
		if (!f.exists()) {
			f.mkdirs();
		}

		String encResponseDir = basePath + File.separator + "Generated" + File.separator + "personalIdAuth"
				+ File.separator + "encrypted" + File.separator + "responses";
		f = new File(encResponseDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		String encRequestPath = encReqDir + File.separator + filename;
		JSONUtil.writeJSONToFile(gson.toJson(personalIdAuthEntity), encRequestPath);

		try {
			response = HTTPUtil.sendHttpRequest(url, gson.toJson(personalIdAuthEntity), type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String encResponsePath = encResponseDir + File.separator + filename;
		JSONUtil.writeJSONToFile(response, encResponsePath);

		encResponse = gson.fromJson(response, EncryptionResponse.class);

		return encResponse;
	}

	public void generatePersonalIdAuthData(EncryptionResponse encryptedData, String filePath, String idvId) {

		Gson gson = new Gson();

		// String filePath = authDataDir + File.separator + "authData.json";

		try {
			System.out.println();
			pt.dto.encrypted.PersonalIdAuthEntity authEntity = YMLUtil.loadPersonalIdRequestData();
			authEntity.setIdvId(idvId);
			Key key = authEntity.getKey();
			key.setSessionKey(encryptedData.getEncryptedSessionKey());
			authEntity.setKey(key);
			authEntity.setRequest(encryptedData.getEncryptedIdentity());
			authEntity.setReqTime(getCurrTime());
			authEntity.setTspID(PropertiesUtil.TSPID);
			JSONUtil.writeJSONToFile(gson.toJson(authEntity), filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	public String extractVidFromAddressAuthRequest(String addressAuthRequestFile) throws Exception {

		try {
			AuthEntity authEntity = JSONUtil.mapExternalJsonToAddressAuthEntity(addressAuthRequestFile);

			if ("V".equals(authEntity.getIdvIdType())) {
				String vid = authEntity.getIdvId();
				return vid;
			}
			return "vid not found";
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		}

	}

}
