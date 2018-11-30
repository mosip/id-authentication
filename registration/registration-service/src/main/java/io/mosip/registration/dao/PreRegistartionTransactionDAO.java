package io.mosip.registration.dao;

import io.mosip.registration.entity.PreregistrationTransaction;

/**
 * pre registartion transaction DAO
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistartionTransactionDAO {

	/**
	 * To save a new pre-reg transaction
	 * @param preregistrationTransaction is an entity
	 * @return saved transaction entity
	 */
	public PreregistrationTransaction savePreRegistrationTransaction(PreregistrationTransaction preregistrationTransaction);
}
