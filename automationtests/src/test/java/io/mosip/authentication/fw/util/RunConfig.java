package io.mosip.authentication.fw.util;

public abstract class RunConfig {

	/**
	 * The method get endpoint url for IDA
	 * 
	 * @return string
	 */
	public abstract String getEndPointUrl();
	/**
	 * The method set endpoint url
	 * 
	 * @param endPointUrl
	 */
	public abstract void setEndPointUrl(String endPointUrl);
	/**
	 * The method get ekyc url path
	 * 
	 * @returnstring 
	 */
	public abstract String getEkycPath();
	/**
	 * The method set ekyc url path
	 * 
	 * @param ekycPath
	 */
	public abstract void setEkycPath(String ekycPath);
	/**
	 * The method get encryption endpoint path
	 * 
	 * @return string
	 */
	public abstract String getEncryptUtilBaseUrl();

	/**
	 * The method will set encryption endpoint path
	 * 
	 * @param encryptUtilBaseUrl
	 */
	public abstract void setEncryptUtilBaseUrl(String encryptUtilBaseUrl);
	/**
	 * The method will get encryption path
	 * 
	 * @return string
	 */
	public abstract String getEncryptionPath();
	/**
	 * The method set encryption path
	 * 
	 * @param encryptionPath
	 */
	public abstract void setEncryptionPath(String encryptionPath);
	/**
	 * The method get encode path
	 * 
	 * @return string
	 */
	public abstract String getEncodePath();
	/**
	 * The method set encode path
	 * 
	 * @param encodePath
	 */
	public abstract void setEncodePath(String encodePath);
	/**
	 * The method get decode path 
	 * 
	 * @return string
	 */
	public abstract String getDecodePath();
	/**
	 * The method set decode path
	 * 
	 * @param decodePath
	 */
	public abstract void setDecodePath(String decodePath);
	/**
	 * The method get scenario path of current test execution
	 * 
	 * @return string
	 */
	public abstract String getScenarioPath();
	/**
	 * The method set scenatio path of current test execution
	 * 
	 * @param scenarioPath
	 */
	public abstract void setScenarioPath(String scenarioPath);
	/**
	 * The method get source path from config file
	 * 
	 * @return string
	 */
	public abstract String getSrcPath();
	/**
	 * The method set src path
	 * 
	 * @param srcPath
	 */
	public abstract void setSrcPath(String srcPath);
	/**
	 * The method get auth path
	 * 
	 * @return String
	 */
	public abstract String getAuthPath();
	/**
	 * The method set auth path
	 * 
	 * @param authPath
	 */
	public abstract void setAuthPath(String authPath);
	/**
	 * The method get internal auth path
	 * 
	 * @return string
	 */
	public abstract String getInternalAuthPath();
	/**
	 * The method set internal auth path
	 * 
	 * @param internalAuthPath
	 */
	public abstract void setInternalAuthPath(String internalAuthPath);
	/**
	 * The method get otp path
	 * 
	 * @return string
	 */
	public abstract String getOtpPath();
	/**
	 * The method set otp path
	 * 
	 * @param otpPath
	 */
	public abstract void setOtpPath(String otpPath);	
	/**
	 * The method get current test data path
	 * 
	 * @return string
	 */
	public abstract String getTestDataPath();
	/**
	 * The method set current test data path
	 * 
	 * @param testDataPath
	 */
	public abstract void setTestDataPath(String testDataPath);
	/**
	 * The method get user directory of project
	 * 
	 * @return string
	 */
	public abstract String getUserDirectory();
	/**
	 * The method set user directory
	 */
	public abstract void setUserDirectory();
	/**
	 * The method get idrepo endpoint url
	 * 
	 * @return string
	 */
	public abstract String getIdRepoEndPointUrl();
	/**
	 * The method set idrepo endpoint url
	 * 
	 * @param idRepoEndPointUrl
	 */
	public abstract void setIdRepoEndPointUrl(String idRepoEndPointUrl);
	
	/**
	 * The method set configuration 
	 * 
	 * @param testDataPath
	 * @param testDataFileName
	 * @param testType
	 */
	public abstract void setConfig(String testDataPath,String testDataFileName,String testType);	
	
