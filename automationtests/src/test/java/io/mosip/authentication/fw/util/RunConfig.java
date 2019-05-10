
package io.mosip.authentication.fw.util;

import java.io.File; 
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import io.mosip.authentication.fw.dto.ErrorsDto;

/**
 * The class hold all the run config path available in runconfiguration file
 * 
 * @author Vignesh
 *
 */
public class RunConfig extends IdaScriptsUtil{
	
	private static Logger logger = Logger.getLogger(RunConfig.class);
	private static String endPointUrl;
	private static String ekycPath;
	private static String encryptUtilBaseUrl;
	private static String encryptionPath;
	private static String encodePath;
	private static String decodePath;
	private static String scenarioPath;
	private static String srcPath;
	private static String authPath;
	private static String internalAuthPath;
	private static String otpPath;
	private static String userDirectory;
	private static String testDataPath;
	private static String idRepoEndPointUrl;
	private static String dbKernelTableName;
	private static String dbKernelSchemaName;
	private static String dbKernelUserName;
	private static String dbKernelPwd;
	private static String testType;
	private static String staticPinPath;
	private static String generateUINPath;
	private static String idRepoRetrieveDataPath;
	private static String idRepoCreateUINRecordPath;
	private static String storeUINDataPath;
	private static String dbIdaTableName;
	private static String dbIdaSchemaName;
	private static String dbIdaUserName;
	private static String dbIdaPwd;	
	private static String dbAuditTableName;
	private static String dbAuditSchemaName;
	private static String dbAuditUserName;
	private static String dbAuditPwd;
	private static String encodeFilePath;
	private static String dbKernelUrl;
	private static String dbIdaUrl;
	private static String dbAuditUrl;
	private static String decodeFilePath;
	private static String vidGenPath;
	private static String testDataFolderName;
	private static String authVersion;
	
	/**
	 * The method get endpoint url for IDA
	 * 
	 * @return string
	 */
	public static String getEndPointUrl() {
		return endPointUrl;
	}
	/**
	 * The method set endpoint url
	 * 
	 * @param endPointUrl
	 */
	public static void setEndPointUrl(String endPointUrl) {
		RunConfig.endPointUrl = endPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
	}
	/**
	 * The method get ekyc url path
	 * 
	 * @returnstring 
	 */
	public static String getEkycPath() {
		return ekycPath;
	}
	/**
	 * The method set ekyc url path
	 * 
	 * @param ekycPath
	 */
	public static void setEkycPath(String ekycPath) {
		RunConfig.ekycPath = ekycPath.replace("$authVersion$", RunConfig.getAuthVersion());
	}
	/**
	 * The method get encryption endpoint path
	 * 
	 * @return string
	 */
	public static String getEncryptUtilBaseUrl() {
		return encryptUtilBaseUrl;
	}

