package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location", schema = "master")
public class LocationHierarcyLevel extends BaseEntity implements Serializable {

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

