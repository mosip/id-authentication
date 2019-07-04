package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The Entity Class for Template FileFormat details
 * 
 * @author Himaja Dhanyamraju
 * @since 1.0.0
 */
@Entity
@Table(schema="reg", name = "TEMPLATE_FILE_FORMAT")
public class TemplateFileFormat extends TemplateCommonFields {
	@EmbeddedId
	@Column(name="pk_tfft_code")
	private TemplateEmbeddedKeyCommonFields pkTfftCode;

	/**
	 * @return the pkTfftCode
	 */
	public TemplateEmbeddedKeyCommonFields getPkTfftCode() {
		return pkTfftCode;
	}

	/**
	 * @param pkTfftCode the pkTfftCode to set
	 */
	public void setPkTfftCode(TemplateEmbeddedKeyCommonFields pkTfftCode) {
		this.pkTfftCode = pkTfftCode;
	}
}
