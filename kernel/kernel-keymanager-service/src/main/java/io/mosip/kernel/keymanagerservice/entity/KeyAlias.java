package io.mosip.kernel.keymanagerservice.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for KeyAlias
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "key_alias", schema = "kernel")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class KeyAlias extends BaseEntity {

	/**
	 * The field alias
	 */
	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String alias;

	/**
	 * The field applicationId
	 */
	@Column(name = "app_id", nullable = false, length = 36)
	private String applicationId;

	/**
	 * The field referenceId
	 */
	@Column(name = "ref_id", length = 36)
	private String referenceId;

	/**
	 * The field keyGenerationTime
	 */
	@Column(name = "key_gen_dtimes")
	private LocalDateTime keyGenerationTime;

	/**
	 * The field keyExpiryTime
	 */
	@Column(name = "key_expire_dtimes")
	private LocalDateTime keyExpiryTime;

	/**
	 * The field status
	 */
	@Column(name = "status_code", length = 36)
	private String status;

}
