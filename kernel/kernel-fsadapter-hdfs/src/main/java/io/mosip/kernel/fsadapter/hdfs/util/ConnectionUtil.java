package io.mosip.kernel.fsadapter.hdfs.util;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
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
	 * Field for username
	 */
	@Value("${mosip.kernel.fsadapter.hdfs.user-name}")
	private String userName;

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
	public synchronized FileSystem getConfiguredFileSystem() {
		if (configuredFileSystem == null) {
			try {
				configuredFileSystem = UserGroupInformation.createRemoteUser(userName, AuthMethod.TOKEN)
						.doAs(new PrivilegedExceptionAction<FileSystem>() {
							public FileSystem run() throws IOException {
								Configuration configuration = new Configuration();
								configuration.set("fs.defaultFS", nameNodeUrl);
								configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
								configuration.set("fs.file.impl", LocalFileSystem.class.getName());
								configuration.set("dfs.client.use.datanode.hostname", "true");
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
		}
		return configuredFileSystem;
	}
}