	/**
	 * The method will set encryption endpoint path
	 * 
	 * @param encryptUtilBaseUrl
	 */
	public static void setEncryptUtilBaseUrl(String encryptUtilBaseUrl) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String actualUrl = encryptUtilBaseUrl.replace("$hostname$", inetAddress.getHostName().toLowerCase());
			RunConfig.encryptUtilBaseUrl = actualUrl;
		} catch (Exception e) {
			logger.error("Execption in RunConfig " + e.getMessage());
		}
	}
	/**
	 * The method will get encryption path
	 * 
	 * @return string
	 */
	public static String getEncryptionPath() {
		return encryptionPath;
	}
	/**
	 * The method set encryption path
	 * 
	 * @param encryptionPath
	 */
	public static void setEncryptionPath(String encryptionPath) {
		RunConfig.encryptionPath = encryptionPath;
	}
	/**
	 * The method get encode path
	 * 
	 * @return string
	 */
	public static String getEncodePath() {
		return encodePath;
	}
	/**
	 * The method set encode path
	 * 
	 * @param encodePath
	 */
	public static void setEncodePath(String encodePath) {
		RunConfig.encodePath = encodePath;
	}
	/**
	 * The method get decode path 
	 * 
	 * @return string
	 */
	public static String getDecodePath() {
		return decodePath;
	}
	/**
	 * The method set decode path
	 * 
	 * @param decodePath
	 */
	public static void setDecodePath(String decodePath) {
		RunConfig.decodePath = decodePath;
	}
	/**
	 * The method get scenario path of current test execution
	 * 
	 * @return string
	 */
	public static String getScenarioPath() {
		return scenarioPath;
	}
	/**
	 * The method set scenatio path of current test execution
	 * 
	 * @param scenarioPath
	 */
	public static void setScenarioPath(String scenarioPath) {
		RunConfig.scenarioPath = scenarioPath;
	}
	/**
	 * The method get source path from config file
	 * 
	 * @return string
	 */
	public static String getSrcPath() {
		return srcPath;
	}
	/**
	 * The method set src path
	 * 
	 * @param srcPath
	 */
	public static void setSrcPath(String srcPath) {
		RunConfig.srcPath = srcPath;
	}
	/**
	 * The method get auth path
	 * 
	 * @return String
	 */
	public static String getAuthPath() {
		return authPath;
	}
	/**
	 * The method set auth path
	 * 
	 * @param authPath
	 */
	public static void setAuthPath(String authPath) {
		RunConfig.authPath = authPath.replace("$authVersion$", RunConfig.getAuthVersion());
	}
	/**
	 * The method get internal auth path
	 * 
	 * @return string
	 */
	public static String getInternalAuthPath() {
		return internalAuthPath;
	}
	/**
	 * The method set internal auth path
	 * 
	 * @param internalAuthPath
	 */
	public static void setInternalAuthPath(String internalAuthPath) {
		RunConfig.internalAuthPath = internalAuthPath.replace("$authVersion$", RunConfig.getAuthVersion());
	}
	/**
	 * The method get otp path
	 * 
	 * @return string
	 */
	public static String getOtpPath() {
		return otpPath;
	}
	/**
	 * The method set otp path
	 * 
	 * @param otpPath
	 */
	public static void setOtpPath(String otpPath) {
		RunConfig.otpPath = otpPath.replace("$authVersion$", RunConfig.getAuthVersion());
	}	
	/**
	 * The method get current test data path
	 * 
	 * @return string
	 */
	public static String getTestDataPath() {
		return testDataPath;
	}
	/**
	 * The method set current test data path
	 * 
	 * @param testDataPath
	 */
	public static void setTestDataPath(String testDataPath) {
		RunConfig.testDataPath = testDataPath;
	}
	/**
	 * The method get user directory of project
	 * 
	 * @return string
	 */
	public static String getUserDirectory() {
		return userDirectory;
	}
	/**
	 * The method set user directory
	 */
	public static void setUserDirectory() {
		Path currentDir = Paths.get(".");
		String path =currentDir.toFile().getAbsolutePath().toString();
		path=path.substring(0, path.length()-1);
		RunConfig.userDirectory = path;
	}
	/**
	 * The method get idrepo endpoint url
	 * 
	 * @return string
	 */
	public static String getIdRepoEndPointUrl() {
		return idRepoEndPointUrl;
	}
	/**
	 * The method set idrepo endpoint url
	 * 
	 * @param idRepoEndPointUrl
	 */
	public static void setIdRepoEndPointUrl(String idRepoEndPointUrl) {
		RunConfig.idRepoEndPointUrl = idRepoEndPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
	}
	
	/**
	 * The method set configuration 
	 * 
	 * @param testDataPath
	 * @param testDataFileName
	 * @param testType
	 */
	public static void setConfig(String testDataPath,String testDataFileName,String testType) {
		setAuthVersion(getPropertyValue("authVersion"));
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
		setDbKernelTableName(getPropertyValue("dbKernelTableName"));
		setDbKernelSchemaName(getPropertyValue("dbKernelSchemaName"));
		setDbKernelUserName(getPropertyValue("dbKernelUserName"));
		setDbKernelPwd(getPropertyValue("dbKernelPwd"));
		File testDataFilePath = new File(/*RunConfig.getUserDirectory() +*/ RunConfig.getSrcPath()
		+ testDataPath + testDataFileName);
		setFilePathFromTestdataFileName(testDataFilePath,testDataPath);
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
		setEncodeFilePath(getPropertyValue("encodeFilePath"));
		setDecodeFilePath(getPropertyValue("decodeFilePath"));
		setDbKernelUrl(getPropertyValue("dbKernelUrl"));
		setDbIdaUrl(getPropertyValue("dbIdaUrl"));
		setDbAuditUrl(getPropertyValue("dbAuditUrl"));
		setVidGenPath(getPropertyValue("vidGenPath"));
		//loadingConfigFile
		loadErrorsData(getErrorsConfigPath());
	}	
	
	/**
	 * The method get kernal db table name
	 * 
	 * @return string
	 */
	public static String getDbKernelTableName() {
		return dbKernelTableName;
	}
	/**
	 * The method set kernel db table name
	 * 
	 * @param dbKernelTableName
	 */
	public static void setDbKernelTableName(String dbKernelTableName) {
		RunConfig.dbKernelTableName = dbKernelTableName;
	}
	/**
	 * The method get kernal db schema name
	 * 
	 * @return string
	 */
	public static String getDbKernelSchemaName() {
		return dbKernelSchemaName;
	}
	/**
	 * The method set kernal db schema name
	 * 
	 * @param dbKernelSchemaName
	 */
	public static void setDbKernelSchemaName(String dbKernelSchemaName) {
		RunConfig.dbKernelSchemaName = dbKernelSchemaName;
	}
	/**
	 * The method get db kernel user name
	 * 
	 * @return string
	 */
	public static String getDbKernelUserName() {
		return dbKernelUserName;
	}
	/**
	 * The method set db kernel username
	 * 
	 * @param dbKernelUserName
	 */
	public static void setDbKernelUserName(String dbKernelUserName) {
		RunConfig.dbKernelUserName = dbKernelUserName;
	}
	/**
	 * The method get kernel db password
	 * 
	 * @return string
	 */
	public static String getDbKernelPwd() {
		return dbKernelPwd;
	}
	/**
	 * The method set kernel db password
	 * 
	 * @param dbKernelPwd
	 */
	public static void setDbKernelPwd(String dbKernelPwd) {
		RunConfig.dbKernelPwd = dbKernelPwd;
	}
	
	/**
	 * The method set file path from test data file name
	 * 
	 * @param filePath
	 * @param testDataPath
	 */
	private static void setFilePathFromTestdataFileName(File filePath, String testDataPath) {
		String[] folderList = filePath.getName().split(Pattern.quote("."));
		String temp = "";
		for (int i = 1; i < folderList.length - 2; i++) {
			temp = temp + "/" + folderList[i];
		}
		String testDataFolderName = "";
		if (testDataPath.contains("\\")) {
			String[] list = testDataPath.split(Pattern.quote("\\\\"));
			testDataFolderName = list[1];
		} else if (testDataPath.contains("/")) {
			String[] list = testDataPath.split(Pattern.quote("/"));
			testDataFolderName = list[1];
		}
		setTestDataFolderName(testDataFolderName);
		scenarioPath = temp;
		setScenarioPath(scenarioPath);
	}
	/**
	 * The method get test type of current execution
	 * 
	 * @return string
	 */
	public static String getTestType() {
		return testType;
	}
	/**
	 * The method set test type of current execution
	 * 
	 * @param testType
	 */
	public static void setTestType(String testType) {
		RunConfig.testType = testType;
	}	
	/**
	 * The method get static pin path
	 * 
	 * @return string
	 */
	public static String getStaticPinPath() {
		return staticPinPath;
	}
	/**
	 * The method set static pin path
	 * 
	 * @param staticPinPath
	 */
	public static void setStaticPinPath(String staticPinPath) {
		RunConfig.staticPinPath = staticPinPath.replace("$authVersion$", RunConfig.getAuthVersion());
	}
	/**
	 * The method get UIN generation path
	 * 
	 * @return string
	 */
	public static String getGenerateUINPath() {
		return generateUINPath;
	}
	/**
	 * The method set UIN generation path
	 * 
	 * @param generateUINPath
	 */
	public static void setGenerateUINPath(String generateUINPath) {
		RunConfig.generateUINPath = generateUINPath;
	}	
	
	/**
	 * The method get retrieve idrepo path
	 * 
	 * @return string
	 */
	public static String getIdRepoRetrieveDataPath() {
		return idRepoRetrieveDataPath;
	}
	/**
	 * The method set retrieve idrepo path
	 * 
	 * @param idRepoRetrieveDataPath
	 */
	public static void setIdRepoRetrieveDataPath(String idRepoRetrieveDataPath) {
		RunConfig.idRepoRetrieveDataPath = idRepoRetrieveDataPath;
	}
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @return string
	 */
	public static String getIdRepoCreateUINRecordPath() {
		return idRepoCreateUINRecordPath;
	}
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @param idRepoCreateUINRecordPath
	 */
	public static void setIdRepoCreateUINRecordPath(String idRepoCreateUINRecordPath) {
		RunConfig.idRepoCreateUINRecordPath = idRepoCreateUINRecordPath;
	}	
	/**
	 * The method get store UIN Data path
	 * 
	 * @return string
	 */
	public static String getStoreUINDataPath() {
		return storeUINDataPath;
	}
	/**
	 * The method set store UIN Data path
	 * 
	 * @param storeUINDataPath
	 */
	public static void setStoreUINDataPath(String storeUINDataPath) {
		RunConfig.storeUINDataPath = storeUINDataPath;
	}	
	/**
	 * The method set IDA db table name
	 * 
	 * @return string
	 */
	public static String getDbIdaTableName() {
		return dbIdaTableName;
	}
	/**
	 * The method set ida db table name
	 * 
	 * @param dbIdaTableName
	 */
	public static void setDbIdaTableName(String dbIdaTableName) {
		RunConfig.dbIdaTableName = dbIdaTableName;
	}
	/**
	 * The method get ida table name
	 * 
	 * @return string
	 */
	public static String getDbIdaSchemaName() {
		return dbIdaSchemaName;
	}
	/**
	 * The method get ida db schema name
	 * 
	 * @param dbIdaSchemaName
	 */
	public static void setDbIdaSchemaName(String dbIdaSchemaName) {
		RunConfig.dbIdaSchemaName = dbIdaSchemaName;
	}
	/**
	 * The method get ida db user name
	 * 
	 * @return string
	 */
	public static String getDbIdaUserName() {
		return dbIdaUserName;
	}
	/**
	 * The method set ida db user name
	 * 
	 * @param dbIdaUserName
	 */
	public static void setDbIdaUserName(String dbIdaUserName) {
		RunConfig.dbIdaUserName = dbIdaUserName;
	}
	/**
	 * The method get ida db password
	 * 
	 * @return string
	 */
	public static String getDbIdaPwd() {
		return dbIdaPwd;
	}
	/**
	 * The method set ida db password
	 * 
	 * @param dbIdaPwd
	 */
	public static void setDbIdaPwd(String dbIdaPwd) {
		RunConfig.dbIdaPwd = dbIdaPwd;
	}	
	/**
	 * The method get db audit table name
	 * 
	 * @return string
	 */
	public static String getDbAuditTableName() {
		return dbAuditTableName;
	}
	/**
	 * The method set db audit table name
	 * 
	 * @param dbAuditTableName
	 */
	public static void setDbAuditTableName(String dbAuditTableName) {
		RunConfig.dbAuditTableName = dbAuditTableName;
	}
	/**
	 * The method get db audit schema name
	 * 
	 * @return string
	 */
	public static String getDbAuditSchemaName() {
		return dbAuditSchemaName;
	}
	/**
	 * The method set db audit schema name
	 * 
	 * @param dbAuditSchemaName
	 */
	public static void setDbAuditSchemaName(String dbAuditSchemaName) {
		RunConfig.dbAuditSchemaName = dbAuditSchemaName;
	}
	/**
	 * The method get db audit user name
	 * 
	 * @return string
	 */
	public static String getDbAuditUserName() {
		return dbAuditUserName;
	}
	/**
	 * The method set db audit user name
	 * 
	 * @param dbAuditUserName
	 */
	public static void setDbAuditUserName(String dbAuditUserName) {
		RunConfig.dbAuditUserName = dbAuditUserName;
	}
	/**
	 * The method get db audit password
	 * 
	 * @return string
	 */
	public static String getDbAuditPwd() {
		return dbAuditPwd;
	}
	/**
	 * The method will get audit db password
	 * 
	 * @param dbAuditPwd
	 */
	public static void setDbAuditPwd(String dbAuditPwd) {
		RunConfig.dbAuditPwd = dbAuditPwd;
	}
	
	/**
	 * The method get encode file path
	 * 
	 * @return string
	 */
	public static String getEncodeFilePath() {
		return encodeFilePath;
	}
	/**
	 * The method will set encode file path
	 * 
	 * @param encodeFile
	 */
	public static void setEncodeFilePath(String encodeFile) {
		RunConfig.encodeFilePath = encodeFile;
	}	
	/**
	 * The method get decode file path
	 * 
	 * @return string
	 */
	public static String getDecodeFilePath() {
		return decodeFilePath;
	}
	/**
	 * The method set decode file path
	 * 
	 * @param decodeFilePath
	 */
	public static void setDecodeFilePath(String decodeFilePath) {
		RunConfig.decodeFilePath = decodeFilePath;
	}	
	
	/**
	 * The method get kernel db url
	 * 
	 * @return string
	 */
	public static String getDbKernelUrl() {
		return dbKernelUrl;
	}
	/**
	 * The method set kernel db url 
	 * 
	 * @param dbKernelUrl
	 */
	public static void setDbKernelUrl(String dbKernelUrl) {
		RunConfig.dbKernelUrl = dbKernelUrl;
	}
	/**
	 * The method get IDA db url
	 * 
	 * @return string
	 */
	public static String getDbIdaUrl() {
		return dbIdaUrl;
	}
	/**
	 * The method set ida db url
	 * 
	 * @param dbIdaUrl
	 */
	public static void setDbIdaUrl(String dbIdaUrl) {
		RunConfig.dbIdaUrl = dbIdaUrl;
	}
	/**
	 * The method set audit db url
	 * 
	 * @return string
	 */
	public static String getDbAuditUrl() {
		return dbAuditUrl;
	}
	/**
	 * The method set DB audit url
	 * 
	 * @param dbAuditUrl
	 */
	public static void setDbAuditUrl(String dbAuditUrl) {
		RunConfig.dbAuditUrl = dbAuditUrl;
	}	
	/**
	 * The method set VID generation path
	 * 
	 * @return
	 */
	public static String getVidGenPath() {
		return vidGenPath;
	}
	/**
	 * The method set VID generation path
	 * 
	 * @param vidGenPath
	 */
	public static void setVidGenPath(String vidGenPath) {
		RunConfig.vidGenPath = vidGenPath.replace("$authVersion$", RunConfig.getAuthVersion());
	}	
	/**
	 * The method get test data folder name of current test execution
	 * 
	 * @return string
	 */
	public static String getTestDataFolderName() {
		return testDataFolderName;
	}
	/**
	 * The method set test data folder name of current execution
	 * 
	 * @param testDataFolderName
	 */
	public static void setTestDataFolderName(String testDataFolderName) {
		RunConfig.testDataFolderName = testDataFolderName;
	}	
	/**
	 * The method get current auth version 
	 * 
	 * @return string
	 */
	public static String getAuthVersion() {
		return authVersion;
	}
	/**
	 * The method set current auth version from config file
	 * 
	 * @param authVersion
	 */
	public static void setAuthVersion(String authVersion) {
		RunConfig.authVersion = authVersion;
	}
	
	/**
	 * The method get error config path
	 * 
	 * @return string
	 */
	public static String getErrorsConfigPath() {
		return "ida/" + RunConfig.getTestDataFolderName() + "/RunConfig/errorCodeMsg.yml";
	}
	
	/**
	 * The method load yml error test data
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private static void loadErrorsData(String path) {
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(
					new File("./" + RunConfig.getSrcPath() + path).getAbsoluteFile());
			ErrorsDto.setErrors((Map<String, Map<String, Map<String, String>>>) yaml.load(inputStream));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}

