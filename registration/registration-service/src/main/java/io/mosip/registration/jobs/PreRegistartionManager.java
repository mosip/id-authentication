package io.mosip.registration.jobs;

import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.entity.SyncTransaction;

/**
 * PreRegistartionManager  is a manager
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface PreRegistartionManager {
	
	/**
	 * save pre-reg transaction
	 * @param preRegistration is a entity
	 * @return saved transaction entity
	 */
	public SyncTransaction saveTransaction(PreRegistrationList preRegistration);

}
