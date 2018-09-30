package org.mosip.registration.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * TemplateFileFormat entity details
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Entity
@Table(schema="master", name = "TEMPLATE_FILE_FORMAT")
public class TemplateFileFormat extends TemplateCommonFields {
	@EmbeddedId
	private TemplateEmbeddedKeyCommonFields pk_tfft_code;
	
	public TemplateEmbeddedKeyCommonFields getPk_tfft_code() {
		return pk_tfft_code;
	}

	public void setPk_tfft_code(TemplateEmbeddedKeyCommonFields pk_tfft_code) {
		this.pk_tfft_code = pk_tfft_code;
	}
}
