package io.mosip.registrationProcessor.perf.service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.registrationProcessor.perf.dto.RegDataCSVDto;
import io.mosip.registrationProcessor.perf.regPacket.dto.FieldData;
import io.mosip.registrationProcessor.perf.regPacket.dto.Identity;
import io.mosip.registrationProcessor.perf.regPacket.dto.RegProcIdDto;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.TestDataUtility;
import io.mosip.resgistrationProcessor.perf.dbaccess.RegProcPerfDaoImpl;

public class PacketDemoDataUtil {

	Gson gson = new Gson();

	private RegProcPerfDaoImpl dao;

	private TestDataUtility testDataUtility;

	private String GOOGLE_API_KEY = "AIzaSyCm--C8RpN6FvNQtHtPKdtM20_k0R0284M";
	private String GOOGLE_LANG_CODE_ENG = "en";
	private String GOOGLE_LANG_CODE_FR = "fr";
	private String GOOGLE_LANG_CODE_AR = "ar";
	private String LANG_CODE_FR = "fra";
	private String LANG_CODE_AR = "ara";
	private String LANG_CODE_ENG = "eng";

	public PacketDemoDataUtil() {
		dao = new RegProcPerfDaoImpl();
		testDataUtility = new TestDataUtility();
	}

	private String translateText(String sourceText, String sourceLang, String destLang) {
		// English en
		// French fr
		// Arabic ar
		// Hindi hi
//		String text = null;
//		GoogleTranslate translator = new GoogleTranslate(GOOGLE_API_KEY);
//		text = translator.translate(sourceText, sourceLang, destLang);
//		if (text == null) {
//			System.out.println("null is returned from Google Translator");
//			text = sourceText;
//		}
//		System.out.println("Google translated " + sourceText + " of source language " + sourceLang + " to " + text
//				+ " in " + destLang);
//		return text;

		return sourceText;
	}

