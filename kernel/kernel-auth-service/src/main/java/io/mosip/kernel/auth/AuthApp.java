package io.mosip.kernel.auth;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.directory.api.ldap.aci.UserClass.Name;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.LdapControl;

@SpringBootApplication
public class AuthApp {
	public static void main(String[] args) {
		SpringApplication.run(AuthApp.class, args);
	}

	@Bean
	public CommandLineRunner unblock() {
		return args -> {
			System.out.println("unblocking started");
			//execute("registration_supervisor");
			System.out.println("unblocking ended");
		};
	}

	@SuppressWarnings("unchecked")
	public void execute(String userId) throws LdapInvalidDnException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, AuthConstant.LDAP_INITAL_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, "ldap://52.172.11.190:10389");
		env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
		env.put(Context.SECURITY_CREDENTIALS, "secret");
		LdapContext context = null;
		try {
			context = new InitialLdapContext(env, null);
			LdapControl ldapControl = new LdapControl();
			context.setRequestControls(ldapControl.getControls());

			ModificationItem[] modItems = new ModificationItem[2];
			modItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(AuthConstant.PWD_ACCOUNT_LOCKED_TIME_ATTRIBUTE));
			modItems[1] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(AuthConstant.PWD_FAILURE_TIME_ATTRIBUTE));

			context.modifyAttributes("uid="+userId+",ou=people,c=morocco", modItems);
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (Exception e) {
				}
			}
		}
	}
}