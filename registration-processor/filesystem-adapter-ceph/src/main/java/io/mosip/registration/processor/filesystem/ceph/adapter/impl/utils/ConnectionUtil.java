package io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
	

/**
 * This class gets connection to CEPH storage cluster.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
@RefreshScope
@Component
public class ConnectionUtil {

	/** The connection. */
	private AmazonS3 connection;

	/** The access key. */
	@Value("${registration.processor.access-key}")
	private String accessKey;

	/** The secret key. */
	@Value("${registration.processor.secret-key}")
	private String secretKey;

	/** The endpoint. */
	@Value("${registration.processor.endpoint}")
	private String endpoint;

	/**
	 * Instantiates a new connection util.
	 */
	public ConnectionUtil() {

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

}