	public RegProcIdDto modifyDemographicdata(RegDataCSVDto regData) {
		RegProcIdDto regProcDemodto = JSONUtil.mapJsonFileToObject();
		Identity identityDto = regProcDemodto.getIdentity();
		List<FieldData> fullName = new ArrayList<>();
		String fullNameEng = regData.getFullName();
		String fullNameFr = translateText(fullNameEng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String fullNameAr = translateText(fullNameEng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData fullNameFrData = new FieldData(LANG_CODE_FR, fullNameFr);
		FieldData fullNameArData = new FieldData(LANG_CODE_AR, fullNameAr);
		fullName.add(fullNameFrData);
		fullName.add(fullNameArData);
		identityDto.setFullName(fullName);

		identityDto.setDateOfBirth(regData.getDateOfBirth());
		identityDto.setAge(Integer.parseInt(regData.getAge()));

		List<FieldData> gender = new ArrayList<>();
		String genderFr = testDataUtility.frenchTexts.get(regData.getGender());
		String genderAr = testDataUtility.arabicTexts.get(regData.getGender());
		FieldData genderFrData = new FieldData(LANG_CODE_FR, genderFr);
		FieldData genderArData = new FieldData(LANG_CODE_AR, genderAr);
		gender.add(genderFrData);
		gender.add(genderArData);
		identityDto.setGender(gender);

		List<FieldData> residenceStatus = new ArrayList<>();
		String resStatusFr = testDataUtility.frenchTexts.get(regData.getResidenceStatus());
		String resStatusAr = testDataUtility.arabicTexts.get(regData.getResidenceStatus());
		FieldData resStatusFrData = new FieldData(LANG_CODE_FR, resStatusFr);
		FieldData resStatusArData = new FieldData(LANG_CODE_AR, resStatusAr);
		residenceStatus.add(resStatusFrData);
		residenceStatus.add(resStatusArData);
		identityDto.setResidenceStatus(residenceStatus);

		List<FieldData> addressLine1 = new ArrayList<>();
		String addressLine1_eng = regData.getAddressLine1();
		String addressLine1_fr = translateText(addressLine1_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String addressLine1_ar = translateText(addressLine1_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData addressLine1_frData = new FieldData(LANG_CODE_FR, addressLine1_fr);
		FieldData addressLine1_arData = new FieldData(LANG_CODE_AR, addressLine1_ar);
		addressLine1.add(addressLine1_frData);
		addressLine1.add(addressLine1_arData);
		identityDto.setAddressLine1(addressLine1);

		List<FieldData> addressLine2 = new ArrayList<>();
		String addressLine2_eng = regData.getAddressLine2();
		String addressLine2_fr = translateText(addressLine2_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String addressLine2_ar = translateText(addressLine2_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData addressLine2_frData = new FieldData(LANG_CODE_FR, addressLine2_fr);
		FieldData addressLine2_arData = new FieldData(LANG_CODE_AR, addressLine2_ar);
		addressLine2.add(addressLine2_frData);
		addressLine2.add(addressLine2_arData);
		identityDto.setAddressLine2(addressLine2);

		List<FieldData> addressLine3 = new ArrayList<>();
		String addressLine3_eng = regData.getAddressLine3();
		String addressLine3_fr = translateText(addressLine3_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String addressLine3_ar = translateText(addressLine3_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData addressLine3_frData = new FieldData(LANG_CODE_FR, addressLine3_fr);
		FieldData addressLine3_arData = new FieldData(LANG_CODE_AR, addressLine3_ar);
		addressLine3.add(addressLine3_frData);
		addressLine3.add(addressLine3_arData);
		identityDto.setAddressLine3(addressLine3);

		List<FieldData> region = new ArrayList<>();
		String regionValFr = convertLocationEngToFrench(regData.getRegion(), 1);
		System.out.println("regionValFr is");
		System.out.println(gson.toJson(regionValFr));
		String regionValAr = convertLocationEngToArabic(regData.getRegion(), 1);
		System.out.println("regionValAr is");
		System.out.println(gson.toJson(regionValAr));
		FieldData regionFr = new FieldData(LANG_CODE_FR, regionValFr);
		FieldData regionAr = new FieldData(LANG_CODE_AR, regionValAr);
		region.add(regionFr);
		region.add(regionAr);
		identityDto.setRegion(region);

		System.out.println("Region is");
		System.out.println(gson.toJson(region));

		List<FieldData> province = new ArrayList<>();
		String provinceValFr = convertLocationEngToFrench(regData.getProvince(), 2);
		String provinceValAr = convertLocationEngToArabic(regData.getProvince(), 2);
		FieldData provinceFr = new FieldData(LANG_CODE_FR, provinceValFr);
		System.out.println("provinceFr is");
		System.out.println(gson.toJson(provinceFr));
		FieldData provinceAr = new FieldData(LANG_CODE_AR, provinceValAr);
		System.out.println("provinceAr is");
		System.out.println(gson.toJson(provinceAr));
		province.add(provinceFr);
		province.add(provinceAr);
		identityDto.setProvince(province);

		System.out.println("Province is");
		System.out.println(gson.toJson(province));

		List<FieldData> city = new ArrayList<>();
		String cityValFr = convertLocationEngToFrench(regData.getCity(), 3);
		String cityValAr = convertLocationEngToArabic(regData.getCity(), 3);
		FieldData cityFr = new FieldData(LANG_CODE_FR, cityValFr);
		FieldData cityAr = new FieldData(LANG_CODE_AR, cityValAr);
		city.add(cityFr);
		city.add(cityAr);
		identityDto.setCity(city);

		System.out.println("city is");
		System.out.println(gson.toJson(city));

		List<FieldData> localAdministrativeAuthority = new ArrayList<>();
		String localAdministrativeAuthorityValFr = convertLocationEngToFrench(regData.getLocalAdministrativeAuthority(),
				4);
		String localAdministrativeAuthorityValAr = convertLocationEngToArabic(regData.getLocalAdministrativeAuthority(),
				4);
		FieldData localAdministrativeAuthorityFr = new FieldData(LANG_CODE_FR, localAdministrativeAuthorityValFr);
		FieldData localAdministrativeAuthorityAr = new FieldData(LANG_CODE_AR, localAdministrativeAuthorityValAr);
		localAdministrativeAuthority.add(localAdministrativeAuthorityFr);
		localAdministrativeAuthority.add(localAdministrativeAuthorityAr);
		identityDto.setLocalAdministrativeAuthority(localAdministrativeAuthority);

		System.out.println("localAdministrativeAuthority is");
		System.out.println(gson.toJson(localAdministrativeAuthority));

		identityDto.setPostalCode(regData.getPostalCode());
		identityDto.setPhone(regData.getPhone());
		identityDto.setEmail(regData.getEmail());

		identityDto.setCNIENumber(regData.getCnieNumber());

		regProcDemodto.setIdentity(identityDto);
		System.out.println(regProcDemodto);
		return regProcDemodto;
	}

	public String convertLocationEngToFrench(String locationName, int hierarchy_level) {
		String result = dao.getTranslatedLocation(locationName, LANG_CODE_FR, hierarchy_level);
		System.out.println(
				LANG_CODE_FR + " of " + locationName + " at hierarchy level " + hierarchy_level + " is " + result);
		return result;
	}

	public String convertLocationEngToArabic(String locationName, int hierarchy_level) {
		String result = dao.getTranslatedLocation(locationName, LANG_CODE_AR, hierarchy_level);
		System.out.println(
				LANG_CODE_AR + " of " + locationName + " at hierarchy level " + hierarchy_level + " is " + result);
		return result;
	}

	public String generateRegId(String centerId, String machineId) {
		String regID = "";
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		timeStamp.replaceAll(".", "");
		int n = 10000 + new Random().nextInt(90000);
		String randomNumber = String.valueOf(n);

		regID = centerId + machineId + randomNumber + timeStamp;
		return regID;
	}

	public void writeChecksumToFile(String file, String checksum) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		writer.print(checksum);
		writer.close();
	}

	public void logRegIdCheckSumToFile(String checkSumLogFile, String regId, String checksumStr, Long sizeInBytes,
			String center_machine_refID) {

		// String checkSumLogFile = "E:\\MOSIP_PT\\Data\\checksums.txt";
		try (FileWriter f = new FileWriter(checkSumLogFile, true);
				BufferedWriter b = new BufferedWriter(f);
				PrintWriter p = new PrintWriter(b);) {

			p.println(regId + "," + checksumStr + "," + sizeInBytes + "," + center_machine_refID);

		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void modifyPacketMetaInfo(String packetMetaInfoFile, String regId, String centerId, String machineId) {
		Gson gson = new Gson();
		JSONObject jsonObject = JSONUtil.loadJsonFromFile(packetMetaInfoFile);
		com.google.gson.internal.LinkedTreeMap identity = null;
		java.util.ArrayList metadata = null;
		for (Object key : jsonObject.keySet()) {
			if ("identity".equals((String) key)) {
				Object inObj = jsonObject.get(key);
				// if (inObj instanceof JSONObject) {
				identity = (com.google.gson.internal.LinkedTreeMap) inObj;
				for (Object key1 : identity.keySet()) {
					if ("metaData".equals((String) key1)) {
						Object metadataObj = identity.get(key1);
						// if (metadataObj instanceof JSONArray) {
						metadata = (java.util.ArrayList) metadataObj;
						break;
						// }
					}
				}

				break;
			}

		}

		for (int i = 0; i < metadata.size(); i++) {

			Map keyVal = (Map) metadata.get(i);
			String reqLabel = "creationDate";
			if (reqLabel.equals(keyVal.get("label"))) {
				keyVal.put("value", getCurrDate());
			}

		}
		identity.put("metaData", metadata);
		jsonObject.put("identity", identity);
		// System.out.println(identity);
		JSONUtil.writeJSONToFile(packetMetaInfoFile, jsonObject);
		// System.out.println(gson.toJson(jsonObject));

	}

	private String getCurrDate() {
		String timestamp = "";
		String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		Date currDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		// cal.add(Calendar.HOUR, -6);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
		timestamp = sdf.format(date);
		// System.out.println(timestamp);
		timestamp = timestamp + "Z";
		return timestamp;
	}

}
