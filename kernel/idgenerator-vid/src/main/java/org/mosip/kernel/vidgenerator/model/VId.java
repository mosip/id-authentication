package org.mosip.kernel.vidgenerator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * entity class for VId generator
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "vids", schema = "ids")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VId {

	/**
	 * Field for uin
	 */
	@Id
	@Column(name = "uin", unique = true, nullable = false, updatable = false)
	private String uin;

	/**
	 * Field for vid
	 */
	@Column(name = "vid", unique = true, nullable = false, updatable = true, length = 16)
	private String vid;

	/**
	 * Field for created time of vid
	 */
	@Column(name = "createdAt")
	private long createdAt;

}
