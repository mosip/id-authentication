	package io.mosip.authentication.testdata.keywords;

import java.io.File;  
import java.io.FileInputStream; 
import java.io.InputStream;
import java.text.DateFormat;   
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.fw.dto.UinStaticPinDto;
import io.mosip.authentication.fw.dto.VidDto;
import io.mosip.authentication.fw.dto.VidStaticPinDto;
import io.mosip.authentication.fw.util.EncrypDecrptUtils;
import io.mosip.authentication.fw.util.IdRepoUtil;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.fw.precon.XmlPrecondtion;
import io.mosip.authentication.fw.util.RunConfig;
import io.mosip.authentication.fw.util.UinVidNumberUtil;
import io.mosip.authentication.testdata.TestDataConfig;
import io.mosip.authentication.testdata.TestDataUtil;

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
	private static XmlPrecondtion objXmlPrecondtion = new XmlPrecondtion();
	private EncrypDecrptUtils objEncrypDecrptUtils = new EncrypDecrptUtils();
	private UinVidNumberUtil objUinVidNumberUtil= new UinVidNumberUtil();
	//private String UIN_PATH="ida/"+RunConfig.getTestDataFolderName()+"/RunConfig/uin.properties";
	//private String STATIC_PIN_UIN_PATH="ida/"+RunConfig.getTestDataFolderName()+"/RunConfig/uinStaticPin.properties";
	//public String VID_PATH="ida/"+RunConfig.getTestDataFolderName()+"/RunConfig/vid.properties";
	//private String STATIC_PIN_VID_PATH="ida/"+RunConfig.getTestDataFolderName()+"/RunConfig/vidStaticPin.properties";

	private static Logger logger = Logger.getLogger(IdaKeywordUtil.class);
	@Override
	public Map<String, String> precondtionKeywords(Map<String, String> map) {
		Map<String, String> returnMap = map;
		boolean flag=false;
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().contains("TOKENID") && entry.getValue().startsWith("$TOKENID")) {
				String[] keys = entry.getValue().split(Pattern.quote("~"));
				Map<String, String> tempmap = new HashMap<String, String>();
				tempmap.put("uin", keys[1]);
				tempmap.put("tspId", keys[2]);
				Map<String, String> dic = precondtionKeywords(tempmap);
				returnMap.put(entry.getKey(), objUinVidNumberUtil.getTokenId(dic.get("uin"), dic.get("tspId")));
			}
			else if (entry.getValue().equals("$TIMESTAMP$")) {
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
			} else if (entry.getValue().contains("$TIMESTAMPZ$")) {
				if (!entry.getKey().startsWith("output."))
					returnMap.put(entry.getKey(), generateTimeStamp());
				else
					returnMap.put(entry.getKey(), entry.getValue());
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
			else if(entry.getValue().contains("$") && entry.getValue().startsWith("$idrepo") && !entry.getValue().contains("+") && !(entry.getValue().contains("DECODE:") || entry.getValue().contains("DECODEFILE:"))) 
			{
				String[] keys = entry.getValue().split("~");
				String findUinKeyword=keys[1];
				String mappingName=keys[2];
				mappingName=mappingName.replace("$", "");
				Map<String,String> tempmap = new HashMap<String,String>();
				tempmap.put("getuin", findUinKeyword);
				returnMap.put(entry.getKey(),objIdRepoUtil.retrieveDataFromIdRepo(mappingName, precondtionKeywords(tempmap).get("getuin")));
			}
			else if (entry.getValue().contains("+") && entry.getValue().contains("$") && !entry.getValue().contains("YYYYMMddHHmmss")
					&& !entry.getValue().contains("TIMESTAMP") && !entry.getValue().contains("TIMESTAMPZ")) {
				String[] keys = entry.getValue().split(Pattern.quote("+"));
				String value = "";
				for (int i = 0; i < keys.length; i++) {					
					/*if(keys[i].equals("ARA"))
					{
					      value="آنسة";
					}*/
					//else
					//{
						Map<String, String> tempmap = new HashMap<String, String>();
						tempmap.put("key", keys[i]);
						value = value + precondtionKeywords(tempmap).get("key");
					//}
				}
				returnMap.put(entry.getKey(), value);
			}
			else if (entry.getValue().contains("$") && entry.getValue().startsWith("$idrepo")
					&& entry.getValue().contains("DECODEFILE:")) {
				String[] keys = entry.getValue().split("~");
				String findUinKeyword = keys[1];
				Map<String, String> tempmap = new HashMap<String, String>();
				tempmap.put("getuin", findUinKeyword);
				String encodedValueMappingName = keys[2].replace("DECODEFILE:", "");
				String biometricValue = objIdRepoUtil.retrieveDataFromIdRepo(encodedValueMappingName,
						precondtionKeywords(tempmap).get("getuin"));
				String expression = keys[3].replace("$", "");
				String xml=objEncrypDecrptUtils.getDecodeFile(biometricValue);
				String value=objXmlPrecondtion.getValueFromXmlContent(xml, expression);
				returnMap.put(entry.getKey(),value);
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
			else if (entry.getValue().contains("$UIN") &&  !entry.getValue().contains("UIN-PIN")) {
				returnMap.put(entry.getKey(), objUinVidNumberUtil.getUinNumber(entry.getValue()));
			}
			else if (entry.getValue().contains("$VID") &&  !entry.getValue().contains("VID-PIN")) {
				returnMap.put(entry.getKey(), getVidNumber());
			}
			else if (entry.getValue().contains("UIN-PIN")) {
				returnMap.put(entry.getKey(), getStaticPinUinNumber());
			}
			else if (entry.getValue().contains("VID-PIN")) {
				returnMap.put(entry.getKey(), getStaticPinVidNumber());
			}
			else if (entry.getValue().contains("$") && (entry.getValue().startsWith("$audit")
					|| entry.getValue().startsWith("$input") || entry.getValue().startsWith("$output"))) {
				String keyword = entry.getValue().replace("$", "");
				String value = returnMap.get(keyword);
				if (value.contains("~") || value.contains("$")) {
					flag = true;
					returnMap.put(entry.getKey(), entry.getValue());
				} else
					returnMap.put(entry.getKey(), value);
			}
			else if (entry.getValue().startsWith("$staticPin")) {
				String[] array = entry.getValue().split(Pattern.quote("~"));
				String uinKeyword = array[1];
				String tempValue = uinKeyword.replace("$", "");
				String value = returnMap.get(tempValue);
				if (value.contains("~") || value.contains("$")) {
					flag = true;
					returnMap.put(entry.getKey(), entry.getValue());
				} else {
					if (value.length() == 16) {
						objUinVidNumberUtil.getStaticPinVidPropertyValue(objUinVidNumberUtil.getStaticPinVidPropertyPath());
						String pin = VidStaticPinDto.getVidStaticPin().get(value).toString();
						returnMap.put(entry.getKey(), pin);
					} else {
						objUinVidNumberUtil.getStaticPinUinPropertyValue(objUinVidNumberUtil.getStaticPinUinPropertyPath());
						String pin = UinStaticPinDto.getUinStaticPin().get(value).toString();
						returnMap.put(entry.getKey(), pin);
					}
				}
			}
			else if(entry.getValue().contains("ENCODEFILE"))
			{
				String value = entry.getValue().replace("$", "");
				String[] actVal=value.split(":");
				String file=TestDataConfig.getUserDirectory()+TestDataConfig.getSrcPath()+TestDataConfig.getTestDataPath()+actVal[1];
				returnMap.put(entry.getKey(),objEncrypDecrptUtils.getEncodeFile(new File(file)));
			}
			else
				returnMap.put(entry.getKey(), entry.getValue());
		}
		if(flag)
			precondtionKeywords(returnMap);
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
	
	private String getStaticPinUinNumber() {
		return objUinVidNumberUtil.getRandomStaticPinUINKey();
	}
	
	private String getStaticPinVidNumber() {
		return objUinVidNumberUtil.getRandomStaticPinVIDKey();
	}
	
	private String getVidNumber() {
		return objUinVidNumberUtil.getRandomVidKey();
	}

}
