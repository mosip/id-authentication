package io.mosip.registration.entity.mastersync.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Embeddable
@Data
public class ValidDocumentID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6394443536056430885L;

	@Column(name = "doctyp_code")
	private String docTypeCode;

	@Column(name = "doccat_code")
	private String docCategoryCode;

	/**
	 * @return the docTypeCode
	 */
	public String getDocTypeCode() {
		return docTypeCode;
	}

	/**
	 * @param docTypeCode the docTypeCode to set
	 */
	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	/**
	 * @return the docCategoryCode
	 */
	public String getDocCategoryCode() {
		return docCategoryCode;
	}

	/**
	 * @param docCategoryCode the docCategoryCode to set
	 */
	public void setDocCategoryCode(String docCategoryCode) {
		this.docCategoryCode = docCategoryCode;
	}
	
	

}
