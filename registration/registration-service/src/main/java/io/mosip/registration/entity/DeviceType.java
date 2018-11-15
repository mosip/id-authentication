package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "device_type", schema = "reg")
public class DeviceType extends RegistrationCommonFields{
	@EmbeddedId
	private RegDeviceTypeId regDeviceTypeId;
	@Column(name = "name")
	private String name;
	@Column(name = "descr")
	private String description;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getDeletedTime() {
		return deletedTime;
	}

	public void setDeletedTime(Timestamp deletedTime) {
		this.deletedTime = deletedTime;
	}

	public RegDeviceTypeId getRegDeviceTypeId() {
		return regDeviceTypeId;
	}

	public void setRegDeviceTypeId(RegDeviceTypeId regDeviceTypeId) {
		this.regDeviceTypeId = regDeviceTypeId;
	}

}
