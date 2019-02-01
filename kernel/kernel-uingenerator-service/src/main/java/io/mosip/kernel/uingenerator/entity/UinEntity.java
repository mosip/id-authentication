package io.mosip.kernel.uingenerator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for uin bean
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "uin", schema = "kernel")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UinEntity extends BaseEntity {

	/**
	 * Field for uin
	 */
	@Id
	@Column(name = "uin", unique = true, nullable = false, updatable = false, length = 28)
	private String uin;

	/**
	 * Field whether this uin is used
	 */
	@Column(name = "is_used")
	private boolean used;

}
