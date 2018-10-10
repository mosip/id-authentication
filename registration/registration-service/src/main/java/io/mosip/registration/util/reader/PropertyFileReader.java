package io.mosip.registration.util.reader;

import java.io.IOException;
import java.util.Properties;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.constants.RegConstants;

/**
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class PropertyFileReader {

	/**
	 * Instance of {@link Properties} class
	 */
	private static Properties properties;

	// Loads the property file
	static {
		properties = new Properties();
		try {
//			properties.put("AES_KEY_MANAGER_ALG", "AES");
//			properties.put("AES_CIPHER_ALG", "AES/CBC/PKCS5Padding");
//			properties.put("AES_KEY_SEED_LENGTH", "32");
//			properties.put("AES_SESSION_KEY_LENGTH", "256");
//			properties.put("AES_KEY_CIPHER_SPLITTER", "#KEY_SPLITTER#");
//			properties.put("USER_NAME", "Balaji");
//			properties.put("HASHING_ALG", "SHA-256");
//			properties.put("RSA_ALG", "RSA");
//			properties.put("USER_NAME", "Balaji");
//			properties.put("PACKET_STORE_LOCATION", "D:\\Registration Store");
//			properties.put("RSA_PUBLIC_KEY_FILE", "D:\\Key Store\\public.key");
//			properties.put("RSA_PRIVATE_KEY_FILE", "D:\\Key Store\\private.key");
//			properties.put("PACKET_STORE_DATE_FORMAT", "dd-MMM-yyyy");
//			properties.put("PACKET_UNZIP_LOCATION", "D:\\Uncompressed");
//			properties.put("PACKET_ZIP_FILE_NAME", "EncryptedData");
//			properties.put("ENROLLMENT_META_DATA_FILE_NAME", "MetaInfo");
//			properties.put("TIME_STAMP_FORMAT", "yyyyMMdd");
			properties.load(ClassLoader.getSystemResourceAsStream(RegConstants.CONSTANTS_FILE_NAME));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new BaseUncheckedException();
		}
	}

	/**
	 * Gets the value of the property
	 * 
	 * @param propertyKey
	 *            the key for which value has to be returned
	 * @return String value of the property
	 */
	public static String getPropertyValue(String propertyKey) {
		return properties.getProperty(propertyKey);
	}

}
