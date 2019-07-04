package io.mosip.kernel.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MosipEnvironment implements EnvironmentAware {

	@Autowired
	private Environment environment;

	private final String jwtSecret = "auth.jwt.secret";
	private final String tokenBase = "auth.jwt.base";
	private final String tokenExpiry = "auth.jwt.expiry";
	private final String refreshTokenExpiry = "auth.jwt.refresh.expiry";
	private final String authTokenHeader = "auth.token.header";
	private final String authRefreshTokenHeader = "auth.refreshtoken.header";

	private final String ldapSvcUrl = "ldap.svc.url";
	private final String ldapAuthenticate = "ldap.api.authenticate";
	private final String ldapVerifyOtpUser = "ldap.api.otp.user.verify";

	private final String generateOtpApi = "otp.manager.api.generate";
	private final String verifyOtpUserApi = "otp.manager.api.verify";

	private final String otpSenderEmailApi = "otp.sender.api.email.send";
	private final String otpSenderSmsApi = "otp.sender.api.sms.send";

	private final String masterDataTemplateApi = "masterdata.api.template";
	private final String masterDataOtpTemplate = "masterdata.api.template.otp";

	private final String propurl = ".datastore.ipaddress";
	private final String propport = ".datastore.port";
	private final String propusername = ".datastore.username";
	private final String proppassword = ".datastore.password";
	private final String propschema = ".datastore.schema";
	private final String propdriver = ".datastore.driverClassName";

	private final String datastores = "datastores";

	private String rolesSearchBase = "ldap.roles.base";
	private String rolesSearchPrefix = "ldap.roles.search.prefix";
	private String rolesSearchSuffix = "ldap.roles.search.suffix";
	private String ldapRolesClass = "ldap.roles.class";

	private String otpPrimaryLanguage = "auth.primary.language";
	
	private String otpSecondaryLanguage = "auth.secondary.language";

	private String uinGetDetailsUrl = "idrepo.api.getuindetails";

	private String authSlidingWindowExp = "auth.token.sliding.window.exp";
	
	private String authPrefix = "mosip.kernel.auth.";
	
	private String authAppId = ".app.id";
	
	private String authAppUserId = ".client.id";
	
	private String authSecretKey = ".secret.key";

	public String getUinGetDetailsUrl() {
		return environment.getProperty(uinGetDetailsUrl);
	}
	
	public String getAppUserId(String appId) {
		return environment.getProperty(authPrefix+appId+authAppUserId);
	}
	
	public String getAppId(String appId) {
		return environment.getProperty(authPrefix+appId+authAppId);
	}
	
	public String getSecretKey(String appId) {
		return environment.getProperty(authPrefix+appId+authSecretKey);
	}

	public Integer getAuthSlidingWindowExp() {
		return Integer.valueOf(environment.getProperty(authSlidingWindowExp));
	}

	public String getPrimaryLanguage() {
		return environment.getProperty(otpPrimaryLanguage);
	}
	
	public String getSecondaryLanguage() {
		return environment.getProperty(otpSecondaryLanguage);
	}

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	public String getAuthTokenHeader() {
		return environment.getProperty(authTokenHeader);
	}

	public String getRefreshTokenHeader() {
		return environment.getProperty(authRefreshTokenHeader);
	}

	public String getDriverName(String datasource) {
		return environment.getProperty(datasource + propdriver);
	}

	public String getUrl(String dataStore) {
		return environment.getProperty(dataStore + propurl);
	}

	public String getPort(String dataStore) {
		return environment.getProperty(dataStore + propport);
	}

	public String getDataStore(String app) {
		return environment.getProperty(app);
	}

	public String getUserName(String dataStore) {
		return environment.getProperty(dataStore + propusername);
	}

	public String getPassword(String dataStore) {
		return environment.getProperty(dataStore + proppassword);
	}

	public String getSchemas(String schema) {
		return environment.getProperty(schema + propschema);
	}

	public String getJwtSecret() {
		return environment.getProperty(jwtSecret);
	}

	public String getTokenBase() {
		return environment.getProperty(tokenBase);
	}

	public Integer getTokenExpiry() {
		return Integer.parseInt(environment.getProperty(tokenExpiry));
	}

	public Long getRefreshTokenExpiry() {
		return Long.valueOf(environment.getProperty(refreshTokenExpiry));
	}

	public String getLdapSvcUrl() {
		return environment.getProperty(ldapSvcUrl);
	}

	public String getLdapAuthenticate() {
		return environment.getProperty(ldapAuthenticate);
	}

	public String getLdapVerifyOtpUser() {
		return environment.getProperty(ldapVerifyOtpUser);
	}

	public String getGenerateOtpApi() {
		return environment.getProperty(generateOtpApi);
	}

	public String getVerifyOtpUserApi() {
		return environment.getProperty(verifyOtpUserApi);
	}

	public String getOtpSenderEmailApi() {
		return environment.getProperty(otpSenderEmailApi);
	}

	public String getOtpSenderSmsApi() {
		return environment.getProperty(otpSenderSmsApi);
	}

	public String getMasterDataTemplateApi() {
		return environment.getProperty(masterDataTemplateApi);
	}

	public String getMasterDataOtpTemplate() {
		return environment.getProperty(masterDataOtpTemplate);
	}

	public String getDataStores() {
		return environment.getProperty(datastores);
	}

	public String getLdapRolesClass() {
		return environment.getProperty(ldapRolesClass);
	}

	public String getRolesSearchBase() {
		return environment.getProperty(rolesSearchBase);
	}

	public String getRolesSearchPrefix() {
		return environment.getProperty(rolesSearchPrefix);
	}

	public String getRolesSearchSuffix() {
		return environment.getProperty(rolesSearchSuffix);
	}

}