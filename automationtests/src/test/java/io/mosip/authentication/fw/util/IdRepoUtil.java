package io.mosip.authentication.fw.util;

import java.io.File;
import java.io.FileOutputStream;  
import java.io.OutputStream;
import java.nio.file.Files;  
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.fw.dto.VidDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;

/**
 * Class to store all the UIN data or json using idrepo
 * 
 * @author Vignesh
 *
 */
public class IdRepoUtil extends IdaScriptsUtil{

	private static FileUtil objFileUtil = new FileUtil();
	private static JsonPrecondtion objJsonPrecondtion = new JsonPrecondtion();
	private static UinVidNumberUtil objUinVidNumberUtil = new UinVidNumberUtil();
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
			if (uinNumber.length() == 16) {
				objUinVidNumberUtil.getVidPropertyValue(objUinVidNumberUtil.getVidPropertyPath());
				uinNumber = VidDto.getVid().get(uinNumber);
			}
			if (retrieveIdRepo(uinNumber)) {
				String value = objJsonPrecondtion.getValueFromJson(
						Paths.get(new File("./"+RunConfig.getSrcPath()
								+ RunConfig.getStoreUINDataPath() + "/" + uinNumber + ".json").getAbsolutePath()).toString(),
						new File("./"+RunConfig.getSrcPath() + RunConfig.getStoreUINDataPath()
								+ "/mapping.properties").getAbsolutePath(),
						mapping);
				if(mapping.contains("dateOfBirth") && mapping.contains("input"))
				{
					Date valuedate= new SimpleDateFormat("yyyy/MM/dd").parse(value);
					SimpleDateFormat date= new SimpleDateFormat("dd/MM/yyyy");
					value = date.format(valuedate);
				}
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

}
