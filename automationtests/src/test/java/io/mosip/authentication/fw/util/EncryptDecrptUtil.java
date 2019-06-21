package io.mosip.authentication.fw.util;

import java.io.File;
import java.io.FileReader; 
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Reporter;

/**
 * Perform encryption and decryption activity using local executable demoApp jar. 
 * 
 * Dependency: Run demo app application in locally
 * 
 * @author Vignesh
 *
 */
public class EncryptDecrptUtil extends AuthTestsUtil{
	private static final Logger ENCRYPTION_DECRYPTION_LOGGER = Logger.getLogger(EncryptDecrptUtil.class);
	private static String key="encryptedSessionKey";
	private static String data="encryptedIdentity";
	private static String hmac="requestHMAC";
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return Map - key,data,hmac and its value
	 */ 
	public static Map<String, String> getEncryptSessionKeyValue(String filename) {
		Map<String, String> ecryptData = new HashMap<String, String>();
		try {
			String json = getEncryption(filename);
			JSONParser parser = new JSONParser();
			JSONObject jsonobj = (JSONObject) parser.parse(json);
			Reporter.log("<b> <u>Encryption of identity request</u> </b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(json) + "</pre>");
			ecryptData.put("key", jsonobj.get(key).toString());
			ecryptData.put("data", jsonobj.get(data).toString());
			ecryptData.put("hmac", jsonobj.get(hmac).toString());
			return ecryptData;
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error(e);
			return null;
		}
	}
	
	/**
	 * The method get internal auth encrypted json for identity request
	 * 
	 * @param filename
	 * @return Map - key,data,hmac and its value
	 */ 
	public static Map<String, String> getInternalEncryptSessionKeyValue(String filename) {
		Map<String, String> ecryptData = new HashMap<String, String>();
		try {
			String json = getIntenalEncryption(filename);
			JSONParser parser = new JSONParser();
			JSONObject jsonobj = (JSONObject) parser.parse(json);
			Reporter.log("<b> <u>Encryption of identity request</u> </b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(json) + "</pre>");
			ecryptData.put("key", jsonobj.get(key).toString());
			ecryptData.put("data", jsonobj.get(data).toString());
			ecryptData.put("hmac", jsonobj.get(hmac).toString());
			return ecryptData;
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error(e);
			return null;
		}
	}
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return String , Ecrypted JSON
	 */
	private static String getEncryption(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			Reporter.log("<b><u> Identity request:</u></b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(objectData.toString())+"</pre>");
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl()+RunConfigUtil.objRunConfig.getEncryptionPath(), objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get encrypted json for identity request
	 * 
	 * @param filename
	 * @return String , Ecrypted JSON
	 */
	private static String getIntenalEncryption(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			Reporter.log("<b><u> Identity request:</u></b>");
			Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml(objectData.toString())+"</pre>");
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl()+RunConfigUtil.objRunConfig.getInternalEncryptionPath(), objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	/**
	 * The method will get encoded data from json content in file
	 * 
	 * @param filename
	 * @return String, Encoded data
	 */
	public static String getEncode(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl()+RunConfigUtil.objRunConfig.getEncodePath(), objectData.toJSONString(), MediaType.TEXT_PLAIN,
					MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	/**
	 * The method will get encoded data from cbeff file
	 * 
	 * @param filename
	 * @return String, Encoded data
	 */
	public static String getCbeffEncode(String filename) {
		try {
			String objectData = FileUtil.readInput(filename);
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl()+RunConfigUtil.objRunConfig.getEncodePath(), objectData, MediaType.TEXT_PLAIN,
					MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	/**
	 * The method get decoded content in file
	 * 
	 * @param content, String to decode
	 * @return String, decoded content
	 */
	public static String getDecodeFile(String content) {
		try {
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl() + RunConfigUtil.objRunConfig.getDecodeFilePath(), content,
					MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get encoded data from file
	 * 
	 * @param file, file to be encoded
	 * @return String, encoded data
	 */
	public static String getEncodeFile(File file) {
		try {
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl() + RunConfigUtil.objRunConfig.getEncodeFilePath(), file,
					MediaType.MULTIPART_FORM_DATA, MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get decoded data from file
	 * 
	 * @param filename, file to to be decoded
	 * @return String, decoded data
	 */
	public static String getDecodeFromFile(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl() + RunConfigUtil.objRunConfig.getDecodePath(),
					objectData.toJSONString(), MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get decoded data from string
	 * 
	 * @param content, String to be decoded
	 * @return String, decoded data
	 */
	public static String getDecodeFromStr(String content) {
		try {
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl() + RunConfigUtil.objRunConfig.getDecodePath(), content,
					MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	/**
	 * The method get decrypt data from file
	 * 
	 * @param filename, file to to be decoded
	 * @return String, decoded data
	 */
	public static String getDecryptFromFile(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl() + RunConfigUtil.objRunConfig.getDecryptPath(),
					objectData.toJSONString(), MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
	
	/**
	 * The method get decrypt data from string
	 * 
	 * @param content, String to be decoded
	 * @return String, decoded data
	 */
	public static String getDecyptFromStr(String content) {
		try {
			return RestClient.postRequest(RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl() + RunConfigUtil.objRunConfig.getDecryptPath(), content,
					MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			ENCRYPTION_DECRYPTION_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}
}
