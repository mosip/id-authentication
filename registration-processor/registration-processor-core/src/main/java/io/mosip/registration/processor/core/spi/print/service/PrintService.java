package io.mosip.registration.processor.core.spi.print.service;

import io.mosip.registration.processor.core.constant.IdType;

/**
 * The Interface PrintService.
 * 
 * @author M1048358 Alok
 *
 * @param <T> the generic type
 */
public interface PrintService<T> {
	
	/**
	 * Gets the pdf.
	 *
	 * @param regId the reg id
	 * @return the pdf
	 */
	public T getDocuments(IdType type, String idValue);

}
