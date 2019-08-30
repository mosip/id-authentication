package io.mosip.registration.processor.core.spi.print.service;

import io.mosip.registration.processor.core.constant.IdType;

// TODO: Auto-generated Javadoc
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
	 * Gets the pdf.
	 *
	 * @param type
	 *            the type
	 * @param idValue
	 *            the id value
	 * @param cardType
	 *            the card type
	 * @return the pdf
	 */
	public T getDocuments(IdType type, String idValue, String cardType);

	/**
	 * Gets the password protected doc.
	 *
	 * @param document
	 *            the document
	 * @param idType
	 *            the id type
	 * @param idValue
	 *            the id value
	 * @return the password protected doc
	 */
	public byte[] getPasswordProtectedDoc(byte[] document, String idType, String idValue);

}
