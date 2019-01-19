package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class RegistrationCommonFields {

	@Column(name = "IS_ACTIVE")
	protected Boolean isActive;
	@Column(name = "CR_BY")
	protected String crBy;
	@Column(name = "CR_DTIMES")
	protected Timestamp crDtime;
	@Column(name = "UPD_BY")
	protected String updBy;
	@Column(name = "UPD_DTIMES")
	protected Timestamp updDtimes;

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the crBy
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * @param crBy
	 *            the crBy to set
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * @return the crDtime
	 */
	public Timestamp getCrDtime() {
		return crDtime;
	}

	/**
	 * @param crDtime
	 *            the crDtime to set
	 */
	public void setCrDtime(Timestamp crDtime) {
		this.crDtime = crDtime;
	}

	/**
	 * @return the updBy
	 */
	public String getUpdBy() {
		return updBy;
	}

	/**
	 * @param updBy
	 *            the updBy to set
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * @return the updDtimes
	 */
	public Timestamp getUpdDtimes() {
		return updDtimes;
	}

	/**
	 * @param updDtimes
	 *            the updDtimes to set
	 */
	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}

}
