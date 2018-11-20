package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_type", schema = "master")
public class DeviceType extends BaseEntity implements Serializable {

	// @IdClass(DeviceTypePk.class)
	private static final long serialVersionUID = -8541947587557590379L;

	@EmbeddedId
	private DeviceTypePk deviceTypeId;

	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 64)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)) })

	private String code;
	private String langCode;

	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "descr", length = 256)
	private String description;

	// @JoinColumns({
	// @JoinColumn(name = "dtyp_code", referencedColumnName = "code"),
	// @JoinColumn(name = "lang_code", referencedColumnName = "lang_code")
	// })
	@OneToMany(mappedBy = "deviceType", cascade = CascadeType.ALL)
	private List<DeviceSpecification> deviceSpecification;

}
