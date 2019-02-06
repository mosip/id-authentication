package io.mosip.kernel.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
@Configuration
public class MosipEnvironment implements EnvironmentAware {

    @Autowired
    private Environment environment;

    private final String jwtSecret = "auth.jwt.secret";
    private final String tokenBase = "auth.jwt.base";
    private final String tokenExpiry = "auth.jwt.expiry";
    private final String authTokenHeader = "auth.token.header";

    private final String ldapSvcUrl = "ldap.svc.url";
    private final String ldapAuthenticate = "ldap.api.authenticate";
    private final String ldapVerifyOtpUser = "ldap.api.otp.user.verify";
    private final String ldapGetAllRoles="ldap.api.all.roles";
    private final String ldapGetAllUserDetails="ldap.api.user.details";

    private final String otpManagerSvcUrl = "otp.manager.svc.url";
    private final String generateOtpApi = "otp.manager.api.generate";
    private final String verifyOtpUserApi = "otp.manager.api.verify";

    private final String otpSenderSvcUrl = "otp.sender.svc.url";
    private final String otpSenderEmailApi = "otp.sender.api.email.send";
    private final String otpSenderSmsApi = "otp.sender.api.sms.send";

    private final String masterDataUrl = "masterdata.svc.url";
    private final String masterDataTemplateApi = "masterdata.api.template";
    private final String masterDataOtpTemplate = "masterdata.api.template.otp";

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public String getJwtSecret() {
        return environment.getProperty(jwtSecret);
    }

    public String getTokenBase() {
        return environment.getProperty(tokenBase);
    }

    public Long getTokenExpiry() {
        return Long.parseLong(environment.getProperty(tokenExpiry));
    }

    public String getAuthTokenHeader() {
        return environment.getProperty(authTokenHeader);
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

    public String getOtpManagerSvcUrl() {
        return environment.getProperty(otpManagerSvcUrl);
    }

    public String getGenerateOtpApi() {
        return environment.getProperty(generateOtpApi);
    }

    public String getVerifyOtpUserApi() {
        return environment.getProperty(verifyOtpUserApi);
    }

    public String getOtpSenderSvcUrl() {
        return environment.getProperty(otpSenderSvcUrl);
    }

    public String getOtpSenderEmailApi() {
        return environment.getProperty(otpSenderEmailApi);
    }

    public String getOtpSenderSmsApi() {
        return environment.getProperty(otpSenderSmsApi);
    }

    public String getMasterDataUrl() {
        return environment.getProperty(masterDataUrl);
    }

    public String getMasterDataTemplateApi() {
        return environment.getProperty(masterDataTemplateApi);
    }

    public String getMasterDataOtpTemplate() {
        return environment.getProperty(masterDataOtpTemplate);
    }
    
    public String getLdapAllRolesUrl() {
    	return environment.getProperty(ldapGetAllRoles);
    }
    
    public String getLdapAllUsersUrl() {
    	return environment.getProperty(ldapGetAllUserDetails);
    }
}