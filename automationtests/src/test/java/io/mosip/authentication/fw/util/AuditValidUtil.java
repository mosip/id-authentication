package io.mosip.authentication.fw.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.authentication.fw.dbUtil.DbConnection;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.dto.UinDto;
import io.mosip.authentication.testdata.Precondtion;
import io.mosip.authentication.testdata.keywords.IdaKeywordUtil;

public class AuditValidUtil {
	
	private static Logger logger = Logger.getLogger(AuditValidUtil.class);
	private DbConnection objDbConnection = new DbConnection();
	private OutputValidationUtil objOutputValidationUtil = new OutputValidationUtil();
	private IdaScriptsUtil objIdaScriptsUtil = new IdaScriptsUtil();
	private FileUtil objFileUtil = new FileUtil();
	private static File auth_txn_file;
	private static File audit_log_file;

	public Map<String, List<OutputValidationDto>> verifyAuditTxn(File[] listOfFiles, String keywordToFind) {
		auth_txn_file = getFile(listOfFiles, keywordToFind);
		Map<String, String> exp = getPropertyValue(auth_txn_file.getAbsolutePath());
		Map<String, String> act = objDbConnection.getDataForQuery(
				"select request_dtimes,response_dtimes,id,request_trn_id,auth_type_code,status_code,status_comment,lang_code,ref_id_type,ref_id,cr_dtimes from ida.auth_transaction where request_trn_id = '"
						+ exp.get("request_trn_id") + "' order by cr_dtimes desc limit 1",
				"IDA");
		objIdaScriptsUtil.generateMappingDic(auth_txn_file.getAbsolutePath().toString(),preconAuditKeywords(exp,act));
		return objOutputValidationUtil.compareActuExpValue(act, exp, "Audit Transaction Validation");
	}

	public Map<String, List<OutputValidationDto>> verifyAuditLog(File[] listOfFiles, String keywordToFind) {
		audit_log_file = getFile(listOfFiles, keywordToFind);
		Map<String, String> exp = getPropertyValue(audit_log_file.getAbsolutePath());
		Map<String, String> act = null;
		if (objFileUtil.verifyFilePresent(listOfFiles, "auth_transaction")) {
			Map<String, String> exp_auth_txn = getPropertyValue(auth_txn_file.getAbsolutePath());
			if (exp_auth_txn.containsKey("request_dtimes") && exp_auth_txn.containsKey("response_dtimes")) {
				act = objDbConnection
						.getDataForQuery(
								getAuditLogQuery(exp.get("app_name"), exp.get("module_name"), exp.get("ref_id"),
										exp_auth_txn.get("request_dtimes"), exp_auth_txn.get("response_dtimes")),
								"AUDIT");
			} else {
				act = objDbConnection.getDataForQuery(
						getAuditLogQuery(exp.get("app_name"), exp.get("module_name"), exp.get("ref_id")), "AUDIT");
			}
		} else {
			act = objDbConnection.getDataForQuery(
					getAuditLogQuery(exp.get("app_name"), exp.get("module_name"), exp.get("ref_id")), "AUDIT");
		}
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
	
	private Map<String, String> preconAuditKeywords(Map<String, String> exp, Map<String, String> act) {
		for (Entry<String, String> temp : exp.entrySet()) {
			if (temp.getValue().contains("$FETCH$")) {
				exp.put(temp.getKey(), act.get(temp.getKey()));
			} else if (temp.getValue().contains("$") && temp.getValue().contains(":")
					&& temp.getValue().contains("audit.")) {
				String keyword = temp.getValue().replace("$", "");
				String arr[] = keyword.split(Pattern.quote(":"));
				String filename = arr[0].replace("audit.", "");
				String value = arr[1];
				String result = objIdaScriptsUtil.getValueFromPropertyFile(auth_txn_file.getAbsolutePath().toString(),
						value);
				exp.put(temp.getKey(), result);
			}
		}
		// objIdaScriptsUtil.generateMappingDic(auth_txn_file.getAbsolutePath(),exp);
		return exp;
	}	
	private String getAuditLogQuery(String app_name, String module_name, String ref_id, String action_dtimes_minTime,
			String action_dtimes_maxTime) {
		return "select * from audit.app_audit_log where app_name ='" + app_name + "' and module_name='" + module_name
				+ "' and ref_id ='" + ref_id + "' and action_dtimes >= timestamp '" + action_dtimes_minTime
				+ "'- interval '10 second'	and action_dtimes <= timestamp '" + action_dtimes_maxTime + "' + interval '10 second' order by log_dtimes limit 1";
	}
	
	private String getAuditLogQuery(String app_name, String module_name, String ref_id) {
		return "select * from audit.app_audit_log where app_name ='" + app_name + "' and module_name='" + module_name
				+ "' and ref_id ='" + ref_id + "' order by log_dtimes limit 1";
	}	

}
