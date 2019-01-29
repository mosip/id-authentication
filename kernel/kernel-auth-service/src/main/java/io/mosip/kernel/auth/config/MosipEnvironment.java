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
    private final String authTokenHeader = "auth.token.header";

    private final String ldapSvcUrl = "ldap.svc.url";
    private final String ldapAuthenticate = "ldap.api.authenticate";

    private final String otpSvcUrl = "otp.svc.url";
    private final String triggerOtpApi = "otp.api.trigger";
    private final String verifyOtpApi = "otp.api.verify";

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

    public String getOtpSvcUrl() {
        return environment.getProperty(otpSvcUrl);
    }

    public String getTriggerOtpApi() {
        return environment.getProperty(triggerOtpApi);
    }

    public String getVerifyOtpApi() {
        return environment.getProperty(verifyOtpApi);
    }
}