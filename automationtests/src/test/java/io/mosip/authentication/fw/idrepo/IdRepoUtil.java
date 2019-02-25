package io.mosip.authentication.fw.idrepo;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;  
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import io.mosip.authentication.fw.util.JsonPrecondtion;
import io.mosip.authentication.fw.util.RunConfig;

/**
 * Class to store all the UIN data or json using idrepo
 * 
 * @author Vignesh
 *
 */
public class IdRepoUtil extends IdaScriptsUtil{

	private static FileUtil objFileUtil = new FileUtil();
	private static JsonPrecondtion objJsonPrecondtion = new JsonPrecondtion();
	private static Logger logger = Logger.getLogger(IdRepoUtil.class);

	/**
	 * Get the uin data or json using idrepo api and save it in output file
	 * 
	 * @param uinNumber
	 * @return true or false
	 */
	public boolean retrieveIdRepo(String uinNumber) {
		String retrievePath = RunConfig.getIdRepoRetrieveDataPath().replace("$uin$", uinNumber);
		String url = RunConfig.getIdRepoEndPointUrl() + retrievePath;
		if (!objFileUtil.checkFileExistForIdRepo(uinNumber + ".json")) {
			if (objFileUtil.createAndWriteFileForIdRepo(uinNumber + ".json", getResponse(url, "type=all")))
				return true;
			else
				return false;
		}
		return true;
	}
	
	/**
	 * Get field data for the key from saved uin data or json 
	 * 
	 * @param mapping
	 * @param uinNumber
	 * @return uin data or json
	 */
	public String retrieveDataFromIdRepo(String mapping, String uinNumber) {
		try {
			if (retrieveIdRepo(uinNumber)) {
				String value = objJsonPrecondtion.getValueFromJson(
						Paths.get(RunConfig.getUserDirectory() + RunConfig.getSrcPath()
								+ RunConfig.getStoreUINDataPath() + "\\" + uinNumber + ".json").toString(),
						RunConfig.getUserDirectory() + RunConfig.getSrcPath() + RunConfig.getStoreUINDataPath()
								+ "\\mapping.properties",
						mapping);
				if (value.contains("Null"))
					return "$REMOVE$";
				else
					return value.toString();
			} else
				return "No Record found for the UIN: " + uinNumber;
		} catch (Exception e) {
			logger.error("Exceptione in fetching the data from id repo" + e);
			if (e.toString().contains("Null")) {
				return "$REMOVE$";
			}
			return "Exceptione in fetching the data from id repo: " + e.toString();
		}
	}
	
	/**
	 * Generate uin number using generate_uin api
	 * 
	 * @return UIN Number
	 */
	public String generateUinNumber() {
		return objJsonPrecondtion
				.getValueFromJson(getResponse(RunConfig.getEndPointUrl() + RunConfig.getGenerateUINPath()), "uin");
	}
	
	/**
	 * Get Create UIN api Path for the generated uin number
	 * 
	 * @param UinNumber
	 * @return create uin path
	 */
	public String getCreateUinPath(String UinNumber) {
		String url = RunConfig.getIdRepoEndPointUrl() + RunConfig.getIdRepoCreateUINRecordPath();
		url = url.replace("$uin$", UinNumber);
		return url;
	}
	
	/**
	 * Create generatd uin number and its test case name in property file
	 * 
	 * @param filePath
	 */
	public void generateUinMappingDic(String filePath) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(filePath);
			for (Entry<String, String> entry : UinDto.getUinData().entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, null);
		} catch (Exception e) {
			logger.error("Excpetion in storing the UIN data in propertyFile" + e.getMessage());
		}
	}

}
