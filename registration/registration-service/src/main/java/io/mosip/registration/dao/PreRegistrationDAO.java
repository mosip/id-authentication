package io.mosip.registration.dao;

import io.mosip.registration.entity.PreRegistration;

/**
 * pre registartion DAO
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDAO {

	/**
	 * To get pre registration
	 * @param preRegId is a id 
	 * @return pre registartion entity
	 */
	public PreRegistration getPreRegistration(String preRegId);
	
	
	/**
	 * To save new Pre registration
	 * @param preRegistration is a entity
	 * @return saved pre registartion
	 */
	public PreRegistration savePreRegistration(PreRegistration preRegistration);
}
