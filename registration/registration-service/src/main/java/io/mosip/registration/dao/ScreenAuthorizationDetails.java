package io.mosip.registration.dao;

/**
 * Interface for RegistrationScreenAuthorization
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface ScreenAuthorizationDetails {
	
	/**
	 * To fetch only selected columns from Embeddable
	 */
	ScreenAuthorization getScreenAuthorizationId();
	
	/**
	 * To fetch only selected columns from table
	 */
	interface ScreenAuthorization{
		String getScreenId();
		String getRoleCode();
	}
	

}
