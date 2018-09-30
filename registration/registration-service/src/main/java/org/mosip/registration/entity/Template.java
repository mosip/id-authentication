package org.mosip.registration.entity;

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
	private String file_format_code;
	private String model;
	private String file_txt;
	private String module_id;
	private String module_name;
	private String template_typ_code;
	private String lang_code;

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

	public String getFile_format_code() {
		return file_format_code;
	}

	public void setFile_format_code(String file_format_code) {
		this.file_format_code = file_format_code;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFile_txt() {
		return file_txt;
	}

	public void setFile_txt(String file_txt) {
		this.file_txt = file_txt;
	}

	public String getModule_id() {
		return module_id;
	}

	public void setModule_id(String module_id) {
		this.module_id = module_id;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getTemplate_typ_code() {
		return template_typ_code;
	}

	public void setTemplate_typ_code(String template_typ_code) {
		this.template_typ_code = template_typ_code;
	}

	public String getLang_code() {
		return lang_code;
	}

	public void setLang_code(String lang_code) {
		this.lang_code = lang_code;
	}

	@Override
	public String toString() {
		return "Template [id=" + id + ", name=" + name + ", descr=" + descr + ", file_format_code=" + file_format_code
				+ ", model=" + model + ", file_txt=" + file_txt + ", module_id=" + module_id + ", module_name="
				+ module_name + ", template_typ_code=" + template_typ_code + ", lang_code=" + lang_code + ", is_active="
				+ isActive + ", cr_by=" + cr_by + ", cr_dtimes=" + cr_dtimes + ", upd_by=" + upd_by + ", upd_dtimes="
				+ upd_dtimes + ", is_deleted=" + is_deleted + ", del_dtimes=" + del_dtimes + "]";
	}

}
