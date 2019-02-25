package io.mosip.authentication.fw.util;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

import io.mosip.testdata.TestDataConfig;
import io.mosip.testdata.TestDataUtil;

/**
 * Dto to hold all the run config path available in runconfiguration file
 * 
 * @author Vignesh
 *
 */
public class RunConfig extends IdaScriptsUtil{
	
	private static String endPointUrl;
	private static String ekycPath;
	private static String encryptUtilBaseUrl;
	private static String encryptionPath;
	private static String encodePath;
	public static String getEndPointUrl() {
		return endPointUrl;
	}
	public static void setEndPointUrl(String endPointUrl) {
		RunConfig.endPointUrl = endPointUrl;
	}
	public static String getEkycPath() {
		return ekycPath;
	}
	public static void setEkycPath(String ekycPath) {
		RunConfig.ekycPath = ekycPath;
	}
	public static String getEncryptUtilBaseUrl() {
		return encryptUtilBaseUrl;
	}
	public static void setEncryptUtilBaseUrl(String encryptUtilBaseUrl) {
		RunConfig.encryptUtilBaseUrl = encryptUtilBaseUrl;
	}
	public static String getEncryptionPath() {
		return encryptionPath;
	}
	public static void setEncryptionPath(String encryptionPath) {
		RunConfig.encryptionPath = encryptionPath;
	}
	public static String getEncodePath() {
		return encodePath;
	}
	public static void setEncodePath(String encodePath) {
		RunConfig.encodePath = encodePath;
	}
	public static String getDecodePath() {
		return decodePath;
	}
	public static void setDecodePath(String decodePath) {
		RunConfig.decodePath = decodePath;
	}
	public static String getScenarioPath() {
		return scenarioPath;
	}
	public static void setScenarioPath(String scenarioPath) {
		RunConfig.scenarioPath = scenarioPath;
	}
	public static String getSrcPath() {
		return srcPath;
	}
	public static void setSrcPath(String srcPath) {
		RunConfig.srcPath = srcPath;
	}
	public static String getAuthPath() {
		return authPath;
	}
	public static void setAuthPath(String authPath) {
		RunConfig.authPath = authPath;
	}
	public static String getInternalAuthPath() {
		return internalAuthPath;
	}
	public static void setInternalAuthPath(String internalAuthPath) {
		RunConfig.internalAuthPath = internalAuthPath;
	}
	public static String getOtpPath() {
		return otpPath;
	}
	public static void setOtpPath(String otpPath) {
		RunConfig.otpPath = otpPath;
	}
	private static String decodePath;
	private static String scenarioPath;
	private static String srcPath;
	private static String authPath;
	private static String internalAuthPath;
	private static String otpPath;
	private static String userDirectory;
	private static String testDataPath;
	
	public static String getTestDataPath() {
		return testDataPath;
	}
	public static void setTestDataPath(String testDataPath) {
		RunConfig.testDataPath = testDataPath;
	}
	public static String getUserDirectory() {
		return userDirectory;
	}
	public static void setUserDirectory() {
		RunConfig.userDirectory = System.getProperty("user.dir");
	}
	
	private static String idRepoEndPointUrl;
	public static String getIdRepoEndPointUrl() {
		return idRepoEndPointUrl;
	}
	public static void setIdRepoEndPointUrl(String idRepoEndPointUrl) {
		RunConfig.idRepoEndPointUrl = idRepoEndPointUrl;
	}
	
	public void setConfig(String testDataPath,String testDataFileName,String testType) {
		setEndPointUrl(getPropertyValue("endPointUrl"));
		setEkycPath(getPropertyValue("ekycPath"));
		setSrcPath(getPropertyValue("srcPath"));
		setAuthPath(getPropertyValue("authPath"));
		setInternalAuthPath(getPropertyValue("internalAuthPath"));
		setOtpPath(getPropertyValue("otpPath"));
		setEncryptUtilBaseUrl(getPropertyValue("encryptUtilBaseUrl"));
		setEncryptionPath(getPropertyValue("encryptionPath"));
		setEncodePath(getPropertyValue("encodePath"));
		setDecodePath(getPropertyValue("decodePath"));
		setUserDirectory();
		setTestDataPath(testDataPath);	
		setIdRepoEndPointUrl(getPropertyValue("idRepoEndPointUrl"));
		setIdRepoRetrieveDataPath(getPropertyValue("idRepoRetrieveDataPath"));
		setDbUrl(getPropertyValue("dbUrl"));
		setDbKernelTableName(getPropertyValue("dbKernelTableName"));
		setDbKernelSchemaName(getPropertyValue("dbKernelSchemaName"));
		setDbKernelUserName(getPropertyValue("dbKernelUserName"));
		setDbKernelPwd(getPropertyValue("dbKernelPwd"));
		File testDataFilePath = new File(RunConfig.getUserDirectory() + RunConfig.getSrcPath()
		+ testDataPath + testDataFileName);
		setFilePathFromTestdataFileName(testDataFilePath);
		setTestType(testType);
		setGenerateUINPath(getPropertyValue("generateUINPath"));
		setStaticPinPath(getPropertyValue("staticPinPath"));
		setIdRepoCreateUINRecordPath(getPropertyValue("idRepoCreateUINRecordPath"));
		setStoreUINDataPath(getPropertyValue("storeUINDataPath"));
		setDbIdaTableName(getPropertyValue("dbIdaTableName"));
		setDbIdaSchemaName(getPropertyValue("dbIdaSchemaName"));
		setDbIdaUserName(getPropertyValue("dbIdaUserName"));
		setDbIdaPwd(getPropertyValue("dbIdaPwd"));
		setDbAuditTableName(getPropertyValue("dbAuditTableName"));
		setDbAuditSchemaName(getPropertyValue("dbAuditSchemaName"));
		setDbAuditUserName(getPropertyValue("dbAuditUserName"));
		setDbAuditPwd(getPropertyValue("dbAuditPwd"));
	}
	
