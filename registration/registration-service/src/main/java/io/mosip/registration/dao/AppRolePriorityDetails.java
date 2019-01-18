package io.mosip.registration.dao;

public interface AppRolePriorityDetails {
	
	/**
	 * To fetch only selected columns from Embeddable
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
