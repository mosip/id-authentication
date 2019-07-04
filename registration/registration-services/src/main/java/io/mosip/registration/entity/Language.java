package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * This Entity Class contains list of languages that are being used in Registration
 * The data for this table will come through sync from server master table 
 * 
 * @author Sreekar chukka
 * @since 1.0.0
 */

@Entity
@Table(name = "language", schema = "reg")
public class Language extends RegistrationCommonFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for language code
	 */
	@Id
	@Column(name = "code")
	private String code;

	/**
	 * Field for language name
	 */
	@Column(name = "name")
	private String name;

	/**
	 * Field for language family
	 */
	@Column(name = "family")
	private String family;

	/**
	 * Field for language native name
	 */
	@Column(name = "native_name")
	private String nativeName;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * @return the nativeName
	 */
	public String getNativeName() {
		return nativeName;
	}

	/**
	 * @param nativeName the nativeName to set
	 */
	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

}
