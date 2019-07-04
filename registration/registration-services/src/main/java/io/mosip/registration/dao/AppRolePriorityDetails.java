package io.mosip.registration.dao;

/**
 * This class is used to fetch only selected (role code and priority) columns
 * from the {@link io.mosip.registration.entity.AppRolePriority} table.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface AppRolePriorityDetails {
	
	/**
	 * This method is used to fetch only selected (role code and priority) columns
	 * from the {@link io.mosip.registration.entity.AppRolePriority} table as given in Embeddable.
	 *  
	 * @return {@link io.mosip.registration.entity.AppRolePriority}
	 */
	AppRolePriority getAppRolePriorityId();
	
	/**
	 * This class defines the columns which are needed to be selected and fetched
	 * from the {@link io.mosip.registration.entity.AppRolePriority}.
	   */
	interface AppRolePriority{
		String getRoleCode();
		String getPriority();
	}

}
