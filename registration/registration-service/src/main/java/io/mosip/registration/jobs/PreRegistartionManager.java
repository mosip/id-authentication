package io.mosip.registration.manager;

import io.mosip.registration.entity.PreRegistration;
import io.mosip.registration.entity.PreregistrationTransaction;

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
	public PreregistrationTransaction saveTransaction(PreRegistration preRegistration);

}
