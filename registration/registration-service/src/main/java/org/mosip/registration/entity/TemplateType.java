package org.mosip.registration.entity;

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
	private TemplateEmbeddedKeyCommonFields pk_tmplt_code;

	public TemplateEmbeddedKeyCommonFields getPk_tmplt_code() {
		return pk_tmplt_code;
	}

	public void setPk_tmplt_code(TemplateEmbeddedKeyCommonFields pk_tmplt_code) {
		this.pk_tmplt_code = pk_tmplt_code;
	}
}
