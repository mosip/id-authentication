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
import io.mosip.authentication.idRepository.fw.util.IdRepoTestsUtil;
import io.mosip.testrunner.MosipTestRunner;

/**
 * The class hold all the run config path available in runconfiguration file
 * 
 * @author Vignesh
 *
 */
public class IdaRunConfig extends RunConfig{
	
	private  Logger logger = Logger.getLogger(IdaRunConfig.class);
	private  String endPointUrl;
	private  String ekycPath;
	private  String encryptUtilBaseUrl;
	private  String encryptionPath;
	private  String encodePath;
	private  String decodePath;
	private  String scenarioPath;
	private  String srcPath;
	private  String authPath;
	private  String internalAuthPath;
	private  String otpPath;
	private  String userDirectory;
	private  String testDataPath;
	private  String idRepoEndPointUrl;
	private  String dbKernelTableName;
	private  String dbKernelSchemaName;
	private  String dbKernelUserName;
	private  String dbKernelPwd;
	private  String testType;
	private  String PinPath;
	private  String generateUINPath;
	private  String idRepoRetrieveDataPath;
	private  String idRepoCreateUINRecordPath;
	private  String storeUINDataPath;
	private  String dbIdaTableName;
	private  String dbIdaSchemaName;
	private  String dbIdaUserName;
	private  String dbIdaPwd;	
	private  String dbAuditTableName;
	private  String dbAuditSchemaName;
	private  String dbAuditUserName;
	private  String dbAuditPwd;
	private  String encodeFilePath;
	private  String dbKernelUrl;
	private  String dbIdaUrl;
	private  String dbAuditUrl;
	private  String decodeFilePath;
	private  String vidGenPath;
	private  String testDataFolderName;
	private  String authVersion;
	private  String clientidsecretkey;
	private String moduleFolderName;
	private String idRepoCreateVIDRecordPath;
	private String idRepoUpdateVIDStatusPath;
	private String idRepoVersion;
	private String decryptPath;
	private String uinIdentityMapper;
	private String internalEncryptionPath;
	private String validateSignaturePath;
	private String encryptionPort;
	private String idRepoRegenerateVID;
	private String idRepoRetrieveUINByVIDPath;
	
