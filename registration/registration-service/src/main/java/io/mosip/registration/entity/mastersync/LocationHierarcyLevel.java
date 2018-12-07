package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;


@Entity
@Table(name = "location", schema = "reg")
public class LocationHierarcyLevel extends RegistrationCommonFields implements Serializable  {
	
	private static final long serialVersionUID = -5585825455521742941L;
    
	@Id
	@OneToOne
	@JoinColumn(name = "code")
	private RegistrationCenter code;

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
