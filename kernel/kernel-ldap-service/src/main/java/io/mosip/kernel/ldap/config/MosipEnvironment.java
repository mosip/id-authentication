package io.mosip.kernel.ldap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MosipEnvironment implements EnvironmentAware {

    @Autowired
    private Environment environment;

    private String ldapHost = "ldap.server.host";
    private String ldapPort = "ldap.server.port";
    private String ldapAdminDn = "ldap.admin.dn";
    private String ldapAdminPassword = "ldap.admin.password";
    private String userDnPrefix = "ldap.userdn.prefix";
    private String userDnSuffix = "ldap.userdn.suffix";
    private String rolesSearchBase = "ldap.roles.base";
    private String rolesSearchPrefix = "ldap.roles.search.prefix";
    private String rolesSearchSuffix = "ldap.roles.search.suffix";
    private String allrolesSearch="mosip.kernel.syncdata.search-all-roles";

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public String getLdapHost() {
        return environment.getProperty(ldapHost);
    }

    public Integer getLdapPort() {
        return Integer.parseInt(environment.getProperty(ldapPort));
    }

    public String getLdapAdminDn() {
        return environment.getProperty(ldapAdminDn);
    }

    public String getLdapAdminPassword() {
        return environment.getProperty(ldapAdminPassword);
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

    public String getUserDnPrefix() {
        return environment.getProperty(userDnPrefix);
    }

    public String getUserDnSuffix() {
        return environment.getProperty(userDnSuffix);
    }
    
    public String getAllRoles() {
        return environment.getProperty(allrolesSearch);
    }
}