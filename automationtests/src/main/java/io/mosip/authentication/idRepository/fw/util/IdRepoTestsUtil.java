package io.mosip.authentication.idRepository.fw.util;

import java.io.File; 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.authentication.fw.util.RunConfigUtil;
import io.restassured.response.Response;

public class IdRepoTestsUtil extends AuthTestsUtil{
	
	private static final Logger IDAREPOSCRIPT_LOGGER = Logger.getLogger(AuthTestsUtil.class);
	private static String identity;
	/**
	 * The method returns run config path
	 */
	public static String getIdRepoRunConfigFile() {
		return RunConfigUtil.getResourcePath()+"idRepository/TestData/RunConfig/runConfiguration.properties";
	}
	
	/**
	 * The method return test data path from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataPath(String className, int index) {
		return getPropertyAsMap(new File(getIdRepoRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataPath[" + index + "]");
	}
	/**
	 * The method will return test data file name from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataFileName(String className, int index) {
		return getPropertyAsMap(new File(getIdRepoRunConfigFile()).getAbsolutePath().toString())
				.get(className + ".testDataFileName[" + index + "]");
	}
	/**
	 * The method get env config details
	 * 
	 * @return properties
	 */
	private static Properties getRunConfigData() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			RunConfigUtil.objRunConfig.setUserDirectory();
			input = new FileInputStream(new File(RunConfigUtil.getResourcePath()+"idRepository/TestData/RunConfig/envRunConfig.properties").getAbsolutePath());
			prop.load(input);
			return prop;
		} catch (Exception e) {
			IDAREPOSCRIPT_LOGGER.error("Exception: " + e.getMessage());
			return prop;
		}
	}
	/**
	 * The method get property value for the key
	 * 
	 * @param key
	 * @return string
	 */
	public static String getPropertyValue(String key) {
		return getRunConfigData().getProperty(key);
	}
	protected static String getCookieRequestFilePath() {
		return RunConfigUtil.getResourcePath()
				+ "idRepository/TestData/Security/GetCookie/getCookieRequest.json".toString();
	}
	public static String getCookieRequestFilePathForUinGenerator() {
		return RunConfigUtil.getResourcePath()
				+ "idRepository/TestData/Security/GetCookie/getCookieForUinGenerator.json".toString();
	}
	
	public static void copyIdrepoTestResource() {
		try {
			File source = new File(RunConfigUtil.getGlobalResourcePath() + "/idRepository");
			File destination = new File(RunConfigUtil.getGlobalResourcePath() + "/"+RunConfigUtil.resourceFolderName);
			FileUtils.copyDirectoryToDirectory(source, destination);
			IDAREPOSCRIPT_LOGGER.info("Copied the idrepository test resource successfully");
		} catch (Exception e) {
			IDAREPOSCRIPT_LOGGER.error("Exception occured while copying the file: "+e.getMessage());
		}
	}
	
	public static void saveIdentityForUpdateIdentityVerification(String id) {
		identity=id;
	}
	
	public static String getIdentityForUpdateIdentityVerification() {
		return identity;
	}
	
	/**
	 * The method will get request and generate output file with return repose
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return String , response for post request
	 */
	protected String getResponseForRequestUrl(String urlPath,String cookieName,String cookieValue) {
		try {
					Response responseJson = RestClient.getRequestWithCookie(urlPath, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,cookieName, cookieValue);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson.asString()) + "</pre>");
					return responseJson.asString();
		} catch (Exception e) {
			IDAREPOSCRIPT_LOGGER.error("Exception " + e);
			return e.getMessage();
		}
	}
	/**
	 * The method will get request and generate output file with return repose
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return String , response for post request
	 */
	protected String getRequestAndGenerateOuputFileWithResponse(String parentFile,String urlPath,
			String generateOutputFileKeyword,String cookieName,String cookieValue) {
		try {
					FileOutputStream fos = new FileOutputStream(
							parentFile + "/" + generateOutputFileKeyword + ".json");
					String responseJson = RestClient.getRequestWithCookie(urlPath, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, 
							cookieName,cookieValue).asString();
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
					return responseJson.toString();
		} catch (Exception e) {
			IDAREPOSCRIPT_LOGGER.error("Exception " + e);
			return e.getMessage();
		}
	}
	/**
	 * The method will post request and generate output file for UIN update
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean postRequestAndGenerateOuputFileForUINUpdate(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, String cookieName,String cookieValue,int code) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String responseJson = "";
					if (code == 0)
						responseJson = patchRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath,cookieName,cookieValue);
					/*else
						responseJson = patchRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, code);*/
					Reporter.log("<b><u>Actual Patch Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			IDAREPOSCRIPT_LOGGER.error("Exception " + e);
			return false;
		}
	}
}
