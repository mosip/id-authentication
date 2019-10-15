package io.mosip.kernel.syncdata.entity;

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
 * @since 1.0.0
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "reg_device_sub_type", schema = "master")
public class DeviceSubTypeDPM extends BaseEntity {

	/** The code. */
	@Id
	@Column(name = "code")
	private String code;

	/** The dtype code. */
	@Column(name = "dtyp_code")
	private String dtypeCode;

	/** The name. */
	@Column(name = "name")
	private String name;

	/** The descr. */
	@Column(name = "descr")
	private String descr;

}
