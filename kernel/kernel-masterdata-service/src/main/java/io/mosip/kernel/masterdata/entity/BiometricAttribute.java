package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "biometric_attribute", schema = "master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiometricAttribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1302520630931393544L;
	@Id
	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "bmtyp_code", nullable = false)
	private String biometricTypeCode;

	@Column(name = "lang_code", nullable = false)
	private String langCode;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(name = "cr_by", nullable = false, length = 24)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdtime;

	@Column(name = "upd_by", length = 24)
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedtime;
}
