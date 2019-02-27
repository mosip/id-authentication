package io.mosip.authentication.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.authentication.fw.dbUtil.DbConnection;
import io.mosip.authentication.fw.idrepo.UinDto;
import io.mosip.testdata.keywords.IdaKeywordUtil;

public class AuditValidUtil {
	
	private static Logger logger = Logger.getLogger(AuditValidUtil.class);
	private DbConnection objDbConnection = new DbConnection();
	private OutputValidationUtil objOutputValidationUtil = new OutputValidationUtil();
	public Map<String, List<OutputValidationDto>> verifyAuditTxn(File[] listOfFiles,String keywordToFind)
	{
		Map<String,String> exp=getPropertyValue(getFile(listOfFiles,keywordToFind).getAbsolutePath());
		Map<String,String> act=objDbConnection.getDataForQuery("select request_dtimes,response_dtimes,id,request_trn_id,auth_type_code,status_code,status_comment,lang_code,ref_id_type,ref_id,cr_dtimes from ida.auth_transaction where request_trn_id = '"+exp.get("request_trn_id")+"' order by cr_dtimes desc limit 1", "IDA");
		//Map<String,String> act=objDbConnection.getDataForQuery("select request_dtimes,response_dtimes,id,request_trn_id,auth_type_code,status_code,status_comment,lang_code,ref_id_type,ref_id,cr_dtimes from ida.auth_transaction where request_trn_id = '5820433111' order by cr_dtimes desc limit 1", "IDA");
		return objOutputValidationUtil.compareActuExpValue(act, exp, "Audit Transaction Validation");
	}
	public Map<String, List<OutputValidationDto>> verifyAuditLog(File[] listOfFiles,String keywordToFind)
	{
		Map<String,String> exp=getPropertyValue(getFile(listOfFiles,keywordToFind).getAbsolutePath());
		Map<String,String> act=objDbConnection.getDataForQuery("select * from audit.app_audit_log where app_name ='"+exp.get("app_name")+"' and module_name='"+exp.get("module_name")+"' order by log_dtimes desc limit 1", "AUDIT");
		//Map<String,String> act=objDbConnection.getDataForQuery("select request_dtimes,response_dtimes,id,request_trn_id,auth_type_code,status_code,status_comment,lang_code,ref_id_type,ref_id,cr_dtimes from ida.auth_transaction where request_trn_id = '5820433111' order by cr_dtimes desc limit 1", "IDA");
		return objOutputValidationUtil.compareActuExpValue(act, exp, "Audit Log Validation");
	}
	private File getFile(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return listOfFiles[j];
			}
		}
		return null;
	}

	protected Map<String, String> getPropertyValue(String path) {
		Properties prop = getAuditTxnDataFromPorpertyFile(path);
		Map<String, String> map = new HashMap<String, String>();
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			map.put(key, value);
		}
		return map;
	}

	private Properties getAuditTxnDataFromPorpertyFile(String path) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(path);
			prop.load(input);
			return prop;
		} catch (Exception e) {
			logger.error("Exception occured in fetching the uin number from property file " + e.getMessage());
			return prop;
		}
	}

}
