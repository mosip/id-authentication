package io.mosip.registration.processor.quality.check.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 * The Class UserRolePKEntity.
 *
 * @author M1048399
 * The primary key class for the user_role database table.
 */
@Embeddable
public class UserRolePKEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	/** The usr id. */
	@Column(name="usr_id")
	private String usrId;

	/** The role code. */
	@Column(name="role_code")
	private String roleCode;

	/** The lang code. */
	@Column(name="lang_code")
	private String langCode;

	/**
	 * Instantiates a new user role PK entity.
	 */
	public UserRolePKEntity() {
		super();
	}
	
	/**
	 * Gets the usr id.
	 *
	 * @return the usr id
	 */
	public String getUsrId() {
		return this.usrId;
	}
	
	/**
	 * Sets the usr id.
	 *
	 * @param usrId the new usr id
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	
	/**
	 * Gets the role code.
	 *
	 * @return the role code
	 */
	public String getRoleCode() {
		return this.roleCode;
	}
	
	/**
	 * Sets the role code.
	 *
	 * @param roleCode the new role code
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	/**
	 * Gets the lang code.
	 *
	 * @return the lang code
	 */
	public String getLangCode() {
		return this.langCode;
	}
	
	/**
	 * Sets the lang code.
	 *
	 * @param langCode the new lang code
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof UserRolePKEntity)) {
			return false;
		}
		UserRolePKEntity castOther = (UserRolePKEntity)other;
		return 
			this.usrId.equals(castOther.usrId)
			&& this.roleCode.equals(castOther.roleCode)
			&& this.langCode.equals(castOther.langCode);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.usrId.hashCode();
		hash = hash * prime + this.roleCode.hashCode();
		hash = hash * prime + this.langCode.hashCode();
		
		return hash;
	}
}