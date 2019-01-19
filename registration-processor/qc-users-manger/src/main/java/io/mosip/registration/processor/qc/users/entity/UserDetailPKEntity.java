package io.mosip.registration.processor.qc.users.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class UserDetailPKEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;

	public UserDetailPKEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
