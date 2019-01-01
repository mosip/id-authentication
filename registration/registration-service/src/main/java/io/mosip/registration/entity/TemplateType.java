package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * TemplateType entity details
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Entity
@Table(schema="master", name = "TEMPLATE_TYPE")
public class TemplateType extends TemplateCommonFields {
	@EmbeddedId
	@Column(name="pk_tmplt_code")
	private TemplateEmbeddedKeyCommonFields pkTmpltCode;

	/**
	 * @return the pkTmpltCode
	 */
	public TemplateEmbeddedKeyCommonFields getPkTmpltCode() {
		return pkTmpltCode;
	}

	/**
	 * @param pkTmpltCode the pkTmpltCode to set
	 */
	public void setPkTmpltCode(TemplateEmbeddedKeyCommonFields pkTmpltCode) {
		this.pkTmpltCode = pkTmpltCode;
	}

	
}