	public String getEncryptionPort() {
		return encryptionPort;
	}
	public void setEncryptionPort(String encryptionPort) {
		this.encryptionPort = encryptionPort;
	}
	public String getValidateSignaturePath() {
		return validateSignaturePath;
	}
	public void setValidateSignaturePath(String validateSignaturePath) {
		this.validateSignaturePath = validateSignaturePath;
	}
	public String getInternalEncryptionPath() {
		return internalEncryptionPath;
	}
	public void setInternalEncryptionPath(String internalEncryptionPath) {
		this.internalEncryptionPath = internalEncryptionPath;
	}
	public String getUinIdentityMapper() {
		return uinIdentityMapper;
	}
	public void setUinIdentityMapper(String uinIdentityMapper) {
		this.uinIdentityMapper = uinIdentityMapper;
	}
	public String getDecryptPath() {
		return decryptPath;
	}
	public void setDecryptPath(String decryptPath) {
		this.decryptPath = decryptPath;
	}
	public String getIdRepoVersion() {
		return idRepoVersion;
	}
	public void setIdRepoVersion(String idRepoVersion) {
		this.idRepoVersion = idRepoVersion;
	}
	/**
	 * The method get endpoint url for IDA
	 * 
	 * @return string
	 */
	public String getEndPointUrl() {
		return endPointUrl;
	}
	/**
	 * The method set endpoint url
	 * 
	 * @param endPointUrl
	 */
	public void setEndPointUrl(String endPointUrl) {
		this.endPointUrl = endPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
	}
	/**
	 * The method get ekyc url path
	 * 
	 * @returnstring 
	 */
	public  String getEkycPath() {
		return ekycPath;
	}
	/**
	 * The method set ekyc url path
	 * 
	 * @param ekycPath
	 */
	public  void setEkycPath(String ekycPath) {
		this.ekycPath = ekycPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get encryption endpoint path
	 * 
	 * @return string
	 */
	public  String getEncryptUtilBaseUrl() {
		return encryptUtilBaseUrl;
	}

	/**
	 * The method will set encryption endpoint path
	 * 
	 * @param encryptUtilBaseUrl
	 */
	public  void setEncryptUtilBaseUrl(String encryptUtilBaseUrl) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String actualUrl = encryptUtilBaseUrl.replace("$hostname$", inetAddress.getHostName().toLowerCase());
			actualUrl=actualUrl.replace("$port$", RunConfigUtil.objRunConfig.getEncryptionPort());
			this.encryptUtilBaseUrl = actualUrl;
		} catch (Exception e) {
			logger.error("Execption in RunConfig " + e.getMessage());
		}
	}
	/**
	 * The method will get encryption path
	 * 
	 * @return string
	 */
	public  String getEncryptionPath() {
		return encryptionPath;
	}
	/**
	 * The method set encryption path
	 * 
	 * @param encryptionPath
	 */
	public  void setEncryptionPath(String encrypPath) {
		this.encryptionPath = encrypPath;
	}
	/**
	 * The method get encode path
	 * 
	 * @return string
	 */
	public  String getEncodePath() {
		return encodePath;
	}
	/**
	 * The method set encode path
	 * 
	 * @param encodePath
	 */
	public  void setEncodePath(String encPath) {
		this.encodePath = encPath;
	}
	/**
	 * The method get decode path 
	 * 
	 * @return string
	 */
	public  String getDecodePath() {
		return decodePath;
	}
	/**
	 * The method set decode path
	 * 
	 * @param decodePath
	 */
	public  void setDecodePath(String decodePath) {
		this.decodePath = decodePath;
	}
	/**
	 * The method get scenario path of current test execution
	 * 
	 * @return string
	 */
	public  String getScenarioPath() {
		return scenarioPath;
	}
	/**
	 * The method set scenatio path of current test execution
	 * 
	 * @param scenarioPath
	 */
	public  void setScenarioPath(String scenarioPath) {
		this.scenarioPath = scenarioPath;
	}
	/**
	 * The method get source path from config file
	 * 
	 * @return string
	 */
	public  String getSrcPath() {
		return srcPath;
	}
	/**
	 * The method set src path
	 * 
	 * @param srcPath
	 */
	public  void setSrcPath(String srcPath) {
		this.srcPath = MosipTestRunner.getGlobalResourcePath();
	}
	/**
	 * The method get auth path
	 * 
	 * @return String
	 */
	public  String getAuthPath() {
		return authPath;
	}
	/**
	 * The method set auth path
	 * 
	 * @param authPath
	 */
	public  void setAuthPath(String authPath) {
		this.authPath = authPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get internal auth path
	 * 
	 * @return string
	 */
	public  String getInternalAuthPath() {
		return internalAuthPath;
	}
	/**
	 * The method set internal auth path
	 * 
	 * @param internalAuthPath
	 */
	public  void setInternalAuthPath(String internalAuthPath) {
		this.internalAuthPath = internalAuthPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get otp path
	 * 
	 * @return string
	 */
	public  String getOtpPath() {
		return otpPath;
	}
	/**
	 * The method set otp path
	 * 
	 * @param otpPath
	 */
	public  void setOtpPath(String otpPath) {
		this.otpPath = otpPath.replace("$authVersion$", getAuthVersion());
	}	
	/**
	 * The method get current test data path
	 * 
	 * @return string
	 */
	public  String getTestDataPath() {
		return testDataPath;
	}
	/**
	 * The method set current test data path
	 * 
	 * @param testDataPath
	 */
	public  void setTestDataPath(String testDataPath) {
		this.testDataPath = testDataPath;
	}
	/**
	 * The method get user directory of project
	 * 
	 * @return string
	 */
	public  String getUserDirectory() {
		return userDirectory;
	}
	/**
	 * The method set user directory
	 */
	public  void setUserDirectory() {
		Path currentDir = Paths.get(".");
		String path =currentDir.toFile().getAbsolutePath().toString();
		path=path.substring(0, path.length()-1);
		this.userDirectory = path;
	}
	/**
	 * The method get idrepo endpoint url
	 * 
	 * @return string
	 */
	public  String getIdRepoEndPointUrl() {
		return idRepoEndPointUrl;
	}
	/**
	 * The method set idrepo endpoint url
	 * 
	 * @param idRepoEndPointUrl
	 */
	public  void setIdRepoEndPointUrl(String idRepoEndPointUrl) {
		this.idRepoEndPointUrl = idRepoEndPointUrl.replace("$endpoint$", System.getProperty("env.endpoint"));
	}
	
	/**
	 * The method set configuration 
	 * 
	 * @param testDataPath
	 * @param testDataFileName
	 * @param testType
	 */
	public  void setConfig(String testDataPath,String testDataFileName,String testType) {
		setIdRepoVersion(AuthTestsUtil.getPropertyValue("idrepoVersion"));
		setAuthVersion(AuthTestsUtil.getPropertyValue("authVersion"));
		setEndPointUrl(AuthTestsUtil.getPropertyValue("endPointUrl"));
		setEkycPath(AuthTestsUtil.getPropertyValue("ekycPath"));
		setSrcPath(AuthTestsUtil.getPropertyValue("srcPath"));
		setAuthPath(AuthTestsUtil.getPropertyValue("authPath"));
		setInternalAuthPath(AuthTestsUtil.getPropertyValue("internalAuthPath"));
		setOtpPath(AuthTestsUtil.getPropertyValue("otpPath"));
		setEncryptionPort(AuthTestsUtil.getPropertyValue(System.getProperty("env.user")+".encryptionPort"));
		setEncryptUtilBaseUrl(AuthTestsUtil.getPropertyValue("encryptUtilBaseUrl"));
		setEncryptionPath(AuthTestsUtil.getPropertyValue("encryptionPath"));
		setEncodePath(AuthTestsUtil.getPropertyValue("encodePath"));
		setDecodePath(AuthTestsUtil.getPropertyValue("decodePath"));
		setDecryptPath(AuthTestsUtil.getPropertyValue("decryptPath"));
		setInternalEncryptionPath(AuthTestsUtil.getPropertyValue("internalEncryptionPath"));
		setValidateSignaturePath(AuthTestsUtil.getPropertyValue("validateSignaturePath"));
		setUserDirectory();
		setTestDataPath(testDataPath);	
		setIdRepoEndPointUrl(AuthTestsUtil.getPropertyValue("idRepoEndPointUrl"));
		setIdRepoRetrieveDataPath(AuthTestsUtil.getPropertyValue("idRepoRetrieveDataPath"));
		setIdRepoCreateVIDRecordPath(AuthTestsUtil.getPropertyValue("idRepoCreateVIDRecordPath"));
		setIdRepoUpdateVIDStatusPath(AuthTestsUtil.getPropertyValue("idRepoUpdateVIDStatusPath"));
		setIdRepoRegenerateVID(AuthTestsUtil.getPropertyValue("idRepoRegenerateVidPath"));
		setIdRepoRetrieveUINByVIDPath(AuthTestsUtil.getPropertyValue("idRepoRetrieveUinByVidPath"));
		setDbKernelTableName(AuthTestsUtil.getPropertyValue("dbKernelTableName"));
		setDbKernelSchemaName(AuthTestsUtil.getPropertyValue("dbKernelSchemaName"));
		setDbKernelUserName(AuthTestsUtil.getPropertyValue("dbKernelUserName"));
		setDbKernelPwd(AuthTestsUtil.getPropertyValue("dbKernelPwd"));
		File testDataFilePath = new File(RunConfigUtil.getResourcePath()
		+ testDataPath + testDataFileName);
		setFilePathFromTestdataFileName(testDataFilePath,testDataPath);
		setTestType(RunConfigUtil.getTestLevel());
		setGenerateUINPath(AuthTestsUtil.getPropertyValue("generateUINPath"));
		setPinPath(AuthTestsUtil.getPropertyValue("staticPinPath"));
		setIdRepoCreateUINRecordPath(AuthTestsUtil.getPropertyValue("idRepoCreateUINRecordPath"));
		setStoreUINDataPath(AuthTestsUtil.getPropertyValue("storeUINDataPath"));
		setDbIdaTableName(AuthTestsUtil.getPropertyValue("dbIdaTableName"));
		setDbIdaSchemaName(AuthTestsUtil.getPropertyValue("dbIdaSchemaName"));
		setDbIdaUserName(AuthTestsUtil.getPropertyValue("dbIdaUserName"));
		setDbIdaPwd(AuthTestsUtil.getPropertyValue("dbIdaPwd"));
		setDbAuditTableName(AuthTestsUtil.getPropertyValue("dbAuditTableName"));
		setDbAuditSchemaName(AuthTestsUtil.getPropertyValue("dbAuditSchemaName"));
		setDbAuditUserName(AuthTestsUtil.getPropertyValue("dbAuditUserName"));
		setDbAuditPwd(AuthTestsUtil.getPropertyValue("dbAuditPwd"));
		setEncodeFilePath(AuthTestsUtil.getPropertyValue("encodeFilePath"));
		setDecodeFilePath(AuthTestsUtil.getPropertyValue("decodeFilePath"));
		setDbKernelUrl(AuthTestsUtil.getPropertyValue(System.getProperty("env.user")+".dbKernelUrl"));
		setDbIdaUrl(AuthTestsUtil.getPropertyValue(System.getProperty("env.user")+".dbIdaUrl"));
		setDbAuditUrl(AuthTestsUtil.getPropertyValue(System.getProperty("env.user")+".dbAuditUrl"));
		setClientidsecretkey(AuthTestsUtil.getPropertyValue("clientidsecretkey"));
		//loadingConfigFile
		loadErrorsData(getErrorsConfigPath());
		setUinIdentityMapper(AuthTestsUtil.getPropertyValue("uinIdentityMapper"));
	}	
	
	/**
	 * The method get kernal db table name
	 * 
	 * @return string
	 */
	public  String getDbKernelTableName() {
		return dbKernelTableName;
	}
	/**
	 * The method set kernel db table name
	 * 
	 * @param dbKernelTableName
	 */
	public  void setDbKernelTableName(String dbKernelTableName) {
		this.dbKernelTableName = dbKernelTableName;
	}
	/**
	 * The method get kernal db schema name
	 * 
	 * @return string
	 */
	public  String getDbKernelSchemaName() {
		return dbKernelSchemaName;
	}
	/**
	 * The method set kernal db schema name
	 * 
	 * @param dbKernelSchemaName
	 */
	public  void setDbKernelSchemaName(String dbKernelSchemaName) {
		this.dbKernelSchemaName = dbKernelSchemaName;
	}
	/**
	 * The method get db kernel user name
	 * 
	 * @return string
	 */
	public  String getDbKernelUserName() {
		return dbKernelUserName;
	}
	/**
	 * The method set db kernel username
	 * 
	 * @param dbKernelUserName
	 */
	public  void setDbKernelUserName(String dbKernelUserName) {
		this.dbKernelUserName = dbKernelUserName;
	}
	/**
	 * The method get kernel db password
	 * 
	 * @return string
	 */
	public  String getDbKernelPwd() {
		return dbKernelPwd;
	}
	/**
	 * The method set kernel db password
	 * 
	 * @param dbKernelPwd
	 */
	public  void setDbKernelPwd(String dbKernelPwd) {
		this.dbKernelPwd = dbKernelPwd;
	}
	
	/**
	 * The method set file path from test data file name
	 * 
	 * @param filePath
	 * @param testDataPath
	 */
	private  void setFilePathFromTestdataFileName(File filePath, String testDataPath) {
		String[] folderList = filePath.getName().split(Pattern.quote("."));
		String temp = "";
		for (int i = 1; i < folderList.length - 2; i++) {
			temp = temp + "/" + folderList[i];
		}
		String testDataFolderName = "";
		String moduleFolderName="";
		if (testDataPath.contains("\\")) {
			String[] list = testDataPath.split(Pattern.quote("\\\\"));
			testDataFolderName = list[1];
		} else if (testDataPath.contains("/")) {
			String[] list = testDataPath.split(Pattern.quote("/"));
			moduleFolderName=list[0];
			testDataFolderName = list[1];
		}
		setTestDataFolderName(testDataFolderName);
		setModuleFolderName(moduleFolderName);
		scenarioPath = temp;
		setScenarioPath(scenarioPath);
	}
	/**
	 * The method get test type of current execution
	 * 
	 * @return string
	 */
	public  String getTestType() {
		return testType;
	}
	/**
	 * The method set test type of current execution
	 * 
	 * @param testType
	 */
	public  void setTestType(String testType) {
		this.testType = testType;
	}	
	/**
	 * The method get  pin path
	 * 
	 * @return string
	 */
	public  String getPinPath() {
		return PinPath;
	}
	/**
	 * The method set  pin path
	 * 
	 * @param PinPath
	 */
	public  void setPinPath(String PinPath) {
		this.PinPath = PinPath.replace("$authVersion$", getAuthVersion());
	}
	/**
	 * The method get UIN generation path
	 * 
	 * @return string
	 */
	public  String getGenerateUINPath() {
		return generateUINPath;
	}
	/**
	 * The method set UIN generation path
	 * 
	 * @param generateUINPath
	 */
	public  void setGenerateUINPath(String generateUINPath) {
		this.generateUINPath = generateUINPath;
	}	
	
	/**
	 * The method get retrieve idrepo path
	 * 
	 * @return string
	 */
	public  String getIdRepoRetrieveDataPath() {
		return idRepoRetrieveDataPath;
	}
	/**
	 * The method set retrieve idrepo path
	 * 
	 * @param idRepoRetrieveDataPath
	 */
	public  void setIdRepoRetrieveDataPath(String idRepoRetrieveDataPath) {
		this.idRepoRetrieveDataPath = idRepoRetrieveDataPath;
	}
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @return string
	 */
	public  String getIdRepoCreateUINRecordPath() {
		return idRepoCreateUINRecordPath;
	}
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @param idRepoCreateUINRecordPath
	 */
	public  void setIdRepoCreateUINRecordPath(String idRepoCreateUINRecordPath) {
		this.idRepoCreateUINRecordPath = idRepoCreateUINRecordPath;
	}	
	/**
	 * The method get store UIN Data path
	 * 
	 * @return string
	 */
	public  String getStoreUINDataPath() {
		return storeUINDataPath;
	}
	/**
	 * The method set store UIN Data path
	 * 
	 * @param storeUINDataPath
	 */
	public  void setStoreUINDataPath(String storeUINDataPath) {
		this.storeUINDataPath = storeUINDataPath;
	}	
	/**
	 * The method set IDA db table name
	 * 
	 * @return string
	 */
	public  String getDbIdaTableName() {
		return dbIdaTableName;
	}
	/**
	 * The method set ida db table name
	 * 
	 * @param dbIdaTableName
	 */
	public  void setDbIdaTableName(String dbIdaTableName) {
		this.dbIdaTableName = dbIdaTableName;
	}
	/**
	 * The method get ida table name
	 * 
	 * @return string
	 */
	public  String getDbIdaSchemaName() {
		return dbIdaSchemaName;
	}
	/**
	 * The method get ida db schema name
	 * 
	 * @param dbIdaSchemaName
	 */
	public  void setDbIdaSchemaName(String dbIdaSchemaName) {
		this.dbIdaSchemaName = dbIdaSchemaName;
	}
	/**
	 * The method get ida db user name
	 * 
	 * @return string
	 */
	public  String getDbIdaUserName() {
		return dbIdaUserName;
	}
	/**
	 * The method set ida db user name
	 * 
	 * @param dbIdaUserName
	 */
	public  void setDbIdaUserName(String dbIdaUserName) {
		this.dbIdaUserName = dbIdaUserName;
	}
	/**
	 * The method get ida db password
	 * 
	 * @return string
	 */
	public  String getDbIdaPwd() {
		return dbIdaPwd;
	}
	/**
	 * The method set ida db password
	 * 
	 * @param dbIdaPwd
	 */
	public  void setDbIdaPwd(String dbIdaPwd) {
		this.dbIdaPwd = dbIdaPwd;
	}	
	/**
	 * The method get db audit table name
	 * 
	 * @return string
	 */
	public  String getDbAuditTableName() {
		return dbAuditTableName;
	}
	/**
	 * The method set db audit table name
	 * 
	 * @param dbAuditTableName
	 */
	public  void setDbAuditTableName(String dbAuditTableName) {
		this.dbAuditTableName = dbAuditTableName;
	}
	/**
	 * The method get db audit schema name
	 * 
	 * @return string
	 */
	public  String getDbAuditSchemaName() {
		return dbAuditSchemaName;
	}
	/**
	 * The method set db audit schema name
	 * 
	 * @param dbAuditSchemaName
	 */
	public  void setDbAuditSchemaName(String dbAuditSchemaName) {
		this.dbAuditSchemaName = dbAuditSchemaName;
	}
	/**
	 * The method get db audit user name
	 * 
	 * @return string
	 */
	public  String getDbAuditUserName() {
		return dbAuditUserName;
	}
	/**
	 * The method set db audit user name
	 * 
	 * @param dbAuditUserName
	 */
	public  void setDbAuditUserName(String dbAuditUserName) {
		this.dbAuditUserName = dbAuditUserName;
	}
	/**
	 * The method get db audit password
	 * 
	 * @return string
	 */
	public  String getDbAuditPwd() {
		return dbAuditPwd;
	}
	/**
	 * The method will get audit db password
	 * 
	 * @param dbAuditPwd
	 */
	public  void setDbAuditPwd(String dbAuditPwd) {
		this.dbAuditPwd = dbAuditPwd;
	}
	
	/**
	 * The method get encode file path
	 * 
	 * @return string
	 */
	public  String getEncodeFilePath() {
		return encodeFilePath;
	}
	/**
	 * The method will set encode file path
	 * 
	 * @param encodeFile
	 */
	public  void setEncodeFilePath(String encodeFile) {
		this.encodeFilePath = encodeFile;
	}	
	/**
	 * The method get decode file path
	 * 
	 * @return string
	 */
	public  String getDecodeFilePath() {
		return decodeFilePath;
	}
	/**
	 * The method set decode file path
	 * 
	 * @param decodeFilePath
	 */
	public  void setDecodeFilePath(String decodeFilePath) {
		this.decodeFilePath = decodeFilePath;
	}	
	
	/**
	 * The method get kernel db url
	 * 
	 * @return string
	 */
	public  String getDbKernelUrl() {
		return dbKernelUrl;
	}
	/**
	 * The method set kernel db url 
	 * 
	 * @param dbKernelUrl
	 */
	public  void setDbKernelUrl(String dbKernelUrl) {
		this.dbKernelUrl = dbKernelUrl;
	}
	/**
	 * The method get IDA db url
	 * 
	 * @return string
	 */
	public  String getDbIdaUrl() {
		return dbIdaUrl;
	}
	/**
	 * The method set ida db url
	 * 
	 * @param dbIdaUrl
	 */
	public  void setDbIdaUrl(String dbIdaUrl) {
		this.dbIdaUrl = dbIdaUrl;
	}
	/**
	 * The method set audit db url
	 * 
	 * @return string
	 */
	public  String getDbAuditUrl() {
		return dbAuditUrl;
	}
	/**
	 * The method set DB audit url
	 * 
	 * @param dbAuditUrl
	 */
	public  void setDbAuditUrl(String dbAuditUrl) {
		this.dbAuditUrl = dbAuditUrl;
	}	
	/**
	 * The method set VID generation path
	 * 
	 * @return
	 */
	public  String getVidGenPath() {
		return vidGenPath;
	}
	/**
	 * The method set VID generation path
	 * 
	 * @param vidGenPath
	 */
	public  void setVidGenPath(String vidGenPath) {
		this.vidGenPath = vidGenPath.replace("$authVersion$", getAuthVersion());
	}	
	/**
	 * The method get test data folder name of current test execution
	 * 
	 * @return string
	 */
	public  String getTestDataFolderName() {
		return testDataFolderName;
	}
	/**
	 * The method set test data folder name of current execution
	 * 
	 * @param testDataFolderName
	 */
	public  void setTestDataFolderName(String testDataFolderName) {
		this.testDataFolderName = testDataFolderName;
	}	
	/**
	 * The method get current auth version 
	 * 
	 * @return string
	 */
	public  String getAuthVersion() {
		return authVersion;
	}
	/**
	 * The method set current auth version from config file
	 * 
	 * @param authVersion
	 */
	public  void setAuthVersion(String authVersion) {
		this.authVersion = authVersion;
	}
	
	/**
	 * The method get error config path
	 * 
	 * @return string
	 */
	public  String getErrorsConfigPath() {
		return "ida/" + getTestDataFolderName() + "/RunConfig/errorCodeMsg.yml";
	}
	
	/**
	 * The method load yml error test data
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private  void loadErrorsData(String path) {
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(
					new File(RunConfigUtil.getResourcePath() + path).getAbsoluteFile());
			ErrorsDto.setErrors((Map<String, Map<String, Map<String, String>>>) yaml.load(inputStream));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public  String getClientidsecretkey() {
		return clientidsecretkey;
	}
	public  void setClientidsecretkey(String cleintIdSecKey) {
		this.clientidsecretkey = cleintIdSecKey;
	}
	@Override
	public String getModuleFolderName() {
		return this.moduleFolderName;
	}
	@Override
	public void setModuleFolderName(String moduleFolderName) {
		this.moduleFolderName=moduleFolderName;
		
	}
	@Override
	public String getGenerateVIDPath() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setGenerateVIDPath(String generateVIDPath) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getIdRepoCreateVIDRecordPath() {
		return this.idRepoCreateVIDRecordPath;
	}
	@Override
	public void setIdRepoCreateVIDRecordPath(String idRepoCreateVIDRecordPath) {
		this.idRepoCreateVIDRecordPath=idRepoCreateVIDRecordPath;		
	}
	@Override
	public String getIdRepoUpdateVIDStatusPath() {
		return this.idRepoUpdateVIDStatusPath;
	}
	@Override
	public void setIdRepoUpdateVIDStatusPath(String idRepoUpdateVIDStatusPath) {
		this.idRepoUpdateVIDStatusPath=idRepoUpdateVIDStatusPath;		
	}
	@Override
	public String getIdRepoRetrieveIdentityByUin() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setIdRepoRetrieveIdentityByUin(String idRepoRetrieveIdentityByUin) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getIdRepoRetrieveUINByVIDPath() {
		return this.idRepoRetrieveUINByVIDPath;
	}
	@Override
	public void setIdRepoRetrieveUINByVIDPath(String idRepoRetrieveUINByVIDPath) {
		this.idRepoRetrieveUINByVIDPath=idRepoRetrieveUINByVIDPath;	
	}
	@Override
	public String getIdRepoRetrieveIdentityByRid() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setIdRepoRetrieveIdentityByRid(String idRepoRetrieveIdentityByRid) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getIdRepoRegenerateVID() {
		return this.idRepoRegenerateVID;
	}
	@Override
	public void setIdRepoRegenerateVID(String idRepoRegenerateVID) {
		this.idRepoRegenerateVID=idRepoRegenerateVID;		
	}
}
