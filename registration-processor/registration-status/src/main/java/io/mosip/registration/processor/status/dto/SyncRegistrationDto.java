/**
 * 
 */
package io.mosip.registration.processor.status.dto;

import java.io.Serializable;

import io.mosip.registration.processor.status.code.SyncStatus;
import io.mosip.registration.processor.status.code.SyncType;

/**
 * The Class SyncRegistrationDto.
 *
 * @author M1047487
 */
public class SyncRegistrationDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3922338139042373367L;

	/** The id. */
	private String id;

	/** The parent id. */
	private String parentId;

	/**
	 * Instantiates a new sync registration dto.
	 *
	 * @param id
	 *            the id
	 * @param parentId
	 *            the parent id
	 * @param syncType
	 *            the sync type
	 * @param synchStatus
	 *            the synch status
	 */
	public SyncRegistrationDto(String id, String parentId, SyncType syncType, SyncStatus synchStatus) {

		this.id = id;
		this.parentId = parentId;

	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the parent id.
	 *
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * Sets the parent id.
	 *
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
