package io.mosip.registration.processor.status.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * 
 * @author Girish Yarru
 *
 */
// Common Entity where RegistrationStatusEntity,Transaction Enity and
// SyncRegistrationEntity extends this. This is created to implement common
// repository(RegistrationRepository)

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseRegistrationEntity {

	public BaseRegistrationEntity() {
		super();
	}

	/** The id. */
	@Column(name = "id", nullable = false)
	@Id
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String baseId) {
		this.id = baseId;
	}

}
