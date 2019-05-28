package io.mosip.dbentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseRegistrationEntity {

	/**
	 * Instantiates a new base registration entity.
	 */
	public BaseRegistrationEntity() {
		super();
	}

	/** The id. */
	@Column(name = "id", nullable = false)
	@Id
	protected String id;

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
	 * @param baseId the new id
	 */
	public void setId(String baseId) {
		this.id = baseId;
	}
}