	private static String dbUrl;
	private static String dbKernelTableName;
	private static String dbKernelSchemaName;
	private static String dbKernelUserName;
	private static String dbKernelPwd;
	public static String getDbUrl() {
		return dbUrl;
	}
	public static void setDbUrl(String dbUrl) {
		RunConfig.dbUrl = dbUrl;
	}
	public static String getDbKernelTableName() {
		return dbKernelTableName;
	}
	public static void setDbKernelTableName(String dbKernelTableName) {
		RunConfig.dbKernelTableName = dbKernelTableName;
	}
	public static String getDbKernelSchemaName() {
		return dbKernelSchemaName;
	}
	public static void setDbKernelSchemaName(String dbKernelSchemaName) {
		RunConfig.dbKernelSchemaName = dbKernelSchemaName;
	}
	public static String getDbKernelUserName() {
		return dbKernelUserName;
	}
	public static void setDbKernelUserName(String dbKernelUserName) {
		RunConfig.dbKernelUserName = dbKernelUserName;
	}
	public static String getDbKernelPwd() {
		return dbKernelPwd;
	}
	public static void setDbKernelPwd(String dbKernelPwd) {
		RunConfig.dbKernelPwd = dbKernelPwd;
	}
	
	private void setFilePathFromTestdataFileName(File filePath) {
		String[] folderList = filePath.getName().split(Pattern.quote("."));
		String temp = "";
		for (int i = 1; i < folderList.length - 2; i++) {
			temp = temp + "\\" + folderList[i];
		}
		scenarioPath = temp;
		setScenarioPath(scenarioPath);
		String mapping = folderList[folderList.length - 2];
	}
	private static String testType;
	public static String getTestType() {
		return testType;
	}
	public static void setTestType(String testType) {
		RunConfig.testType = testType;
	}
	
	private static String staticPinPath;
	private static String generateUINPath;
	public static String getStaticPinPath() {
		return staticPinPath;
	}
	public static void setStaticPinPath(String staticPinPath) {
		RunConfig.staticPinPath = staticPinPath;
	}
	public static String getGenerateUINPath() {
		return generateUINPath;
	}
	public static void setGenerateUINPath(String generateUINPath) {
		RunConfig.generateUINPath = generateUINPath;
	}
	
	private static String idRepoRetrieveDataPath;
	private static String idRepoCreateUINRecordPath;
	public static String getIdRepoRetrieveDataPath() {
		return idRepoRetrieveDataPath;
	}
	public static void setIdRepoRetrieveDataPath(String idRepoRetrieveDataPath) {
		RunConfig.idRepoRetrieveDataPath = idRepoRetrieveDataPath;
	}
	public static String getIdRepoCreateUINRecordPath() {
		return idRepoCreateUINRecordPath;
	}
	public static void setIdRepoCreateUINRecordPath(String idRepoCreateUINRecordPath) {
		RunConfig.idRepoCreateUINRecordPath = idRepoCreateUINRecordPath;
	}
	
	private static String storeUINDataPath;
	public static String getStoreUINDataPath() {
		return storeUINDataPath;
	}
	public static void setStoreUINDataPath(String storeUINDataPath) {
		RunConfig.storeUINDataPath = storeUINDataPath;
	}
	
	private static String dbIdaTableName;
	private static String dbIdaSchemaName;
	private static String dbIdaUserName;
	public static String getDbIdaTableName() {
		return dbIdaTableName;
	}
	public static void setDbIdaTableName(String dbIdaTableName) {
		RunConfig.dbIdaTableName = dbIdaTableName;
	}
	public static String getDbIdaSchemaName() {
		return dbIdaSchemaName;
	}
	public static void setDbIdaSchemaName(String dbIdaSchemaName) {
		RunConfig.dbIdaSchemaName = dbIdaSchemaName;
	}
	public static String getDbIdaUserName() {
		return dbIdaUserName;
	}
	public static void setDbIdaUserName(String dbIdaUserName) {
		RunConfig.dbIdaUserName = dbIdaUserName;
	}
	public static String getDbIdaPwd() {
		return dbIdaPwd;
	}
	public static void setDbIdaPwd(String dbIdaPwd) {
		RunConfig.dbIdaPwd = dbIdaPwd;
	}
	private static String dbIdaPwd;
	
	private static String dbAuditTableName;
	private static String dbAuditSchemaName;
	private static String dbAuditUserName;
	private static String dbAuditPwd;
	public static String getDbAuditTableName() {
		return dbAuditTableName;
	}
	public static void setDbAuditTableName(String dbAuditTableName) {
		RunConfig.dbAuditTableName = dbAuditTableName;
	}
	public static String getDbAuditSchemaName() {
		return dbAuditSchemaName;
	}
	public static void setDbAuditSchemaName(String dbAuditSchemaName) {
		RunConfig.dbAuditSchemaName = dbAuditSchemaName;
	}
	public static String getDbAuditUserName() {
		return dbAuditUserName;
	}
	public static void setDbAuditUserName(String dbAuditUserName) {
		RunConfig.dbAuditUserName = dbAuditUserName;
	}
	public static String getDbAuditPwd() {
		return dbAuditPwd;
	}
	public static void setDbAuditPwd(String dbAuditPwd) {
		RunConfig.dbAuditPwd = dbAuditPwd;
	}

}
