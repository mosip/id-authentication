package io.mosip.kernel.vidgenerator.entity;

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
 * Entity class for vid bean
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "vid", schema = "kernel")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class VidEntity extends BaseEntity {

	/**
	 * Field for vid
	 */
	@Id
	@Column(name = "vid", unique = true, nullable = false, updatable = false, length = 28)
	private String vid;

	/**
	 * Field whether this vid is used
	 */
	@Column(name = "vid_status", nullable = false, length = 16)
	private String status;

	/**
	 * The field createdtimes
	 */
	@Column(name = "vid_expiry")
	private LocalDateTime vidExpiry;
}
