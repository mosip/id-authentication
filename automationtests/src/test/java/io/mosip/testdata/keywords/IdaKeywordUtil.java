package io.mosip.testdata.keywords;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;   
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import io.mosip.authentication.fw.idrepo.*;
import io.mosip.authentication.fw.util.EncrypDecrptUtils;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.util.JsonPrecondtion;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.testdata.TestDataUtil;

/**
 * The class is to implementation of keyword as per ida test execution
 * 
 * @author Vignesh
 *
 */
public class IdaKeywordUtil extends KeywordUtil{

	private IdRepoUtil objIdRepoUtil = new IdRepoUtil();	
	private IdaScriptsUtil objIdaScriptsUtil = new IdaScriptsUtil();
	private static JsonPrecondtion objJsonPrecondtion = new JsonPrecondtion();
	private EncrypDecrptUtils objEncrypDecrptUtils = new EncrypDecrptUtils();
	private static Logger logger = Logger.getLogger(IdaKeywordUtil.class);
	@Override
	public Map<String, String> precondtionKeywords(Map<String, String> map) {
		Map<String, String> returnMap = new HashMap<String, String>();
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().equals("$TIMESTAMP$")) {
				if (!entry.getKey().startsWith("output."))
					returnMap.put(entry.getKey(), generateCurrentTimeStamp());
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
			}else if (entry.getValue().contains("$TIMESTAMPZ$")) {
				returnMap.put(entry.getKey(),generateTimeStamp());				
		}else if (entry.getValue().contains("$INVALIDTIMESTAMPZ$")) {
			returnMap.put(entry.getKey(),generateInvalidTimeStamp());
		}else if (entry.getValue().contains("$") && entry.getValue().contains(":") && (entry.getValue().startsWith("$input") || entry.getValue().startsWith("$output"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split(":");
				String jsonFileName = keys[0];
				String fieldName = keys[1];
				String val = TestDataUtil.getCurrTestDataDic().get(jsonFileName).get(fieldName);
				returnMap.put(entry.getKey(), val);
			}
			else if(entry.getValue().contains("$") && entry.getValue().startsWith("$idrepo") && !(entry.getValue().contains("DECODE:"))) 
			{
				String[] keys = entry.getValue().split("~");
				String findUinKeyword=keys[1];
				String mappingName=keys[2];
				mappingName=mappingName.replace("$", "");
				Map<String,String> tempmap = new HashMap<String,String>();
				tempmap.put("getuin", findUinKeyword);
				returnMap.put(entry.getKey(),objIdRepoUtil.retrieveDataFromIdRepo(mappingName, precondtionKeywords(tempmap).get("getuin")));
			}
			else if (entry.getValue().contains("$") && entry.getValue().startsWith("$idrepo")
					&& entry.getValue().contains("DECODE:")) {
				String[] keys = entry.getValue().split("~");
				String findUinKeyword = keys[1];
				Map<String, String> tempmap = new HashMap<String, String>();
				tempmap.put("getuin", findUinKeyword);
				String encodedValueMappingName = keys[2].replace("DECODE:", "");
				String biometricValue = objIdRepoUtil.retrieveDataFromIdRepo(encodedValueMappingName,
						precondtionKeywords(tempmap).get("getuin"));
				String mappingName = keys[3];
				mappingName = mappingName.replace("$", "");
				String jsonContent = objEncrypDecrptUtils.getDecodeFromStr(biometricValue);
				returnMap
						.put(entry.getKey(),
								objJsonPrecondtion.getValueFromJsonUsingMapping(jsonContent,
										RunConfig.getUserDirectory() + RunConfig.getSrcPath()
												+ RunConfig.getStoreUINDataPath() + "\\mapping.properties",
										mappingName));
			}
	        else if (entry.getValue().contains("$") && entry.getValue().contains(":") && (entry.getValue().contains("$RANDOM"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split(":");
				String type=keys[1];
				String digit=keys[2];
				if(type.equals("N"))
					returnMap.put(entry.getKey(),randomize(Integer.parseInt(digit)));
				if(type.equals("AN"))
					returnMap.put(entry.getKey(),randomize(Integer.parseInt(digit)));
			}
	        else if (entry.getValue().contains("%") && entry.getValue().contains(":") && entry.getValue().startsWith("%$"))
	        	{
	        		Map<String,String> tempMap = new HashMap<String,String>();
	        		String temp=entry.getValue().replaceAll("%", "");
	        		String[] getValue=temp.split("_");
	        		tempMap.put("txnID", getValue[0]);
	        		tempMap.put("tspId", getValue[1]);
	        		Map<String,String> tempOut=precondtionKeywords(tempMap);
	        		String baseQuery="select otp from kernel.otp_transaction where id like ";
	        		String otpId = "%"+tempOut.get("txnID")+"_"+tempOut.get("tspId")+"%";
	        		String OtpFindQuery=baseQuery+"'"+otpId+"'"+":"+getValue[2];
	        		returnMap.put(entry.getKey(),OtpFindQuery);
	        	}
			else if (entry.getValue().contains("$YYYYMMddHHmmss$")) {
				objIdaScriptsUtil.wait(5000);
				String[] tempArray=entry.getValue().split(Pattern.quote("+"));
				String constantValue=tempArray[0];
				DateFormat dateFormatter = new SimpleDateFormat("YYYYMMddHHmmss");
				Calendar cal = Calendar.getInstance();
				String timestampValue= dateFormatter.format(cal.getTime());
				returnMap.put(entry.getKey(), constantValue+timestampValue);
			}
			else if (entry.getValue().contains("$") && (entry.getValue().startsWith("$TRANSLATE"))) {
				String keyword = entry.getValue().replace("$", "");
				String[] keys = keyword.split("~");
				String type=keys[0];
				String text=keys[1];
				String surceLang=keys[2];
				String destLang=keys[3];
				String str=objIdaScriptsUtil.languageConverter(text,surceLang, destLang);
				returnMap.put(entry.getKey(), str);
			}
			//Keyword to get UIN Number
			else if (entry.getValue().contains("$UIN")) {
				returnMap.put(entry.getKey(), getUinNumber(entry.getValue()));
			}
			else
				returnMap.put(entry.getKey(), entry.getValue());
		}
		return returnMap;
		
	}
	private String randomize(int digit){
        Random r = new Random();
        String randomNumber="";
        for (int i = 0; i < digit; i++) {
        	randomNumber=randomNumber+r.nextInt(9);
        }
        return randomNumber;
    }
	
	private String modifyMultiTimestamp(String inputStr) {
		String temp = inputStr.replace("$TIMESTAMP$", "");
		if (temp.contains("+")) {
			String[] time = temp.split(Pattern.quote("+"));
			String calType = time[0];
			int number = Integer.parseInt(time[1]);
			return generateTimeStamp(calType, "+", number);
		} else if (temp.contains("-")) {
			String[] time = temp.split("-");
			String calType = time[0];
			int number = Integer.parseInt(time[1]);
			return generateTimeStamp(calType, "-", number);
		}
		else
			return inputStr;
	}
	private String generateCurrentTimeStamp()
	{
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'+'");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -2);
		return dateFormatter.format(cal.getTime()) + "05:30";
	}
	
	private String generateTimeStamp(String calendarType, String addsub, int number) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'+'");
		Calendar cal = Calendar.getInstance();
		if (calendarType.equals("HOUR") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.HOUR, append);
		} else if (calendarType.equals("HOUR") && addsub.equals("+")) {
			// int append = Integer.parseInt(addsub+number);
			cal.add(Calendar.HOUR, number);
		} else if (calendarType.equals("MINUTE") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.MINUTE, append);
		} else if (calendarType.equals("MINUTE") && addsub.equals("+")) {
			// int append = Integer.parseInt(addsub+number);
			cal.add(Calendar.MINUTE, number);
		} else if (calendarType.equals("SECOND") && addsub.equals("-")) {
			int append = Integer.parseInt(addsub + number);
			cal.add(Calendar.SECOND, append);
		} else if (calendarType.equals("SECOND") && addsub.equals("+")) {
			// int append = Integer.parseInt(addsub+number);
			cal.add(Calendar.SECOND, number);
		}
		return dateFormatter.format(cal.getTime()) + "05:30";
	}	
	
	private String generateTimeStamp() {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			return dateFormat.format(date);
	}
	
	private String generateInvalidTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
}
	
	private String getUinNumber(String keyword) {
		if (keyword.contains("EVEN")) {
			int count = 1;
			while (count > 0) {
				String key = getRandomUINKey();
				String lastNumberAsString = key.substring(key.length() - 1, key.length());
				int lastNum = Integer.parseInt(lastNumberAsString);
				if (lastNum % 2 == 0)
					return key;
				else
					count++;
			}
		}
		else if (keyword.contains("ODD")) {
			int count = 1;
			while (count > 0) {
				String key = getRandomUINKey();
				String lastNumberAsString = key.substring(key.length() - 1, key.length());
				int lastNum = Integer.parseInt(lastNumberAsString);
				if (lastNum % 2 != 0)
					return key;
				else
					count++;
			}
		}
		else if (keyword.equals("$UIN$")) {
			String key = getRandomUINKey();
			return key;
		}
		else
		{
			keyword=keyword.replace("$", "");
			String keys[] = keyword.split(":");
			String keywrdToFind=keys[2];
			return getUINKey(keywrdToFind);
		}
		return "NoLoadedUINFound";
	}
	
	private String getRandomUINKey() {
		getPropertyValue();
		int count = 1;
		while (count > 0) {
			Object[] randomKeys = UinDto.getUinData().keySet().toArray();
			Object key = randomKeys[new Random().nextInt(randomKeys.length)];
			if (UinDto.getUinData().get(key).toString().contains("valid")) {
				return key.toString();
			}
			count++;
		}
		return "NoUINFound";
	}
	
	private String getUINKey(String keywordToFind) {
		getPropertyValue();
		for(Entry<String,String> entry: UinDto.getUinData().entrySet())
		{
			if(entry.getValue().contains(keywordToFind))
				return entry.getKey();
		}
		return "NoLoadedUINFound";
	}
	
	protected void getPropertyValue() {
		Properties prop = getUINData();
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		UinDto.setUinData(map);
	}
	
	private Properties getUINData() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(
					RunConfig.getUserDirectory() + RunConfig.getSrcPath() + "ida\\Testdata\\RunConfig\\uin.properties");
			prop.load(input);
			return prop;
		} catch (Exception e) {
			logger.error("Exception occured in fetching the uin number from property file " + e.getMessage());
			return prop;
		}
	}

}
