package io.mosip.preregistration.datasync.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jagadishwari
 * @since 1.0.0
 */
public class ReverseDataSyncRequestDTO implements Serializable {

	/**
	 * auto generated serialVersionUID
	 */
	private static final long serialVersionUID = -2626266155604751029L;

	/**
	 * List to store the list of pre Ids
	 */
	private List<String> preRegistrationIds;

	/**
	 * @return preRegistrationIds
	 */
	public List<String> getPreRegistrationIds() {
		return preRegistrationIds;
	}

	/**
	 * @param preRegistrationIds
	 */
	public void setPreRegistrationIds(List<String> preRegistrationIds) {
		this.preRegistrationIds = preRegistrationIds;
	}

}
