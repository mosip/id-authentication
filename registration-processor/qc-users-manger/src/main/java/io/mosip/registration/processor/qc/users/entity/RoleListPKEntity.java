package io.mosip.registration.processor.qc.users.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * @author M1048399
 * The primary key class for the role_list database table.
 */
@Embeddable
public class RoleListPKEntity implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="code")
	private String roleCode;

	@Column(name="lang_code")
	private String langCode;

	public RoleListPKEntity() {
		super();
	}
	public String getRoleCode() {
		return this.roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getLangCode() {
		return this.langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RoleListPKEntity)) {
			return false;
		}
		RoleListPKEntity castOther = (RoleListPKEntity)other;
		return 
			this.roleCode.equals(castOther.roleCode)
			&& this.langCode.equals(castOther.langCode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.roleCode.hashCode();
		hash = hash * prime + this.langCode.hashCode();
		
		return hash;
	}
}