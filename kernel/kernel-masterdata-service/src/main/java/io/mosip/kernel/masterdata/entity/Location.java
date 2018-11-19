package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Srinivasan
 *
 */
@Entity
@Table(name = "location", schema = "master")
@Data
public class Location implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	@Id
	@Column(name = "code", nullable = false,length=36)
	private String code;

	@Column(name = "name", nullable = false,length=128)
	private String name;

	@Column(name = "hierarchy_level", nullable = false)
	private Integer hierarchyLevel;

	@Column(name = "hierarchy_level_name", nullable = false,length=64)
	private String hierarchyName;
	
	@Column(name = "parent_loc_code", nullable = false,length=32)
	private String parentLocCode;
    
	
	@Column(name = "lang_code", nullable = false,length=3)
	private String languageCode;

	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "cr_by",length=64)

	private String createdBy;

	@Column(name = "upd_by",length=64)
	private String updatedBy;

}
