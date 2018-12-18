package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;

/**
 * Gender entity mapped according to DB
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "gender", schema = "reg")
public class GenderType extends RegistrationCommonFields implements Serializable {
	private static final long serialVersionUID = 1323022736883315822L;

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "genderCode", column = @Column(name = "code")),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code")) })

	@Column(name = "code")
	private GenderTypeId id;

	@Column(name = "name")
	private String genderName;

	/**
	 * @return the id
	 */
	public GenderTypeId getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(GenderTypeId id) {
		this.id = id;
	}

	/**
	 * @return the genderName
	 */
	public String getGenderName() {
		return genderName;
	}

	/**
	 * @param genderName the genderName to set
	 */
	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	
	
}
