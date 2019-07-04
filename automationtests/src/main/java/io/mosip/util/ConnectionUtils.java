package io.mosip.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedExceptionAction;
import java.security.URIParameter;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.SaslRpcServer.AuthMethod;
import org.apache.hadoop.security.UserGroupInformation;

import io.mosip.registrationProcessor.util.RegProcApiRequests;


/**
 * This class gets connection to DFS.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 */

public class ConnectionUtils {

	/**
	 * Field for name-node url
	 */
	//@Value("${mosip.kernel.fsadapter.hdfs.name-node-url}")
	//private String nameNodeUrl = "hdfs://13.71.115.204:51000";
	
	/**
	 * Field for kdc domain
	 */
	//@Value("${mosip.kernel.fsadapter.hdfs.kdc-domain}")
	//private String kdcDomain = "NODE-MASTER.SOUTHINDIA.CLOUDAPP.AZURE.COM";

	/**
	 * Field for username
	 */
	//@Value("${mosip.kernel.fsadapter.hdfs.user-name}")
	private String userName = "qa-regprocessor";

	/**
	 * Field for userPass
	 */
	//@Value("${mosip.kernel.fsadapter.hdfs.user-pass}")
	private String userPass = "Mosip@dev123";

	//@Value("${mosip.kernel.fsadapter.hdfs.authentication-enabled:false}")
	private boolean isAuthEnable = false;

	/**
	 * Field for hadoop FileSystem
	 */
	private FileSystem configuredFileSystem;

	private static final String HADOOP_HOME = "hadoop-lib";
	private static final String WIN_UTIL = "winutils.exe";

	/**
	 * hadoop lib path
	 */
	private Path hadoopLibPath;

	/**
	 * Instantiate a ConnectionUtil
	 */
	public ConnectionUtils() {
		// Used when a new bean is created for ConnectionUtil
	}

	/**
	 * Function to get instance of the hadoop filesystem with its configuration set
	 * 
	 * @return configured filesystem
	 */
	public FileSystem getConfiguredFileSystem() {
		if (configuredFileSystem == null) {
			try {
				Configuration configuration = prepareConfiguration();
				if (isAuthEnable) {
					configuration = initSecurityConfiguration(configuration);
					loginUser(readProperty().getProperty("userName") + "@" + readProperty().getProperty("kdcDomain"), readProperty().getProperty("userPass"));
					configuredFileSystem = FileSystem.get(configuration);
				} else {
					configuredFileSystem = getDefaultConfiguredFileSystem(configuration);
					return configuredFileSystem;
				}

			} catch (IOException e) {
				/*throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
						HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);*/
			}
		}
		return configuredFileSystem;
	}

	/**
	 * Prepares hadoop configuration object with required properties
	 * 
	 * @return hadoop configuration
	 * @throws IOException
	 */
	private Configuration initSecurityConfiguration(Configuration configuration) throws IOException {
		configuration.set("dfs.data.transfer.protection", "authentication");
		configuration.set("hadoop.security.authentication", "kerberos");
		InputStream krbStream = getClass().getClassLoader().getResourceAsStream("krb5.conf");
		Path krbPath = Paths.get(hadoopLibPath.toString(), "krb5.conf");
		Files.copy(krbStream, krbPath);
		System.setProperty("java.security.krb5.conf", krbPath.toString());
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
			/*throw new FSAdapterException(HDFSAdapterErrorCode.URI_SYNTAX_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.URI_SYNTAX_EXCEPTION.getErrorMessage(), e);*/
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
			/*throw new FSAdapterException(HDFSAdapterErrorCode.LOGIN_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.LOGIN_EXCEPTION.getErrorMessage(), e);*/
		} catch (NoSuchAlgorithmException e) {
			/*throw new FSAdapterException(HDFSAdapterErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage(), e);*/
		}
	}

	/**
	 * Function to get instance of the hadoop filesystem with its configuration set
	 * 
	 * @return configured filesystem
	 */
	private FileSystem getDefaultConfiguredFileSystem(Configuration configuration) {
		if (configuredFileSystem == null) {
			try {
				configuredFileSystem = UserGroupInformation.createRemoteUser(userName, AuthMethod.TOKEN)
						.doAs(new PrivilegedExceptionAction<FileSystem>() {
							public FileSystem run() throws IOException {
								return FileSystem.get(configuration);
							}
						});
			} catch (IOException e) {
				/*throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
						HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);*/
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				/*throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
						HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);*/
			}
		}
		return configuredFileSystem;
	}

	private Configuration prepareConfiguration() {
		Configuration configuration = null;
		try {
			configuration = new Configuration();
			configuration.set("fs.defaultFS", readProperty().getProperty("nameNodeUrl"));
			configuration.set("dfs.client.use.datanode.hostname", "true");
			configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
			//configuration.set("fs.file.impl", LocalFileSystem.class.getName());
			hadoopLibPath = Files.createTempDirectory(HADOOP_HOME);
			if (SystemUtils.IS_OS_WINDOWS) {
				Path binPath = Files.createDirectory(Paths.get(hadoopLibPath.toString(), "bin"));
				InputStream winUtilsStream = getClass().getClassLoader().getResourceAsStream(WIN_UTIL);
				Path winUtilsPath = Paths.get(binPath.toString(), WIN_UTIL);
				Files.copy(winUtilsStream, winUtilsPath);
				System.setProperty("hadoop.home.dir", hadoopLibPath.toString());
			}
		} catch (IOException e) {
			/*throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);*/
		}
		return configuration;
	}
	
	private Properties readProperty(){
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		String propertyFilePath=apiRequests.getResourcePath()+"config/registrationProcessorAPI.properties";
		Properties p = new Properties();
		try {
			p.load(new FileReader(new File(propertyFilePath)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return p;
	}
}