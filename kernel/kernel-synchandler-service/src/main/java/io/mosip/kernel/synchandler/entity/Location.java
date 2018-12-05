package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Srinivasan
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location", schema = "master")

public class Location extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = -5585825705521742941L;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "hierarchy_level", nullable = false)
	private Integer hierarchyLevel;

	@Column(name = "hierarchy_level_name", nullable = false, length = 64)
	private String hierarchyName;

	@Column(name = "parent_loc_code", nullable = false, length = 32)
	private String parentLocCode;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String languageCode;

}
