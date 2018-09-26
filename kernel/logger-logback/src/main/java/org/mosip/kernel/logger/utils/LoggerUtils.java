package org.mosip.kernel.logger.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.mosip.kernel.logger.constants.LogExeptionCodeConstants;
import org.mosip.kernel.logger.exception.MosipXMLConfigurationParseException;

/**
 * This is utility class for Logger
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class LoggerUtils {
	/**
	 * 
	 */
	private LoggerUtils() {
	}

	/**
	 * @param file
	 * @param clazz
	 * @return
	 */
	public static Object unmarshell(File file, Class<?> clazz) {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return unmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new MosipXMLConfigurationParseException(LogExeptionCodeConstants.MOSIPCONFIGURATIONXMLPARSE,
					LogExeptionCodeConstants.MOSIPCONFIGURATIONXMLPARSEMESSAGE);
		}

	}
}
