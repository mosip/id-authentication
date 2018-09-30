package org.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

@MappedSuperclass
public class RegistrationCommonFields {
	
	@Column(name="IS_ACTIVE", nullable=true, updatable=true)
	@Type(type= "true_false")
	protected Boolean isActive;
	@Column(name="CR_BY", length=32, nullable=false, updatable=true)
	protected String crBy;
	@Column(name="CR_DTIMES", nullable=false, updatable=true)
	protected OffsetDateTime crDtime;
	@Column(name="UPD_BY", length=32, nullable=true, updatable=true)
	protected String updBy;
	@Column(name="UPD_DTIMES", nullable=true, updatable=false)
	protected OffsetDateTime updDtimes;
	
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public String getCrBy() {
		return crBy;
	}
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}
	public OffsetDateTime getCrDtime() {
		return crDtime;
	}
	public void setCrDtime(OffsetDateTime crDtime) {
		this.crDtime = crDtime;
	}
	public String getUpdBy() {
		return updBy;
	}
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}
	public OffsetDateTime getUpdDtimes() {
		return updDtimes;
	}
	public void setUpdDtimes(OffsetDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}
	
	
}
