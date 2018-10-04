package io.mosip.registration.processor.status.service;

import io.mosip.registration.processor.status.entity.TransactionEntity;

/**
 * This service is used to perform crud operations(get/addd/update) on
 * transaction table.
 *
 */

public interface TransactionService<U> {

	public TransactionEntity addRegistrationTransaction(U registrationStatusDto);

}
