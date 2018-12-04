package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for valid document.
 * 
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "valid_document", schema = "master")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ValidDocumentID.class)
public class ValidDocument extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3111581667845281498L;

	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "docTypeCode", column = @Column(name = "doctyp_code", nullable = false, length = 36)),
			@AttributeOverride(name = "docCategoryCode", column = @Column(name = "doccat_code", nullable = false, length = 36)) })

	private String docTypeCode;

	private String docCategoryCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "doccat_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false), })
	DocumentCategory documentCategory;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "doctyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	DocumentType documentType;

	@Column(name = "lang_code", length = 3)
	private String langCode;

}
