package org.mosip.kernel.vidgenerator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * entity class for VId generator
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */

@Entity
@Table(name = "vid", schema = "ids")
@Data
public class Vid {

	/**
	 * Field for uin
	 */
	@Id
	@Column(name = "uin", unique = true, nullable = false, updatable = false)
	private String uin;

	/**
	 * Field for id
	 */
	@Column(name = "id", unique = true, nullable = false, updatable = true, length = 16)
	private String id;

	/**
	 * Field for created time of vid
	 */
	@Column(name = "createdAt")
	private long createdAt;

}
