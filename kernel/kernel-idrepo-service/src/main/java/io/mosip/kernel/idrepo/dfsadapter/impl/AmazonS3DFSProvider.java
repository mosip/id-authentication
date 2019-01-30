package io.mosip.kernel.idrepo.dfsadapter.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.amazonaws.SdkBaseException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.spi.MosipDFSProvider;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.idrepo.config.IdRepoLogger;

/**
 * This class gets connection to CEPH storage cluster.
 *
 * @author Manoj SP
 */
@RefreshScope
@Component("amazonS3")
public class AmazonS3DFSProvider implements MosipDFSProvider {

	/** The Constant AMAZONS3_DFS_ADAPTER. */
	private static final String AMAZONS3_DFS_ADAPTER = "AmazonS3DFSAdapter";

	/** The Constant SUCCESS_UPLOAD_MESSAGE. */
	private static final String SUCCESS_UPLOAD_MESSAGE = "Successfully uploaded to DFS";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(AmazonS3DFSProvider.class);

	/** The connection. */
	private AmazonS3 connection;

	/** The access key. */
	@Value("${mosip.kernel.idrepo.dfs.access-key}")
	private String accessKey;

	/** The secret key. */
	@Value("${mosip.kernel.idrepo.dfs.secret-key}")
	private String secretKey;

	/** The endpoint. */
	@Value("${mosip.kernel.idrepo.dfs.endpoint}")
	private String endpoint;

	/**
	 * Instantiates a new connection util.
	 */
	public AmazonS3DFSProvider() {

	}

	/**
	 * Initialize connection.
	 */
	private void initializeConnection() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		connection = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withEndpointConfiguration(new EndpointConfiguration(endpoint, Regions.AP_SOUTH_1.toString())).build();
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public AmazonS3 getConnection() {
		if (connection == null) {
			initializeConnection();
		}
		return connection;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.idrepo.spi.MosipDFSProvider#storeFile(java.lang.String, java.lang.String, byte[])
	 */
	public boolean storeFile(String uin, String filePathAndName, byte[] fileData) throws IdRepoAppException {
		try {
			mosipLogger.debug(AMAZONS3_DFS_ADAPTER, uin, filePathAndName,
					"bucket exists with uin: " + uin + " -- " + getConnection().doesBucketExistV2(uin));
			if (!getConnection().doesBucketExistV2(uin)) {
				getConnection().createBucket(uin);
				mosipLogger.debug(AMAZONS3_DFS_ADAPTER, uin, filePathAndName, "bucket created with uin : " + uin);
			}
			mosipLogger.debug(AMAZONS3_DFS_ADAPTER, uin, filePathAndName, "before storing file");
			getConnection().putObject(uin, filePathAndName, new ByteArrayInputStream(fileData), null);
			mosipLogger.debug(AMAZONS3_DFS_ADAPTER, uin, filePathAndName, SUCCESS_UPLOAD_MESSAGE);
		} catch (SdkClientException e) {
			mosipLogger.error(AMAZONS3_DFS_ADAPTER, AMAZONS3_DFS_ADAPTER, "storeFile",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR, e);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.idrepo.spi.MosipDFSProvider#getFile(java.lang.String, java.lang.String)
	 */
	public byte[] getFile(String uin, String filePathAndName) throws IdRepoAppException {
		try {
			if (getConnection().doesBucketExistV2(uin) && getConnection().doesObjectExist(uin, filePathAndName)) {
				return IOUtils.toByteArray((InputStream) getConnection()
						.getObject(new GetObjectRequest(uin, filePathAndName)).getObjectContent());

			}
		} catch (SdkBaseException | IOException e) {
			mosipLogger.error(AMAZONS3_DFS_ADAPTER, AMAZONS3_DFS_ADAPTER, "getFile",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR, e);
		}
		return null;
	}

}
