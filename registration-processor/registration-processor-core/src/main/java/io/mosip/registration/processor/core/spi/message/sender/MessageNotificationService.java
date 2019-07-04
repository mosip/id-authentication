package io.mosip.registration.processor.core.spi.message.sender;

import java.io.IOException;
import java.util.Map;

import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;

/**
 * The Interface MessageNotificationService
 * 
 * @author Alok Ranjan
 *
 * @param <T>
 * @param <U>
 * @param <V>
 */
public interface MessageNotificationService<T, U, V> {

	/**
	 * Method for sending sms notification
	 * @param templateTypeCode
	 * @param id
	 * @param idType
	 * @param attributes
	 * @param regType 
	 * @return
	 * @throws ApisResourceAccessException
	 * @throws IOException
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	public T sendSmsNotification(String templateTypeCode, String id, IdType idType,
			Map<String, Object> attributes, String regType) throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException;
	
	
	/**
	 * Method to send email notification
	 * @param templateTypeCode
	 * @param id
	 * @param idType
	 * @param attributes
	 * @param mailCc
	 * @param subject
	 * @param attachment
	 * @param regType 
	 * @return
	 * @throws IOException
	 * @throws ApisResourceAccessException
	 */
	public U sendEmailNotification(String templateTypeCode, String id,
			IdType idType, Map<String, Object> attributes, String[] mailCc, String subject,
			V attachment, String regType) throws IOException, ApisResourceAccessException,Exception;
	
}
