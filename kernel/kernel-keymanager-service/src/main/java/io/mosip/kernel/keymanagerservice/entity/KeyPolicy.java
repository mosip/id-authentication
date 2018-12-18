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
 * Entity class for KeyPolicy
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "key_policy_def", schema = "kernel")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class KeyPolicy extends BaseEntity {

	/**
	 * The field applicationId
	 */
	@Id
	@Column(name = "app_id", nullable = false, length = 36)
	private String applicationId;

	/**
	 * The field validityInDays
	 */
	@Column(name = "key_validity_duration")
	private int validityInDays;

	/**
	 * The field isActive
	 */
	@Column(name = "is_active")
	private boolean isActive;

}
