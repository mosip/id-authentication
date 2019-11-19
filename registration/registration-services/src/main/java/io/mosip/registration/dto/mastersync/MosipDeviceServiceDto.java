package io.mosip.registration.dto.mastersync;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MosipDeviceServiceDto extends MasterSyncBaseDto {

	private String id;
	private String swBinaryHash;
	private String swVersion;
	private String dproviderId;
	private String dtypeCode;
	private String dsTypeCode;
	private String make;
	private String model;
	private Timestamp swCrDtimes;
	private Timestamp swExpiryDtimes;
	private Boolean isActive;
	private String crBy;
	private Timestamp crDtime;
	private String updBy;
	private Timestamp updDtimes;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public byte[] getSwBinaryHash() {
		return swBinaryHash.getBytes();
	}
	public void setSwBinaryHash(String swBinaryHash) {
		this.swBinaryHash = swBinaryHash;
	}
	public String getSwVersion() {
		return swVersion;
	}
	public void setSwVersion(String swVersion) {
		this.swVersion = swVersion;
	}
	public String getdProviderId() {
		return dproviderId;
	}
	public void setdProviderId(String dproviderId) {
		this.dproviderId = dproviderId;
	}
	public String getdTypeCode() {
		return dtypeCode;
	}
	public void setdTypeCode(String dtypeCode) {
		this.dtypeCode = dtypeCode;
	}
	public String getDsTypeCode() {
		return dsTypeCode;
	}
	public void setDsTypeCode(String dsTypeCode) {
		this.dsTypeCode = dsTypeCode;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public Timestamp getSwCrDtimes() {
		return swCrDtimes;
	}
	public void setSwCrDtimes(Timestamp swCrDtimes) {
		this.swCrDtimes = swCrDtimes;
	}
	public Timestamp getSwExpiryDtimes() {
		return swExpiryDtimes;
	}
	public void setSwExpiryDtimes(Timestamp swExpiryDtimes) {
		this.swExpiryDtimes = swExpiryDtimes;
	}
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
	public Timestamp getCrDtime() {
		return crDtime;
	}
	public void setCrDtime(Timestamp crDtime) {
		this.crDtime = crDtime;
	}
	public String getUpdBy() {
		return updBy;
	}
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}
	public Timestamp getUpdDtimes() {
		return updDtimes;
	}
	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}
	public Timestamp getDelDtimes() {
		return delDtimes;
	}
	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}
	private Timestamp delDtimes;
}

