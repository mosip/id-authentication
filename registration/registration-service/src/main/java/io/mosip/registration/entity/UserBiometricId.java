package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite key for UserBiometric entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
public class UserBiometricId implements Serializable {

	private static final long serialVersionUID = 4356301394048825993L;

	@Column(name = "usr_id")
	private String usrId;
	@Column(name = "bmtyp_code")
	private String bioTypeCode;
	@Column(name = "bmatt_code")
	private String bioAttributeCode;

	/**
	 * @return the usrId
	 */
	public String getUsrId() {
		return usrId;
	}

	/**
	 * @param usrId
	 *            the usrId to set
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	/**
	 * @return the bioTypeCode
	 */
	public String getBioTypeCode() {
		return bioTypeCode;
	}

	/**
	 * @param bioTypeCode
	 *            the bioTypeCode to set
	 */
	public void setBioTypeCode(String bioTypeCode) {
		this.bioTypeCode = bioTypeCode;
	}

	/**
	 * @return the bioAttributeCode
	 */
	public String getBioAttributeCode() {
		return bioAttributeCode;
	}

	/**
	 * @param bioAttributeCode
	 *            the bioAttributeCode to set
	 */
	public void setBioAttributeCode(String bioAttributeCode) {
		this.bioAttributeCode = bioAttributeCode;
	}

}
