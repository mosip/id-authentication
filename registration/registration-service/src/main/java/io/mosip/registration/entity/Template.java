package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Template entity details
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Entity
@Table(schema = "master", name = "TEMPLATE")
public class Template extends TemplateCommonFields {

	@Id
	private String id;
	private String name;
	@Column(name="file_format_code")
	private String fileFormatCode;
	private String model;
	@Column(name="file_txt")
	private String fileTxt;
	@Column(name="module_id")
	private String moduleId;
	@Column(name="module_name")
	private String moduleName;
	@Column(name="template_typ_code")
	private String templateTypCode;
	@Column(name="lang_code")
	private String langCode;
	
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

	public String getFileFormatCode() {
		return fileFormatCode;
	}

	public void setFileFormatCode(String fileFormatCode) {
		this.fileFormatCode = fileFormatCode;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFileTxt() {
		return fileTxt;
	}

	public void setFileTxt(String fileTxt) {
		this.fileTxt = fileTxt;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getTemplateTypCode() {
		return templateTypCode;
	}

	public void setTemplateTypCode(String templateTypCode) {
		this.templateTypCode = templateTypCode;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileFormatCode == null) ? 0 : fileFormatCode.hashCode());
		result = prime * result + ((fileTxt == null) ? 0 : fileTxt.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((langCode == null) ? 0 : langCode.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((moduleId == null) ? 0 : moduleId.hashCode());
		result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((templateTypCode == null) ? 0 : templateTypCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Template other = (Template) obj;
		if (fileFormatCode == null) {
			if (other.fileFormatCode != null)
				return false;
		} else if (!fileFormatCode.equals(other.fileFormatCode))
			return false;
		if (fileTxt == null) {
			if (other.fileTxt != null)
				return false;
		} else if (!fileTxt.equals(other.fileTxt))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (langCode == null) {
			if (other.langCode != null)
				return false;
		} else if (!langCode.equals(other.langCode))
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (moduleId == null) {
			if (other.moduleId != null)
				return false;
		} else if (!moduleId.equals(other.moduleId))
			return false;
		if (moduleName == null) {
			if (other.moduleName != null)
				return false;
		} else if (!moduleName.equals(other.moduleName))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (templateTypCode == null) {
			if (other.templateTypCode != null)
				return false;
		} else if (!templateTypCode.equals(other.templateTypCode))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "Template [id=" + id + ", name=" + name + ", descr=" + descr + ", file_format_code=" + fileFormatCode
				+ ", model=" + model + ", file_txt=" + fileTxt + ", module_id=" + moduleId + ", module_name="
				+ moduleName + ", template_typ_code=" + templateTypCode + ", lang_code=" + langCode + ", is_active="
				+ isActive + ", cr_by=" + crBy + ", cr_dtimes=" + crDtimes + ", upd_by=" + updBy + ", upd_dtimes="
				+ updDtimes + ", is_deleted=" + isDeleted + ", del_dtimes=" + delDtimes + "]";
	}

}
