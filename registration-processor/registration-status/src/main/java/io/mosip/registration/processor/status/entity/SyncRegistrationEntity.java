/**
 * 
 */
package io.mosip.registration.processor.status.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

/**
 * The Class SyncRegistrationEntity.
 *
 * @author M1047487
 */
@Component
@Entity
@Table(name = "registrationSync", schema = "regprc")
public class SyncRegistrationEntity {

	/** The registration id. */
	@Column(name = "id", nullable = false)
	@Id
	private String registrationSyncId;

	/** The registration type. */
	@Column(name = "parent_id", nullable = false)
	private String parentId;

	/**
	 * Instantiates a new sync registration entity.
	 */
	public SyncRegistrationEntity() {
		super();
	}

	/**
	 * Gets the registration sync id.
	 *
	 * @return the registrationSyncId
	 */
	public String getRegistrationSyncId() {
		return registrationSyncId;
	}

	/**
	 * Sets the registration sync id.
	 *
	 * @param registrationSyncId
	 *            the registrationSyncId to set
	 */
	public void setRegistrationSyncId(String registrationSyncId) {
		this.registrationSyncId = registrationSyncId;
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
