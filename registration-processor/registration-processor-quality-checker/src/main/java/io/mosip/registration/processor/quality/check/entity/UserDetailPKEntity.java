package io.mosip.registration.processor.quality.check.entity;
	
import java.io.Serializable;

import javax.persistence.Embeddable;
/**
 * The Class UserDetailPKEntity.
 */
@Embeddable
public class UserDetailPKEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	private String id;

	/**
	 * Instantiates a new user detail PK entity.
	 */
	public UserDetailPKEntity() {
		super();
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
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
}
