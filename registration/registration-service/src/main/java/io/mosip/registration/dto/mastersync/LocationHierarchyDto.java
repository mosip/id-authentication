package io.mosip.registration.dto.mastersync;

import java.io.Serializable;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */

public class LocationHierarchyDto extends MasterSyncBaseDto implements Serializable {

	
	private static final long serialVersionUID = 4552110961570300174L;

	private Short locationHierarchylevel;

	private String locationHierarchyName;

	private Boolean isActive;

	/**
	 * @return the locationHierarchylevel
	 */
	public Short getLocationHierarchylevel() {
		return locationHierarchylevel;
	}

	/**
	 * @param locationHierarchylevel the locationHierarchylevel to set
	 */
	public void setLocationHierarchylevel(Short locationHierarchylevel) {
		this.locationHierarchylevel = locationHierarchylevel;
	}

	/**
	 * @return the locationHierarchyName
	 */
	public String getLocationHierarchyName() {
		return locationHierarchyName;
	}

	/**
	 * @param locationHierarchyName the locationHierarchyName to set
	 */
	public void setLocationHierarchyName(String locationHierarchyName) {
		this.locationHierarchyName = locationHierarchyName;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	

}
