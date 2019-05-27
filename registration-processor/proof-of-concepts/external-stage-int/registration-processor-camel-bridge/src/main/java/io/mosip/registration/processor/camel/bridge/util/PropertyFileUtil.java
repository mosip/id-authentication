package io.mosip.registration.processor.camel.bridge.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;

/**
 * The Class PropertyFileUtil.
 *
 * @author Mukul Puspam
 */
public class PropertyFileUtil {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PropertyFileUtil.class);


	/**
	 * Instantiates a new property file util.
	 */
	private PropertyFileUtil() {

	}

	/**
	 * Gets the property.
	 *
	 * @param clazz the clazz
	 * @param fileName the file name
	 * @param key the key
	 * @return the property
	 */
	public static String getProperty(Class<?> clazz, String fileName, String key) {

		Properties prop = new Properties();
		String value = null;

		try (InputStream input = clazz.getClassLoader().getResourceAsStream(fileName)) {

			if (input == null) {
				throw new FileNotFoundException("File Not available " + fileName);
			}
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"Failed to read properties : ",ex.getCause().toString());

		}
		return value;
	}
}
