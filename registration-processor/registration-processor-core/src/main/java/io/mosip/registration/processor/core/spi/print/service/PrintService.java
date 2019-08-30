package io.mosip.registration.processor.core.spi.print.service;

import io.mosip.registration.processor.core.constant.IdType;

/**
 * The Interface PrintService.
 * 
 * @author M1048358 Alok
 *
 * @param <T>
 *            the generic type
 */
public interface PrintService<T> {

	/**
	 * Gets the documents.
	 *
	 * @param type
	 *            the type
	 * @param idValue
	 *            the id value
	 * @param cardType
	 *            the card type
	 * @param isPasswordProtected
	 *            the is password protected
	 * @return the documents
	 */
	public T getDocuments(IdType type, String idValue, String cardType, boolean isPasswordProtected);

}
