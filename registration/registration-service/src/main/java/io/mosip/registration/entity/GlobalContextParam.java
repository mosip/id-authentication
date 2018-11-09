package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;


/**
 * Global context parameter entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "master", name = "global_param")
public class GlobalContextParam extends RegistrationCommonFields {

	@EmbeddedId
	@Column(name = "pk_glbp_code")
	private GlobalContextParamId globalContextParamId;

	@Column(name = "name")
	private String name;
	@Column(name = "val")
	private String val;
	@Column(name = "typ")
	private String typ;
	@Column(name = "is_deleted")
	@Type(type= "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	/**
	 * @return the globalContextParamId
	 */
	public GlobalContextParamId getGlobalContextParamId() {
		return globalContextParamId;
	}

	/**
	 * @param globalContextParamId
	 *            the globalContextParamId to set
	 */
	public void setGlobalContextParamId(GlobalContextParamId globalContextParamId) {
		this.globalContextParamId = globalContextParamId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the val
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param val
	 *            the val to set
	 */
	public void setVal(String val) {
		this.val = val;
	}

	/**
	 * @return the typ
	 */
	public String getTyp() {
		return typ;
	}

	/**
	 * @param typ
	 *            the typ to set
	 */
	public void setTyp(String typ) {
		this.typ = typ;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the delDtimes
	 */
	public Timestamp getDelDtimes() {
		return delDtimes;
	}

	/**
	 * @param delDtimes
	 *            the delDtimes to set
	 */
	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}

}
