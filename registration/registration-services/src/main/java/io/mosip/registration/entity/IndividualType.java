package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.IndividualTypeId;

/**
 * The Entity Class for IndividualType.
 *
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Entity
@Table(name = "individual_type", schema = "reg")
public class IndividualType extends RegistrationCommonFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9192768324920614543L;

	@EmbeddedId
	private IndividualTypeId individualTypeId;

	/**
	 * @return the individualTypeId
	 */
	public IndividualTypeId getIndividualTypeId() {
		return individualTypeId;
	}

	/**
	 * @param individualTypeId the individualTypeId to set
	 */
	public void setIndividualTypeId(IndividualTypeId individualTypeId) {
		this.individualTypeId = individualTypeId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "name")
	private String name;

}
