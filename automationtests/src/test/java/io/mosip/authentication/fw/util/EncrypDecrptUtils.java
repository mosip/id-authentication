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

import io.mosip.authentication.fw.client.RestClient;

/**
 * Perform encryption and decryption util. 
 * 
 * Dependency: Run demo app application in locally
 * 
 * @author Vignesh
 *
 */
public class EncrypDecrptUtils extends IdaScriptsUtil{
	public static RestClient objRestClient = new RestClient();
	private static Logger logger = Logger.getLogger(EncrypDecrptUtils.class);
	private ReportUtil objReportUtil =  new ReportUtil();
	private String key="encryptedSessionKey";
	private String value="encryptedIdentity";
	
	public Map<String,String> getEncryptSessionKeyValue(String filename) {
		Map<String, String> ecryptData = new HashMap<String, String>();
		try {
			String json = getEncryption(filename);
			JSONParser parser = new JSONParser();
			JSONObject jsonobj = (JSONObject) parser.parse(json);
			Reporter.log("<b> <u>Encryption of identity request</u> </b>");
			Reporter.log("<pre>" + objReportUtil.getTextAreaJsonMsgHtml(json)+"</pre>");
			ecryptData.put(jsonobj.get(key).toString(), jsonobj.get(value).toString());
			return ecryptData;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}
	
	private String getEncryption(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			Reporter.log("<b><u> Identity request:</u></b>");
			Reporter.log("<pre>" + objReportUtil.getTextAreaJsonMsgHtml(objectData.toString())+"</pre>");
			return objRestClient.postRequest(RunConfig.getEncryptUtilBaseUrl()+RunConfig.getEncryptionPath(), objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}	

	public String getEncode(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return objRestClient.postRequest(RunConfig.getEncryptUtilBaseUrl()+RunConfig.getEncodePath(), objectData.toJSONString(), MediaType.TEXT_PLAIN,
					MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	
	public String getDecodeFile(String content) {
		try {
			return objRestClient.postRequest(RunConfig.getEncryptUtilBaseUrl() + RunConfig.getDecodeFilePath(), content,
					MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	
	public String getEncodeFile(File file) {
		try {
			return objRestClient.postRequest(RunConfig.getEncryptUtilBaseUrl() + RunConfig.getEncodeFilePath(), file,
					MediaType.MULTIPART_FORM_DATA, MediaType.TEXT_PLAIN).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	
	public String getDecodeFromFile(String filename) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return objRestClient.postRequest(RunConfig.getEncryptUtilBaseUrl()+RunConfig.getDecodePath(), objectData.toJSONString(), MediaType.TEXT_PLAIN,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	
	public String getDecodeFromStr(String content) {
		try {
			return objRestClient.postRequest(RunConfig.getEncryptUtilBaseUrl()+RunConfig.getDecodePath(), content, MediaType.TEXT_PLAIN,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
}

