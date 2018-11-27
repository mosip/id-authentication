package io.mosip.kernel.keymanagerservice.entity;

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
@Table(name = "key_policy_def", schema = "keymanager")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class KeyPolicy extends BaseEntity {

	@Id
	@Column(name = "app_id", nullable = false, length = 36)
	private String applicationId;

	@Column(name = "key_validity_duration")
	private String validityInDays;

	@Column(name = "is_active")
	private Boolean isActive;

}
