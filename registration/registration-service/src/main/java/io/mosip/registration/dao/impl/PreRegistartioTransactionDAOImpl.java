package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.dao.PreRegistartionTransactionDAO;
import io.mosip.registration.entity.PreregistrationTransaction;
import io.mosip.registration.repositories.PreRegistrationTransactionRepository;

/**
 * {@link PreRegistartionTransactionDAO}
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class PreRegistartioTransactionDAOImpl implements PreRegistartionTransactionDAO {
	
	/**
	 * autowires Pre Registration Transaction Repository class
	 */
	@Autowired
	PreRegistrationTransactionRepository preRegistrationTransactionRepository;

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.PreRegistartionTransactionDAO#savePreRegistrationTransaction(io.mosip.registration.entity.PreregistrationTransaction)
	 */
	@Override
	public PreregistrationTransaction savePreRegistrationTransaction(
			PreregistrationTransaction preregistrationTransaction) {
		return preRegistrationTransactionRepository.save(preregistrationTransaction);
	}

}
