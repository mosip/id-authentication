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
 * Entity class for IdType.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@Entity
@Table(name = "id_type", schema = "master")
@NoArgsConstructor
@AllArgsConstructor
public class IdType implements Serializable {

	private static final long serialVersionUID = -97767928612692201L;

	@Id
	@Column(name = "code", length = 36)
	private String code;

	@Column(name = "name", nullable = false, length = 64)
	private String name;

	@Column(name = "descr", length = 128)
	private String descr;

	@Column(name = "lang_code", unique = true, nullable = false, length = 3)
	private String langCode;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(name = "cr_by", nullable = false, length = 32)
	private String crBy;

	@Column(name = "cr_dtimes", nullable = false)
	private String crDtimes;

	@Column(name = "upd_by", length = 32)
	private String updBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDtimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delDtimes;
}