	/**
	 * The method get kernal db table name
	 * 
	 * @return string
	 */
	public abstract String getDbKernelTableName();
	/**
	 * The method set kernel db table name
	 * 
	 * @param dbKernelTableName
	 */
	public abstract void setDbKernelTableName(String dbKernelTableName);
	/**
	 * The method get kernal db schema name
	 * 
	 * @return string
	 */
	public abstract String getDbKernelSchemaName();
	/**
	 * The method set kernal db schema name
	 * 
	 * @param dbKernelSchemaName
	 */
	public abstract void setDbKernelSchemaName(String dbKernelSchemaName);
	/**
	 * The method get db kernel user name
	 * 
	 * @return string
	 */
	public abstract String getDbKernelUserName();
	/**
	 * The method set db kernel username
	 * 
	 * @param dbKernelUserName
	 */
	public abstract void setDbKernelUserName(String dbKernelUserName);
	/**
	 * The method get kernel db password
	 * 
	 * @return string
	 */
	public abstract String getDbKernelPwd();
	/**
	 * The method set kernel db password
	 * 
	 * @param dbKernelPwd
	 */
	public abstract void setDbKernelPwd(String dbKernelPwd);
	/**
	 * The method get test type of current execution
	 * 
	 * @return string
	 */
	public abstract String getTestType();
	/**
	 * The method set test type of current execution
	 * 
	 * @param testType
	 */
	public abstract void setTestType(String testType);	
	/**
	 * The method get UIN generation path
	 * 
	 * @return string
	 */
	public abstract String getGenerateUINPath();
	/**
	 * The method set UIN generation path
	 * 
	 * @param generateUINPath
	 */
	public abstract void setGenerateUINPath(String generateUINPath);	
	
	/**
	 * The method get retrieve idrepo path
	 * 
	 * @return string
	 */
	public abstract String getIdRepoRetrieveDataPath();
	/**
	 * The method set retrieve idrepo path
	 * 
	 * @param idRepoRetrieveDataPath
	 */
	public abstract void setIdRepoRetrieveDataPath(String idRepoRetrieveDataPath);
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @return string
	 */
	public abstract String getIdRepoCreateUINRecordPath();
	/**
	 * The method set create UIN record idrepo path
	 * 
	 * @param idRepoCreateUINRecordPath
	 */
	public abstract void setIdRepoCreateUINRecordPath(String idRepoCreateUINRecordPath);	
	/**
	 * The method get store UIN Data path
	 * 
	 * @return string
	 */
	public abstract String getStoreUINDataPath();
	/**
	 * The method set store UIN Data path
	 * 
	 * @param storeUINDataPath
	 */
	public abstract void setStoreUINDataPath(String storeUINDataPath);	
	/**
	 * The method set IDA db table name
	 * 
	 * @return string
	 */
	public abstract String getDbIdaTableName();
	/**
	 * The method set ida db table name
	 * 
	 * @param dbIdaTableName
	 */
	public abstract void setDbIdaTableName(String dbIdaTableName);
	/**
	 * The method get ida table name
	 * 
	 * @return string
	 */
	public abstract String getDbIdaSchemaName();
	/**
	 * The method get ida db schema name
	 * 
	 * @param dbIdaSchemaName
	 */
	public abstract void setDbIdaSchemaName(String dbIdaSchemaName);
	/**
	 * The method get ida db user name
	 * 
	 * @return string
	 */
	public abstract String getDbIdaUserName();
	/**
	 * The method set ida db user name
	 * 
	 * @param dbIdaUserName
	 */
	public abstract void setDbIdaUserName(String dbIdaUserName);
	/**
	 * The method get ida db password
	 * 
	 * @return string
	 */
	public abstract String getDbIdaPwd();
	/**
	 * The method set ida db password
	 * 
	 * @param dbIdaPwd
	 */
	public abstract void setDbIdaPwd(String dbIdaPwd);	
	/**
	 * The method get db audit table name
	 * 
	 * @return string
	 */
	public abstract String getDbAuditTableName();
	/**
	 * The method set db audit table name
	 * 
	 * @param dbAuditTableName
	 */
	public abstract void setDbAuditTableName(String dbAuditTableName);
	/**
	 * The method get db audit schema name
	 * 
	 * @return string
	 */
	public abstract String getDbAuditSchemaName();
	/**
	 * The method set db audit schema name
	 * 
	 * @param dbAuditSchemaName
	 */
	public abstract void setDbAuditSchemaName(String dbAuditSchemaName);
	/**
	 * The method get db audit user name
	 * 
	 * @return string
	 */
	public abstract String getDbAuditUserName();
	/**
	 * The method set db audit user name
	 * 
	 * @param dbAuditUserName
	 */
	public abstract void setDbAuditUserName(String dbAuditUserName);
	/**
	 * The method get db audit password
	 * 
	 * @return string
	 */
	public abstract String getDbAuditPwd();
	/**
	 * The method will get audit db password
	 * 
	 * @param dbAuditPwd
	 */
	public abstract void setDbAuditPwd(String dbAuditPwd);
	
