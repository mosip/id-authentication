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

	public TransactionDto getTransactionByRegIdAndStatusCode(String regId, String statusCode);

}
