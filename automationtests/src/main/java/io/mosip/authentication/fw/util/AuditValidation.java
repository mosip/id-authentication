package io.mosip.authentication.fw.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.authentication.fw.dto.OutputValidationDto;

/**
 * The class to handle audit log and auth transaction validation
 * @author M1049813
 *
 */
public class AuditValidation {
	
	private static File auth_txn_file;
	private static File audit_log_file;

	/**
	 * The method verify audit Transaction of IDA
	 *  
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return Map, Output Validation report
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static Map<String, List<OutputValidationDto>> verifyAuditTxn(File[] listOfFiles, String keywordToFind){
		auth_txn_file = FileUtil.getFileFromList(listOfFiles, keywordToFind);
		Map<String, String> exp = AuthTestsUtil.getPropertyAsMap(auth_txn_file.getAbsolutePath());
		Map<String, String> act = DbConnection.getDataForQuery(
				"select request_dtimes,response_dtimes,id,request_trn_id,auth_type_code,status_code,status_comment,lang_code,ref_id_type,ref_id,cr_dtimes from ida.auth_transaction where request_trn_id = '"
						+ exp.get("request_trn_id") + "' order by cr_dtimes desc limit 1",
				"IDA");
		AuthTestsUtil.generateMappingDic(auth_txn_file.getAbsolutePath().toString(), preconAuditKeywords(exp, act));
		return OutputValidationUtil.compareActuExpValue(act, exp, "Audit Transaction Validation");
	}

	/**
	 * The method verify audit log of IDA
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return Map, Output Validation report
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static Map<String, List<OutputValidationDto>> verifyAuditLog(File[] listOfFiles, String keywordToFind) {
		audit_log_file = FileUtil.getFileFromList(listOfFiles, keywordToFind);
		Map<String, String> exp = AuthTestsUtil.getPropertyAsMap(audit_log_file.getAbsolutePath());
		Map<String, String> act = null;
		if (FileUtil.verifyFilePresent(listOfFiles, "auth_transaction")) {
			Map<String, String> exp_auth_txn = AuthTestsUtil.getPropertyAsMap(auth_txn_file.getAbsolutePath());
			if (exp_auth_txn.containsKey("request_dtimes") && exp_auth_txn.containsKey("response_dtimes")) {
				act = DbConnection
						.getDataForQuery(
								getAuditLogQuery(exp.get("app_name"), exp.get("module_name"), exp.get("ref_id"),
										exp_auth_txn.get("request_dtimes"), exp_auth_txn.get("response_dtimes")),
								"AUDIT");
			} else {
				act = DbConnection.getDataForQuery(
						getAuditLogQuery(exp.get("app_name"), exp.get("module_name"), exp.get("ref_id")), "AUDIT");
			}
		} else {
			act = DbConnection.getDataForQuery(
					getAuditLogQuery(exp.get("app_name"), exp.get("module_name"), exp.get("ref_id")), "AUDIT");
		}
		return OutputValidationUtil.compareActuExpValue(act, exp, "Audit Log Validation");
	}	
	
	/**
	 * The method to precondtion audit impl keywords
	 * 
	 * @param exp, expected value as map
	 * @param act, actual value as map
	 * @return Map
	 */
	private static Map<String, String> preconAuditKeywords(Map<String, String> exp, Map<String, String> act) {
		for (Entry<String, String> temp : exp.entrySet()) {
			if (temp.getValue().contains("$FETCH$")) {
				exp.put(temp.getKey(), act.get(temp.getKey()));
			} else if (temp.getValue().contains("$") && temp.getValue().contains(":")
					&& temp.getValue().contains("audit.")) {
				String arr[] = temp.getValue().replace("$", "").split(Pattern.quote(":"));
				String value = arr[1];
				exp.put(temp.getKey(),
						AuthTestsUtil.getValueFromPropertyFile(auth_txn_file.getAbsolutePath().toString(), value));
			}
		}
		return exp;
	}	
	
	/**
	 * The method get audit log query with timestamp
	 * 
	 * @param app_name
	 * @param module_name
	 * @param ref_id
	 * @param action_dtimes_minTime
	 * @param action_dtimes_maxTime
	 * @return String, SQL Query
	 */
	private static String getAuditLogQuery(String app_name, String module_name, String ref_id, String action_dtimes_minTime,
			String action_dtimes_maxTime) {
		return "select * from audit.app_audit_log where app_name ='" + app_name + "' and module_name='" + module_name
				+ "' and ref_id ='" + ref_id + "' and action_dtimes >= timestamp '" + action_dtimes_minTime
				+ "'- interval '10 second'	and action_dtimes <= timestamp '" + action_dtimes_maxTime
				+ "' + interval '10 second' order by log_dtimes limit 1";
	}
	
	/**
	 * The method get audit log query
	 * 
	 * @param app_name
	 * @param module_name
	 * @param ref_id
	 * @return String, SQL Query
	 */
	private static String getAuditLogQuery(String app_name, String module_name, String ref_id) {
		return "select * from audit.app_audit_log where app_name ='" + app_name + "' and module_name='" + module_name
				+ "' and ref_id ='" + ref_id + "' order by log_dtimes limit 1";
	}	

}