	/**
	 * The method get encode file path
	 * 
	 * @return string
	 */
	public abstract String getEncodeFilePath();
	/**
	 * The method will set encode file path
	 * 
	 * @param encodeFile
	 */
	public abstract void setEncodeFilePath(String encodeFile);
	/**
	 * The method get decode file path
	 * 
	 * @return string
	 */
	public abstract String getDecodeFilePath();
	/**
	 * The method set decode file path
	 * 
	 * @param decodeFilePath
	 */
	public abstract void setDecodeFilePath(String decodeFilePath);	
	
	/**
	 * The method get kernel db url
	 * 
	 * @return string
	 */
	public abstract String getDbKernelUrl();
	/**
	 * The method set kernel db url 
	 * 
	 * @param dbKernelUrl
	 */
	public abstract void setDbKernelUrl(String dbKernelUrl);
	/**
	 * The method get IDA db url
	 * 
	 * @return string
	 */
	public abstract String getDbIdaUrl();
	/**
	 * The method set ida db url
	 * 
	 * @param dbIdaUrl
	 */
	public abstract void setDbIdaUrl(String dbIdaUrl);
	/**
	 * The method set audit db url
	 * 
	 * @return string
	 */
	public abstract String getDbAuditUrl();
	/**
	 * The method set DB audit url
	 * 
	 * @param dbAuditUrl
	 */
	public abstract void setDbAuditUrl(String dbAuditUrl);	
	/**
	 * The method set VID generation path
	 * 
	 * @return
	 */
	public abstract String getVidGenPath();
	/**
	 * The method set VID generation path
	 * 
	 * @param vidGenPath
	 */
	public abstract void setVidGenPath(String vidGenPath);	
	/**
	 * The method get test data folder name of current test execution
	 * 
	 * @return string
	 */
	public abstract String getTestDataFolderName();
	/**
	 * The method set test data folder name of current execution
	 * 
	 * @param testDataFolderName
	 */
	public abstract void setTestDataFolderName(String testDataFolderName);	
	/**
	 * The method get current auth version 
	 * 
	 * @return string
	 */
	public abstract String getAuthVersion();
	/**
	 * The method set current auth version from config file
	 * 
	 * @param authVersion
	 */
	public abstract void setAuthVersion(String authVersion);
	
	/**
	 * The method get error config path
	 * 
	 * @return string
	 */
	public abstract String getErrorsConfigPath();	
	public abstract String getClientidsecretkey();
	public abstract void setClientidsecretkey(String clientidsecretkey);
	public abstract String getModuleFolderName();
	public abstract void setModuleFolderName(String moduleFolderName);
	public abstract String getGenerateVIDPath();
	public abstract void setGenerateVIDPath(String generateVIDPath);
	public abstract String getIdRepoCreateVIDRecordPath();
	public abstract void setIdRepoCreateVIDRecordPath(String idRepoCreateVIDRecordPath);
	public abstract String getIdRepoUpdateVIDStatusPath();
	public abstract void setIdRepoUpdateVIDStatusPath(String IdRepoUpdateVIDStatusPath);
	public abstract String getIdRepoVersion();
	public abstract void setIdRepoVersion(String idRepoVersion);
	public abstract String getDecryptPath();
	public abstract void setDecryptPath(String decryptPath);
	public abstract void setUinIdentityMapper(String uinIdentityMapper);
	public abstract String getUinIdentityMapper();
	public abstract String getInternalEncryptionPath();
	public abstract void setInternalEncryptionPath(String internalEncryptionPath);

}
