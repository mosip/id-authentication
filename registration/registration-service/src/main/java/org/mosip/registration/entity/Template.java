package org.mosip.registration.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * Template entity details
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Entity
@Table(schema="master", name = "TEMPLATE")
public class Template {

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
	private String descr;
	@Column(name="is_active")
	@Type(type= "true_false")
	private boolean isActive;
	private String cr_by;
	private Date cr_dtimes;
	private String upd_by;
	private Date upd_dtimes;
	private boolean is_deleted;
	private Date del_dtimes;
	
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getCr_by() {
		return cr_by;
	}
	public void setCr_by(String cr_by) {
		this.cr_by = cr_by;
	}
	public Date getCr_dtimes() {
		return cr_dtimes;
	}
	public void setCr_dtimes(Date cr_dtimes) {
		this.cr_dtimes = cr_dtimes;
	}
	public String getUpd_by() {
		return upd_by;
	}
	public void setUpd_by(String upd_by) {
		this.upd_by = upd_by;
	}
	public Date getUpd_dtimes() {
		return upd_dtimes;
	}
	public void setUpd_dtimes(Date upd_dtimes) {
		this.upd_dtimes = upd_dtimes;
	}
	public boolean isIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	public Date getDel_dtimes() {
		return del_dtimes;
	}
	public void setDel_dtimes(Date del_dtimes) {
		this.del_dtimes = del_dtimes;
	}
	
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
	
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
