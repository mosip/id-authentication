package io.mosip.registration.processor.core.util;

/**
 * The Class MessageBusUtil.
 * 
 * @author M1048358 Alok
 */
public class MessageBusUtil {
	
	private MessageBusUtil() {
	}

	/**
	 * Gets the message bus adress.
	 *
	 * @param stageName the stage name
	 * @return the message bus adress
	 */
	public static String getMessageBusAdress(String stageName) {
		String messageAddress = stageName.substring(0, stageName.length() - 5);
		for (int count = 1; count < messageAddress.length(); count++) {
			if (Character.isUpperCase(messageAddress.charAt(count))) {
				messageAddress = messageAddress.substring(0, count) + "-" + messageAddress.substring(count);
				count = count + 1;
			}
		}

		return messageAddress.toLowerCase();
	}
}
