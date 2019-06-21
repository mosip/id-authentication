package io.mosip.registration.dao;

import io.mosip.registration.entity.AppAuthenticationMethod;

/**
 * This class is used to fetch only selected (process names and auth method code) columns
 * from the {@link AppAuthenticationMethod} table.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface AppAuthenticationDetails {
	
	/**
	 * This method is used to fetch only selected (process names and auth method code) columns
	 * from the {@link AppAuthenticationMethod} table as given in Embeddable.
	 *
	 * @return {@link AppAuthentication}
	 */
	AppAuthentication getAppAuthenticationMethodId();
	
	/**
	 * This class defines the columns which are needed to be selected and fetched
	 * from the {@link AppAuthenticationMethod}.
	 */
	interface AppAuthentication{
		String getProcessName();
		String getAuthMethodCode();
	}

}
