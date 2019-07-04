package io.mosip.kernel.auth.constant;

public class LdapConstants {

	private LdapConstants() {

	}

	public static final String CN = "cn";
	public static final String SN = "sn";
	public static final String MAIL = "mail";
	public static final String MOBILE = "mobile";
	public static final String DOB = "dob";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String GENDER_CODE = "genderCode";
	public static final String OBJECT_CLASS = "objectClass";
	public static final String INET_ORG_PERSON = "inetOrgPerson";
	public static final String USER_PASSWORD = "userPassword";
	public static final String RID = "rid";
	public static final String ORGANIZATIONAL_PERSON = "organizationalPerson";
	public static final String IS_ACTIVE = "isActive";
	public static final String PERSON = "person";
	public static final String TOP = "top";
	public static final String USER_DETAILS = "userDetails";
	public static final String ROLE_OCCUPANT = "roleOccupant";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static final String LDAP_INITAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	public static final String PWD_ACCOUNT_LOCKED_TIME_ATTRIBUTE = "pwdAccountLockedTime";
	public static final String PWD_FAILURE_TIME_ATTRIBUTE = "pwdFailureTime";
}
