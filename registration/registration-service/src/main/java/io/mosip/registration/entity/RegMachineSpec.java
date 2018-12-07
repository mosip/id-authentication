package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@Entity
@Table(name = "machine_spec", schema = "reg")
public class RegMachineSpec extends RegistrationCommonFields {
	@Id
	@Column(name = "id", length = 36, nullable = false)
	private String id;
	@Column(name = "name", length = 64, nullable = false)
	private String name;
	@Column(name = "brand", length = 32, nullable = false)
	private String brand;
	@Column(name = "model", length = 16, nullable = false)
	private String model;
	@Column(name = "mtyp_code", length = 36, nullable = false)
	private String mtypeCode;
	@Column(name = "min_driver_ver", length = 16, nullable = false)
	private String minDriverVersion;
	@Column(name = "descr", length = 256)
	private String description;
	@Column(name = "lang_code", length = 3, nullable = false)
	private String languageCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMtypeCode() {
		return mtypeCode;
	}

	public void setMtypeCode(String mtypeCode) {
		this.mtypeCode = mtypeCode;
	}

	public String getMinDriverVersion() {
		return minDriverVersion;
	}

	public void setMinDriverVersion(String minDriverVersion) {
		this.minDriverVersion = minDriverVersion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
