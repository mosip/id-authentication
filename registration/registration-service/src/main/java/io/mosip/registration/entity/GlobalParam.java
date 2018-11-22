package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Global parame entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "master", name = "global_param")
public class GlobalParam extends RegistrationCommonFields {

	@EmbeddedId
	@Column(name = "pk_glbp_code")
	private GlobalParamId globalParamId;

	@Column(name = "name")
	private String name;
	@Column(name = "val")
	private String val;
	@Column(name = "typ")
	private String typ;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

	/**
	 * @return the globalContextParamId
	 */
	public GlobalParamId getGlobalParamId() {
		return globalParamId;
	}

	/**
	 * @param globalContextParamId
	 *            the globalContextParamId to set
	 */
	public void setGlobalParamId(GlobalParamId globalParamId) {
		this.globalParamId = globalParamId;
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
