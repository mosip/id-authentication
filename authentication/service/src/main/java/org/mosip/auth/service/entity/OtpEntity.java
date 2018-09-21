package org.mosip.auth.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OtpEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private int id;

	@Column(name = "uniqueid", nullable = false)
	private String uniqueID;

	@Column(name = "tempid", nullable = false)
	private String tempID;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public String getTempID() {
		return tempID;
	}

	public void setTempID(String tempID) {
		this.tempID = tempID;
	}

}
