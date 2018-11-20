package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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


	private static final long serialVersionUID = -8541947587557590379L;

	@EmbeddedId
	private DeviceTypePk deviceTypeId;


	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "descr", length = 256)
	private String description;


	@OneToMany(mappedBy = "deviceType", cascade = CascadeType.ALL)
	private List<DeviceSpecification> deviceSpecifications = new ArrayList<>();

	public boolean addDeviceSpec(DeviceSpecification d) {
		return deviceSpecifications.add(d);
	}

}
