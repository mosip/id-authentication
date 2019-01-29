package io.mosip.registration.entity.mastersync.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Entity for composite primary key in gender table in DB
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Embeddable
public class GenderID implements Serializable {

	private static final long serialVersionUID = -1169678225048676557L;

	@Column(name = "code")
	private String genderCode;

	@Column(name = "name")
	private String genderName;

	/**
	 * @return the genderCode
	 */
	public String getGenderCode() {
		return genderCode;
	}

	/**
	 * @param genderCode the genderCode to set
	 */
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
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
