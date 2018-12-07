package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */

@Entity
@Table(name = "template", schema = "reg")
public class Template extends RegistrationCommonFields implements Serializable {

	/**
	 * Generated serialization id
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "file_format_code")
	private String fileFormatCode;

	@Column(name = "model")
	private String model;

	@Column(name = "file_txt")
	private String fileText;

	@Column(name = "module_id")
	private String moduleId;

	@Column(name = "module_name")
	private String moduleName;

	@Column(name = "template_typ_code")
	private String templateTypeCode;

	@Column(name = "lang_code")
	private String langCode;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the fileFormatCode
	 */
	public String getFileFormatCode() {
		return fileFormatCode;
	}

	/**
	 * @param fileFormatCode the fileFormatCode to set
	 */
	public void setFileFormatCode(String fileFormatCode) {
		this.fileFormatCode = fileFormatCode;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the fileText
	 */
	public String getFileText() {
		return fileText;
	}

	/**
	 * @param fileText the fileText to set
	 */
	public void setFileText(String fileText) {
		this.fileText = fileText;
	}

	/**
	 * @return the moduleId
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * @return the templateTypeCode
	 */
	public String getTemplateTypeCode() {
		return templateTypeCode;
	}

	/**
	 * @param templateTypeCode the templateTypeCode to set
	 */
	public void setTemplateTypeCode(String templateTypeCode) {
		this.templateTypeCode = templateTypeCode;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

}
