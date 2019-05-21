package io.mosip.authentication.testdata.keywords;

import java.io.File; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import io.mosip.authentication.fw.dto.ErrorsDto;
import io.mosip.authentication.fw.dto.UinStaticPinDto;
import io.mosip.authentication.fw.dto.VidStaticPinDto;
import io.mosip.authentication.fw.precon.XmlPrecondtion;
import io.mosip.authentication.fw.util.EncryptDecrptUtil;
import io.mosip.authentication.fw.util.IdRepoUtil;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.mosip.authentication.testdata.TestDataConfig;
import io.mosip.authentication.testdata.TestDataProcessor;
import io.mosip.authentication.testdata.TestDataUtil;

public class IdRepoKeywordUtil extends KeywordUtil{

	private static Map<String,String> currentTestData = new HashMap<String,String>();
	/**
	 * The method return precondtion message or keyword from yml test data file
	 * 
	 * @return map
	 */
	@Override
	public Map<String, String> precondtionKeywords(Map<String, String> map) {
		TreeMap<String, String> returnMap = getSortedTreeMap(map);
		boolean flag = false;
		for (Entry<String, String> entry : returnMap.entrySet()) {
			if (entry.getValue().contains("TOKENID") && entry.getValue().startsWith("$TOKENID")) {
				String[] keys = entry.getValue().split(Pattern.quote("~"));
				Map<String, String> tempmap = new HashMap<String, String>();
				tempmap.put("uin", keys[1]);
				tempmap.put("tspId", keys[2]);
				Map<String, String> dic = precondtionKeywords(tempmap);
				String pid = dic.get("tspId").toString();
				if (pid.contains("/"))
					pid = pid.substring(0, pid.indexOf("/"));
				returnMap.put(entry.getKey(), RunConfigUtil.getTokenId(dic.get("uin"), pid));
			} else if (entry.getValue().contains("TestData:") && !entry.getValue().contains("+")
					&& entry.getValue().startsWith("$TestData:")) {
				String dataParam = entry.getValue().replace("$", "").replace("TestData:", "");
				returnMap.put(entry.getKey(), TestDataProcessor.getYamlData("idRepository", "TestData",
						"RunConfig/authenitcationTestdata", dataParam));
			} else if (entry.getValue().contains("$errors:") && entry.getValue().startsWith("$errors:")) {
				String value = entry.getValue().replace("$", "");
				String[] key = value.split(":");
				String scenario = key[1];
				String expValue = key[2];
				returnMap.put(entry.getKey(),
						ErrorsDto.getErrors().get("errors").get(scenario).get(expValue).toString());
			} else if (entry.getValue().contains("PIDMLKURL:")) {
				String value = entry.getValue().replace("$", "");
				String[] key = value.split(":");
				returnMap.put(entry.getKey(), RunConfigUtil.getPartnerIDMispLKValue(key[1]));
			} else if (entry.getValue().equals("$TIMESTAMP$")) {
				if (!entry.getKey().startsWith("output."))
					returnMap.put(entry.getKey(), generateCurrentTimeStampWithTimeZone());
				else
					returnMap.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue().contains("$TIMESTAMP$")) {
				String temp = entry.getValue().replace("$TIMESTAMP$", "");
				if (temp.contains("+")) {
					String[] time = temp.split(Pattern.quote("+"));
					String calType = time[0];
					int number = Integer.parseInt(time[1]);
					returnMap.put(entry.getKey(), generateTimeStamp(calType, "+", number));
				} else if (temp.contains("-")) {
					String[] time = temp.split("-");
					String calType = time[0];
					int number = Integer.parseInt(time[1]);
					returnMap.put(entry.getKey(), generateTimeStamp(calType, "-", number));
				}
			} else if (entry.getValue().contains("$TIMESTAMPZ$")) {
				if (!entry.getKey().startsWith("output."))
					returnMap.put(entry.getKey(), generateTimeStampWithZTimeZone());
				else
					returnMap.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue().contains("$INVALIDTIMESTAMPZ$")) {
				returnMap.put(entry.getKey(), generateInvalidTimeStamp());
			} else if (entry.getValue().contains("$") && entry.getValue().contains(":")
					&& (entry.getValue().startsWith("$input") || entry.getValue().startsWith("$end")
							|| entry.getValue().startsWith("$output"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split(":");
				String jsonFileName = keys[0];
				String fieldName = keys[1];
				String val=null;
				if (TestDataUtil.getCurrTestDataDic()!=null && TestDataUtil.getCurrTestDataDic().containsKey(jsonFileName))
					val = TestDataUtil.getCurrTestDataDic().get(jsonFileName).get(fieldName);
				else
					val=currentTestData.get(fieldName).toString();
				returnMap.put(entry.getKey(), val);
			} else if (entry.getValue().contains("$") && entry.getValue().startsWith("$idrepo")
					&& !entry.getValue().contains("+")
					&& !(entry.getValue().contains("DECODE:") || entry.getValue().contains("DECODEFILE:"))) {
				String[] keys = entry.getValue().split("~");
				String findUinKeyword = keys[1];
				String mappingName = keys[2].replace("$", "");
				if (mappingName.contains("TestData:")) {
					Map<String, String> temp = new HashMap<String, String>();
					String key[] = mappingName.split(":");
					temp.put("getValue", "$TestData:" + key[key.length - 1] + "$");
					mappingName = precondtionKeywords(temp).get("getValue");
					mappingName = key[0] + ":" + key[1] + ":" + mappingName;
				}
				if (mappingName.contains("input.")) {
					Map<String, String> temp = new HashMap<String, String>();
					String key[] = mappingName.split(":");
					temp.put("getValue", "$" + key[key.length - 2] + ":" + key[key.length - 1] + "$");
					mappingName = precondtionKeywords(temp).get("getValue");
					mappingName = key[0] + ":" + key[1] + ":" + mappingName;
				}
				mappingName = mappingName.replace("$", "");
				Map<String, String> tempmap = new HashMap<String, String>();
				tempmap.put("getuin", findUinKeyword);
				returnMap.put(entry.getKey(),
						IdRepoUtil.retrieveDataFromIdRepo(mappingName, precondtionKeywords(tempmap).get("getuin")));
			} else if (entry.getValue().contains("+") && entry.getValue().contains("$")
					&& !entry.getValue().contains("YYYYMMddHHmmss") && !entry.getValue().contains("TIMESTAMP")
					&& !entry.getValue().contains("TIMESTAMPZ")) {
				String[] keys = entry.getValue().split(Pattern.quote("+"));
				String value = "";
				for (int i = 0; i < keys.length; i++) {
					Map<String, String> tempmap = new HashMap<String, String>();
					tempmap.put("key", keys[i]);
					value = value + precondtionKeywords(tempmap).get("key");
				}
				returnMap.put(entry.getKey(), value);
			} else if (entry.getValue().contains("$") && entry.getValue().startsWith("$idrepo")
					&& entry.getValue().contains("DECODEFILE:")) {
				String[] keys = entry.getValue().split("~");
				String findUinKeyword = keys[1];
				Map<String, String> tempmap = new HashMap<String, String>();
				tempmap.put("getuin", findUinKeyword);
				String encodedValueMappingName = keys[2].replace("DECODEFILE:", "");
				String biometricValue = IdRepoUtil.retrieveDataFromIdRepo(encodedValueMappingName,
						precondtionKeywords(tempmap).get("getuin"));
				String expression = keys[3].replace("$", "");
				String xml = EncryptDecrptUtil.getDecodeFile(biometricValue);
				String value = XmlPrecondtion.getValueFromXmlContent(xml, expression);
				returnMap.put(entry.getKey(), value);
			} else if (entry.getValue().contains("$") && entry.getValue().contains(":")
					&& (entry.getValue().contains("$RANDOM"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split(":");
				String type = keys[1];
				String digit = keys[2];
				if (type.equals("N"))
					returnMap.put(entry.getKey(), randomize(Integer.parseInt(digit)));
				if (type.equals("AN"))
					returnMap.put(entry.getKey(), randomize(Integer.parseInt(digit)));
			} else if (entry.getValue().contains("$YYYYMMddHHmmss$")) {
				AuthTestsUtil.wait(5000);
				String[] tempArray = entry.getValue().split(Pattern.quote("+"));
				String constantValue = tempArray[0];
				DateFormat dateFormatter = new SimpleDateFormat("YYYYMMddHHmmss");
				Calendar cal = Calendar.getInstance();
				String timestampValue = dateFormatter.format(cal.getTime());
				returnMap.put(entry.getKey(), constantValue + timestampValue);
			} else if (entry.getValue().contains("$") && (entry.getValue().startsWith("$TRANSLATE"))) {
				// String keyword = entry.getValue().replace("$", "");
				String[] keys = entry.getValue().split("~");
				String text = keys[1];
				Map<String, String> tempMap = new HashMap<String, String>();
				if (text.contains("$")) {
					tempMap.put("text", text);
					tempMap = precondtionKeywords(tempMap);
				} else
					tempMap.put("text", text);
				String surceLang = keys[2].replace("$", "");
				String destLang = keys[3].replace("$", "");
				String str = AuthTestsUtil.languageConverter(tempMap.get("text"), surceLang, destLang);
				returnMap.put(entry.getKey(), str);
			}
			// Keyword to get UIN Number
			else if (entry.getValue().contains("$UIN") && !entry.getValue().contains("UIN-PIN")) {
				returnMap.put(entry.getKey(), RunConfigUtil.getUinNumber(entry.getValue()));
			} else if (entry.getValue().contains("$VID") && !entry.getValue().contains("VID-PIN")) {
				if (entry.getValue().contains("WHERE") && entry.getValue().contains("WITH")
						&& entry.getValue().contains("UIN")) {
					String uinKeyword = entry.getValue().replace("VID:WHERE", "");
					Map<String, String> tempIn = new HashMap<String, String>();
					tempIn.put("uin", uinKeyword);
					Map<String, String> tempOut = precondtionKeywords(tempIn);
					returnMap.put(entry.getKey(), RunConfigUtil.getVidKey(tempOut.get("uin").toString()));
				} else
					returnMap.put(entry.getKey(), getVidNumber());
			} else if (entry.getValue().contains("$") && (entry.getValue().startsWith("$audit")
					|| entry.getValue().startsWith("$input") || entry.getValue().startsWith("$output"))) {
				String keyword = entry.getValue().replace("$", "");
				String value = returnMap.get(keyword);
				if (value.contains("~") || value.contains("$")) {
					flag = true;
					returnMap.put(entry.getKey(), entry.getValue());
				} else
					returnMap.put(entry.getKey(), value);
			} else if (entry.getValue().contains("ENCODEFILE")) {
				String value = entry.getValue().replace("$", "");
				String[] actVal = value.split(":");
				String file = new File(
						"./" + TestDataConfig.getSrcPath() + TestDataConfig.getTestDataPath() + actVal[1])
								.getAbsolutePath();
				returnMap.put(entry.getKey(), EncryptDecrptUtil.getCbeffEncode(new File(file).getAbsolutePath().toString()));
			} else
				returnMap.put(entry.getKey(), entry.getValue());
			currentTestData=returnMap;
		}
		if (flag)
			precondtionKeywords(returnMap);
		return returnMap;
	}
	
	/**
	 * Method return random integer value for number of digit
	 * 
	 * @param digit
	 * @return string
	 */
	private String randomize(int digit){
        Random r = new Random();
        String randomNumber="";
        for (int i = 0; i < digit; i++) {
        	randomNumber=randomNumber+r.nextInt(9);
        }
        return randomNumber;
    }
	/**
	 * The method generate current timestamp
	 * 
	 * @return string
	 */
	private String generateCurrentTimeStampWithTimeZone()
	{
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'+'");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -2);
		return dateFormatter.format(cal.getTime()) + "05:30";
	}
	/**
	 * Method generate timestamp
	 * 
	 * @param calendarType
	 * @param addsub
	 * @param number
	 * @return string
	 */
	private String generateTimeStamp(String calendarType, String addsub, int number) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'+'");
		Calendar cal = Calendar.getInstance();
		if (calendarType.equals("HOUR") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.HOUR, append);
		} else if (calendarType.equals("HOUR") && addsub.equals("+")) {
			cal.add(Calendar.HOUR, number);
		} else if (calendarType.equals("MINUTE") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.MINUTE, append);
		} else if (calendarType.equals("MINUTE") && addsub.equals("+")) {
			cal.add(Calendar.MINUTE, number);
		} else if (calendarType.equals("SECOND") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.SECOND, append);
		} else if (calendarType.equals("SECOND") && addsub.equals("+")) {
			cal.add(Calendar.SECOND, number);
		}
		return dateFormatter.format(cal.getTime()) + "05:30";
	}	
	/**
	 * The method generate timestamo with Z timezone
	 * 
	 * @return string
	 */
	private String generateTimeStampWithZTimeZone() {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			return dateFormat.format(date);
	}
	/**
	 * The method generate invalid ISO timestamo
	 * 
	 * @return string
	 */
	private String generateInvalidTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}
	/**
	 * The method get static pin for UIN number
	 * 
	 * @return static pin
	 */
	private String getStaticPinUinNumber() {
		return RunConfigUtil.getRandomStaticPinUINKey();
	}
	/**
	 * The method get static pin for VID number
	 * 
	 * @return static pin
	 */
	private String getStaticPinVidNumber() {
		return RunConfigUtil.getRandomStaticPinVIDKey();
	}
	/**
	 * The method generate random VID number
	 * 
	 * @return VID
	 */
	private String getVidNumber() {
		return RunConfigUtil.getRandomVidKey();
	}
	/**
	 * The method get modified OTP template
	 * 
	 * @param template
	 * @param uin
	 * @param fullName
	 * @return string
	 */
	private String getModifiedOtpEmailTemplate(String template, String uin, String fullName) {
		String emailNotiConfigFile = new File(
				"./" + TestDataConfig.getSrcPath() + "ida/TestData/RunConfig/emailNotification.properties")
						.getAbsolutePath();
		String messageText = null;
		if (template.equals("otp.generate.email.fra.message.body")) {
			messageText = AuthTestsUtil.getPropertyFromFilePath(emailNotiConfigFile).get(template).toString();
			if (uin.length() == 10) {
				messageText = messageText.replace("$maskedUIN/VID$", "XXXXXXXX" + uin.substring(8, uin.length()));
				messageText = messageText.replace("$uin/vid$", "UIN");
			} else if (uin.length() == 16) {
				messageText = messageText.replace("$maskedUIN/VID$",
						"XXXXXXXXXXXXXX" + uin.substring(14, uin.length()));
				messageText = messageText.replace("$uin/vid$", "VID");
			}
		}
		if (template.equals("otp.generate.email.fra.message.address")) {
			messageText = AuthTestsUtil.getPropertyFromFilePath(emailNotiConfigFile).get(template).toString();
			messageText = messageText.replace("$fullname$", fullName);
		}
		return messageText;
	}
	
	private TreeMap<String, String> getSortedTreeMap(Map<String, String> map) {
		SortedSet<String> sortedKey = new TreeSet<>();
		map.forEach((key, value) -> {
			sortedKey.add(key);
		});
		TreeMap<String, String> sortedMap = new TreeMap<String, String>();
		sortedKey.forEach(sortKey -> {
			map.forEach((key, value) -> {
				if (key.equals(sortKey))
					sortedMap.put(key, value);
			});
		});
		return sortedMap;
	}
}
