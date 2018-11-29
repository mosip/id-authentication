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

	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String alias;

	@Column(name = "app_id", nullable = false, length = 36)
	private String applicationId;

	@Column(name = "ref_id", nullable = false, length = 36)
	private String referenceId;

	@Column(name = "key_gen_dtimes")
	private LocalDateTime keyGenerationTime;

	@Column(name = "key_expire_dtimes")
	private LocalDateTime keyExpiryTime;

	@Column(name = "status_code", length = 36)
	private String status;

}
