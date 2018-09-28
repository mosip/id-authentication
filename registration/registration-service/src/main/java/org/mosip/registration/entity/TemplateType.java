package org.mosip.registration.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * TemplateType entity details
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Entity
@Table(schema="master", name = "TEMPLATE_TYPE")
public class TemplateType implements Serializable{

	/**
	 * Auto-Generated Serial Version Id
	 */
	private static final long serialVersionUID = -4228551994002616240L;
	@EmbeddedId
	private TemplateTypePK pk_tmplt_code;
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
	
	public TemplateTypePK getPk_tmplt_code() {
		return pk_tmplt_code;
	}

	public void setPk_tmplt_code(TemplateTypePK pk_tmplt_code) {
		this.pk_tmplt_code = pk_tmplt_code;
	}
	
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	
}
