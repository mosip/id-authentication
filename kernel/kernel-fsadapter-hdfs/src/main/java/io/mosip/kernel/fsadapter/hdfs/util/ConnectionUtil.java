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
	 * Private constructor for ConnectionUtil
	 */
	private ConnectionUtil() {
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
			} catch (NoSuchAlgorithmException | LoginException | URISyntaxException | IOException e) {
				throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
						HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
			}
		}
		return configuredFileSystem;
	}

	private Configuration prepareConfiguration() {
		Configuration configuration = new Configuration();
		configuration.set("fs.defaultFS", nameNodeUrl);
		configuration.set("hadoop.security.authentication", "kerberos");
		configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
		configuration.set("fs.file.impl", LocalFileSystem.class.getName());
		configuration.set("dfs.client.use.datanode.hostname", "true");
		configuration.set("dfs.data.transfer.protection", "authentication");
		System.setProperty("java.security.krb5.conf", getClass().getClassLoader().getResource("krb5.conf").getPath());
		UserGroupInformation.setConfiguration(configuration);
		return configuration;
	}

	private void loginUser(final String principal, final String pass)
			throws URISyntaxException, NoSuchAlgorithmException, LoginException, IOException {

		URIParameter uriParameter = new URIParameter(getClass().getClassLoader().getResource("jaas.conf").toURI());
		LoginContext loginContext = new LoginContext("HdfsAuth", new Subject(), (Callback[] callbacks) -> {
			for (Callback callback : callbacks) {
				if (callback instanceof NameCallback) {
					((NameCallback) callback).setName(principal);
				}
				if (callback instanceof PasswordCallback) {
					((PasswordCallback) callback).setPassword(pass.toCharArray());
				}
			}
		}, javax.security.auth.login.Configuration.getInstance("JavaLoginConfig", uriParameter));
		loginContext.login();
		UserGroupInformation.loginUserFromSubject(loginContext.getSubject());
	}
}