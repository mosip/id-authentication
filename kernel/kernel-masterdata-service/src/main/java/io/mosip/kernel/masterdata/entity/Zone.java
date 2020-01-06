package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zone", schema = "master")
@IdClass(CodeAndLanguageCodeID.class)
public class Zone extends BaseEntity implements Serializable {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 3690188968108639857L;

	@Id
	@Column(name = "code")
	private String code;

	@Id
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "hierarchy_level", nullable = false)
	private short hierarchyLevel;

	@Column(name = "hierarchy_level_name", nullable = false, length = 64)
	private String hierarchyName;

	@Column(name = "parent_zone_code", nullable = false, length = 36)
	private String parentZoneCode;

	@Column(name = "hierarchy_path", nullable = false, length = 1024)
	private String hierarchyPath;

}
