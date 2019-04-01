package io.mosip.registration.dao;

/**
 * Interface for AppRolePriority
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface AppRolePriorityDetails {
	
	/**
	 * To fetch only selected columns from Embeddable
	 * 
	 * @return {@link AppRolePriority}
	 */
	AppRolePriority getAppRolePriorityId();
	
	/**
	 * To fetch only selected columns from table
	 */
	interface AppRolePriority{
		String getRoleCode();
		String getPriority();
	}

}
