package org.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * RegistrationCenter entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema="reg", name = "center")
public class RegistrationCenter extends RegistrationCommonFields {
	@Id
	@Column(name="id", length=64, nullable=false, updatable=false)
	private String centerId;
	@Column(name="name", length=64, nullable=true, updatable=false)
	private String centerName;
	@Column(name="addr_line1", length=256, nullable=true, updatable=false)
	private String addrLine1;
	@Column(name="addr_line2", length=256, nullable=true, updatable=false)
	private String addrLine2;
	@Column(name="addr_line3", length=256, nullable=true, updatable=false)
	private String addrLine3;
	@Column(name="loc_line1", length=128, nullable=true, updatable=false)
	private String locLine1;
	@Column(name="loc_line2", length=128, nullable=true, updatable=false)
	private String locLine2;
	@Column(name="loc_line3", length=128, nullable=true, updatable=false)
	private String locLine3;
	@Column(name="loc_line4", length=128, nullable=true, updatable=false)
	private String locLine4;
	@Column(name="latitude", nullable=true, updatable = false)
	private float latitude;
	@Column(name="longitude", nullable=true, updatable = false)
	private float longitude;
	@Column(name="country", length=64, nullable=true, updatable=false)
	private String country;
	@Column(name="pincode", length=16, nullable=true, updatable=false)
	private String pincode;
	@Column(name="lang_code", length=3, nullable=false, updatable=false)
	private String langCode;
	@Column(name="is_deleted", nullable=true, updatable=false)
	@Type(type= "true_false")
	private boolean isDeleted;
	@Column(name="del_dtimes", nullable=true, updatable=false)
	private OffsetDateTime delDtimes;
	
	public String getCenterId() {
		return centerId;
	}
	public void setId(String centerId) {
		this.centerId = centerId;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setName(String centerName) {
		this.centerName = centerName;
	}
	public String getAddrLine1() {
		return addrLine1;
	}
	public void setAddrLine1(String addrLine1) {
		this.addrLine1 = addrLine1;
	}
	public String getAddrLine2() {
		return addrLine2;
	}
	public void setAddrLine2(String addrLine2) {
		this.addrLine2 = addrLine2;
	}
	public String getAddrLine3() {
		return addrLine3;
	}
	public void setAddrLine3(String addrLine3) {
		this.addrLine3 = addrLine3;
	}
	public String getLocLine1() {
		return locLine1;
	}
	public void setLocLine1(String locLine1) {
		this.locLine1 = locLine1;
	}
	public String getLocLine2() {
		return locLine2;
	}
	public void setLocLine2(String locLine2) {
		this.locLine2 = locLine2;
	}
	public String getLocLine3() {
		return locLine3;
	}
	public void setLocLine3(String locLine3) {
		this.locLine3 = locLine3;
	}
	public String getLocLine4() {
		return locLine4;
	}
	public void setLocLine4(String locLine4) {
		this.locLine4 = locLine4;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public OffsetDateTime getDelDtimes() {
		return delDtimes;
	}
	public void setDelDtimes(OffsetDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}
	
}
