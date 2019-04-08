package io.mosip.kernel.fsadapter.hdfs.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.PrivilegedExceptionAction;

import org.apache.commons.lang3.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.SaslRpcServer.AuthMethod;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.fsadapter.hdfs.constant.HDFSAdapterErrorCode;

/**
 * This class gets connection to DFS.
 *
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Component
public class ConnectionUtils {

	/**
	 * Field for name-node url
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.name-node-url}")
	private String nameNodeUrl;

	/**
	 * Field for kdc domain, default is 'NOTSET'
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.kdc-domain:NOTSET}")
	private String kdcDomain;

	/**
	 * Field for username
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.user-name}")
	private String userName;

	/**
	 * Field for verify Authentication is enable, default is false.
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.authentication-enabled:false}")
	private boolean isAuthEnable;

	/**
	 * Field for keytab,default value is 'NOTSET'
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.keytab-file:NOTSET}")
	private String keytabPath;

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
					configuredFileSystem = FileSystem.get(configuration);
				} else {
					configuredFileSystem = getDefaultConfiguredFileSystem(configuration);
				}

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
		String user = userName + "@" + kdcDomain;
		loginWithKeyTab(user, keytabPath);
		return configuration;
	}

	/**
	 * Function to get instance of the hadoop filesystem with its configuration set
	 * 
	 * @return configured filesystem
	 */
	private FileSystem getDefaultConfiguredFileSystem(Configuration configuration) {
		try {
			configuredFileSystem = UserGroupInformation.createRemoteUser(userName, AuthMethod.TOKEN)
					.doAs(new PrivilegedExceptionAction<FileSystem>() {
						public FileSystem run() throws IOException {
							return FileSystem.get(configuration);
						}
					});
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
		return configuredFileSystem;
	}

	/**
	 * preparing default hdfs configuration
	 * 
	 * @return {@link Configuration}
	 */
	private Configuration prepareConfiguration() {
		Configuration configuration = null;
		try {
			configuration = new Configuration();
			configuration.set("fs.defaultFS", nameNodeUrl);
			configuration.set("dfs.client.use.datanode.hostname", "true");
			configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
			hadoopLibPath = Files.createTempDirectory(HADOOP_HOME);
			System.setProperty("hadoop.home.dir", hadoopLibPath.toString());
			if (SystemUtils.IS_OS_WINDOWS) {
				Path binPath = Files.createDirectory(Paths.get(hadoopLibPath.toString(), "bin"));
				InputStream winUtilsStream = getClass().getClassLoader().getResourceAsStream(WIN_UTIL);
				Path winUtilsPath = Paths.get(binPath.toString(), WIN_UTIL);
				Files.copy(winUtilsStream, winUtilsPath);
			}
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.HDFS_ADAPTER_EXCEPTION.getErrorMessage(), e);
		}
		return configuration;
	}

	/**
	 * Login User with keytab
	 * 
	 * @param user
	 *            username with the kdc, eg. test@kdc.example.com
	 * @param keytabPath
	 *            path of the keytab file
	 * @throws IOException
	 */
	private void loginWithKeyTab(String user, String keytabPath) throws IOException {
		InputStream keytabInputStream = this.getClass().getClassLoader().getResourceAsStream(keytabPath);
		Path dataPath = Files.createDirectory(Paths.get(hadoopLibPath.toString(), "data"));
		Path keyPath = null;
		if (keytabInputStream != null) {
			keyPath = Paths.get(dataPath.toString(), keytabPath);
			Files.copy(keytabInputStream, keyPath, StandardCopyOption.REPLACE_EXISTING);
		} else {
			throw new FSAdapterException(HDFSAdapterErrorCode.KEYTAB_FILE_NOT_FOUND_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.KEYTAB_FILE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		try {
			UserGroupInformation.loginUserFromKeytab(user, keyPath.toString());
		} catch (IOException e) {
			throw new FSAdapterException(HDFSAdapterErrorCode.LOGIN_EXCEPTION.getErrorCode(),
					HDFSAdapterErrorCode.LOGIN_EXCEPTION.getErrorMessage(), e);
		}
	}
}