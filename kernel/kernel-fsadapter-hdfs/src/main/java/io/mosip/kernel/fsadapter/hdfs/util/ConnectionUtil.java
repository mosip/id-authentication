package io.mosip.kernel.fsadapter.hdfs.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.URIParameter;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.fsadapter.hdfs.constant.HDFSAdapterErrorCode;

/**
 * This class gets connection to DFS.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */
@Component
public class ConnectionUtil {

	/**
	 * Field for name-node url
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.name-node-url}")
	private String nameNodeUrl;

	/**
	 * Field for kdc domain
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.kdc-domain}")
	private String kdcDomain;

	/**
	 * Field for username
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.user-name}")
	private String userName;

	/**
	 * Field for userPass
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.user-pass}")
	private String userPass;

	/**
	 * Field for hadoop FileSystem
	 */
	private FileSystem configuredFileSystem;

	/**
	 * Instantiate a ConnectionUtil
	 */
	public ConnectionUtil() {
		// Used when a new bean is created for ConnectionUtil
	}

	/**
	 * Function to get instance of the hadoop filesystem with its configuration set
	 * 
	 * @return configured filesystem
	 */
	public FileSystem getConfiguredFileSystem() {
		if (configuredFileSystem == null) {
			Configuration configuration = prepareConfiguration();
			try {
				loginUser(userName + "@" + kdcDomain, userPass);
				return FileSystem.get(configuration);
			} catch (IOException e) {
				throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
						HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
			}
		}
		return configuredFileSystem;
	}

	/**
	 * Prepares hadoop configuration object with required properties
	 * 
	 * @return hadoop configuration
	 */
	private Configuration prepareConfiguration() {
		Configuration configuration = new Configuration();
		configuration.set("fs.defaultFS", nameNodeUrl);
		configuration.set("hadoop.security.authentication", "kerberos");
		configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
		configuration.set("fs.file.impl", LocalFileSystem.class.getName());
		configuration.set("dfs.client.use.datanode.hostname", "true");
		configuration.set("dfs.data.transfer.protection", "authentication");
		System.setProperty("java.security.krb5.conf", getClass().getClassLoader().getResource("krb5.conf").getPath());
		if (SystemUtils.IS_OS_WINDOWS) {
			System.setProperty("hadoop.home.dir", getClass().getClassLoader().getResource("hadoop-2.8.1").getPath());
		}
		UserGroupInformation.setConfiguration(configuration);
		return configuration;
	}

	/**
	 * Instantiate a new LoginContext object with user principal and user passkey
	 * and performs authentication
	 * 
	 * @param principal
	 *            the user principal
	 * @param passkey
	 *            the user passkey
	 * @throws IOException
	 *             if login fails
	 */
	private void loginUser(final String principal, final String passkey) throws IOException {
		URIParameter uriParameter = null;
		LoginContext loginContext = null;
		try {
			uriParameter = new URIParameter(getClass().getClassLoader().getResource("jaas.conf").toURI());
		} catch (URISyntaxException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.URI_SYNTAX_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.URI_SYNTAX_EXCEPTION.getErrorMessage(), e);
		}
		try {
			loginContext = new LoginContext("HdfsAuth", new Subject(), (Callback[] callbacks) -> {
				for (Callback callback : callbacks) {
					if (callback instanceof NameCallback) {
						((NameCallback) callback).setName(principal);
					}
					if (callback instanceof PasswordCallback) {
						((PasswordCallback) callback).setPassword(passkey.toCharArray());
					}
				}
			}, javax.security.auth.login.Configuration.getInstance("JavaLoginConfig", uriParameter));
			loginContext.login();
			UserGroupInformation.loginUserFromSubject(loginContext.getSubject());
		} catch (LoginException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.LOGIN_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.LOGIN_EXCEPTION.getErrorMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage(), e);
		}
	}
}