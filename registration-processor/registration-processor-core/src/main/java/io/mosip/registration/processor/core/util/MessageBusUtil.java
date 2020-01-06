package io.mosip.registration.processor.core.util;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;

/**
 * The Class MessageBusUtil.
 * 
 * @author M1048358 Alok
 */
public class MessageBusUtil {
	
	private MessageBusUtil() {
	}
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MessageBusUtil.class);

	/**
	 * Gets the message bus adress.
	 *
	 * @param stageName the stage name
	 * @return the message bus adress
	 */
	public static String getMessageBusAdress(String stageName) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"MessageBusUtil::getMessageBusAdress()::entry stageName "+stageName);

		String messageAddress = stageName.substring(0, stageName.length() - 5);
		for (int count = 1; count < messageAddress.length(); count++) {
			if (Character.isUpperCase(messageAddress.charAt(count))) {
				messageAddress = messageAddress.substring(0, count) + "-" + messageAddress.substring(count);
				count = count + 1;
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"MessageBusUtil::getMessageBusAdress()::exit stageName "+stageName);

		return messageAddress.toLowerCase();
	}
}
