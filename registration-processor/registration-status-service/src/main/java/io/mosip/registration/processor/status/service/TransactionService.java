package io.mosip.registration.processor.status.service;

import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.TransactionEntity;

/**
 * This service is used to perform crud operations(get/addd/update) on
 * transaction table.
 *
 * @param <U>
 *            the generic type
 */

public interface TransactionService<U> {

	/**
	 * Adds the registration transaction.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 * @return the transaction entity
	 */
	public TransactionEntity addRegistrationTransaction(U registrationStatusDto);

	/**
	 * Gets the transaction by reg id and status code.
	 *
	 * @param regId
	 *            the reg id
	 * @param statusCode
	 *            the status code
	 * @return the transaction by reg id and status code
	 */
	public TransactionDto getTransactionByRegIdAndStatusCode(String regId, String statusCode);

}
