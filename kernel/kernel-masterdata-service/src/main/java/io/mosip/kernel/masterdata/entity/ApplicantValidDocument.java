package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.ApplicantValidDocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * Entity for Applicant valid document
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applicant_valid_document", schema = "master")
public class ApplicantValidDocument extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for individual type code
	 */
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "appTypeCode", column = @Column(name = "apptyp_code")),
			@AttributeOverride(name = "docCategoryCode", column = @Column(name = "doccat_code")),
			@AttributeOverride(name = "docTypeCode", column = @Column(name = "doctyp_code")) })
	private ApplicantValidDocumentId applicantValidDocumentId;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
}
