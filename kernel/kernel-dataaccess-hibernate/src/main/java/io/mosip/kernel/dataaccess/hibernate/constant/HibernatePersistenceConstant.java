/**
 * 
 */
package io.mosip.kernel.dataaccess.hibernate.constant;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class HibernatePersistenceConstant {

	/**
	 * Private constructor for HibernatePersistenceConstants
	 */
	private HibernatePersistenceConstant() {
	}

	/**
	 * The string constant boot password
	 */
	public static final String BOOT_PASS = "jdbc.bootpassword";

	/**
	 * The string constant boot password key
	 */
	public static final String BOOT_PASS_KEY = "bootPassword=";

	/**
	 * The string constant jdbc driver
	 */
	public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";

	/**
	 * The string constant jdbc url
	 */
	public static final String JDBC_URL = "javax.persistence.jdbc.url";

	/**
	 * The string constant jdbc password
	 */
	public static final String JDBC_PASS = "javax.persistence.jdbc.password";

	/**
	 * The string constant jdbc user
	 */
	public static final String JDBC_USER = "javax.persistence.jdbc.user";
	/**
	 * The string constant cache query property
	 */
	public static final String CACHE_QUERY_PROPERTY = "javax.persistence.cache.storeMode";
	/**
	 * The string constant my sql dialect
	 */
	public static final String MY_SQL5_DIALECT = "org.hibernate.dialect.MySQL5Dialect";
	/**
	 * The string constant my sql dialect
	 */
	public static final String POSTGRESQL_95_DIALECT = "org.hibernate.dialect.PostgreSQL95Dialect";
	/**
	 * The string constant for hibernate statistics
	 */
	public static final String HIBERNATE_GENERATE_STATISTICS = "hibernate.generate_statistics";
	/**
	 * The string constant for use_structured_entries
	 */
	public static final String HIBERNATE_CACHE_USE_STRUCTURED_ENTRIES = "hibernate.cache.use_structured_entries";
	/**
	 * The string constant for use_query_cache
	 */
	public static final String HIBERNATE_CACHE_USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
	/**
	 * The string constant for use_second_level_cache
	 */
	public static final String HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
	/**
	 * The string constant for charSet
	 */
	public static final String HIBERNATE_CONNECTION_CHAR_SET = "hibernate.connection.charSet";
	/**
	 * The string constant for format_sql
	 */
	public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
	/**
	 * The string constant for show_sql
	 */
	public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	/**
	 * The string constant for dialect
	 */
	public static final String HIBERNATE_DIALECT = "hibernate.dialect";
	/**
	 * The string constant for hbm2ddl
	 */
	public static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
	/**
	 * The string constant for non_contextual_creation
	 */
	public static final String HIBERNATE_NON_CONTEXTUAL_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
	/**
	 * The string constant for current_session_context_class
	 */
	public static final String HIBERNATE_CURRENT_SESSION_CONTEXT = "hibernate.current_session_context_class";

	/**
	 * The string constant mosip package
	 */
	public static final String MOSIP_PACKAGE = "io.mosip.*";

	/**
	 * The string constant false
	 */
	public static final String FALSE = "false";
	/**
	 * The string constant utf8
	 */
	public static final String UTF8 = "utf8";
	/**
	 * The string constant true
	 */
	public static final String TRUE = "true";

	/**
	 * The string constant update
	 */
	public static final String UPDATE = "update";
	/**
	 * The string constant jta
	 */
	public static final String JTA = "jta";
	/**
	 * The string constant hibernate
	 */
	public static final String HIBERNATE = "hibernate";
	/**
	 * 
	 */
	public static final String HIBERNATE_EJB_INTERCEPTOR = "hibernate.ejb.interceptor";
	/**
	 * 
	 */
	public static final String EMPTY_INTERCEPTOR = "hibernate.empty.interceptor";

}
