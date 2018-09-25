package org.mosip.registration.processor.filesystem.ceph.adapter.impl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author Pranav Kumar
 * 
 * @since 0.0.1
 *
 */
public class ConnectionUtil {

	private static final String CONFIG_FILE_NAME = "config.properties";
	private static AmazonS3 connection = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemCephAdapterImpl.class);

	private ConnectionUtil() {

	}

	private static void initializeConnection() {
		Properties properties = new Properties();
		InputStream inputStream;
		inputStream = ConnectionUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			LOGGER.error("Unable to load config file");
		}
		String accessKey = properties.getProperty("access-key");
		String secretKey = properties.getProperty("secret-key");
		String endpoint = properties.getProperty("endpoint");
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		ConnectionUtil.connection = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withEndpointConfiguration(new EndpointConfiguration(endpoint, Regions.AP_SOUTH_1.toString())).build();
	}

	public static AmazonS3 getConnection() {
		if (connection == null) {
			initializeConnection();
		}
		return connection;
	}

}
