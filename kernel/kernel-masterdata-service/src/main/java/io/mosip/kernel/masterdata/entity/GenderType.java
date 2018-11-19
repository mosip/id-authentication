package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gender entity mapped according to DB
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gender", schema = "master")
public class GenderType implements Serializable {
	private static final long serialVersionUID = 1323022736883315822L;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "genderCode", column = @Column(name = "code", nullable = false, length = 16)),
			@AttributeOverride(name = "genderName", column = @Column(name = "name", nullable = false, length = 64)) })
	@Column(name = "code", unique = true, nullable = false, length = 16)
	private GenderTypeId id;

	@Column(name = "lang_code", unique = true, nullable = false, length = 3)
	private String languageCode;

	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "cr_by", unique = true, nullable = false, length = 24)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private Date createdtime;

	@Column(name = "upd_by", unique = true, nullable = false, length = 24)
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private OffsetDateTime updatedtime;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private OffsetDateTime deletedtime;

}
