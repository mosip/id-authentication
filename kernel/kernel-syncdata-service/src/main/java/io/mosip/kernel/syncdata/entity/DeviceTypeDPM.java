package io.mosip.kernel.syncdata.entity;

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
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "reg_device_type", schema = "master")
public class DeviceTypeDPM extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6764549464759477685L;

	/** The code. */
	@Id
	@Column(name = "code")
	private String code;

	/** The name. */
	@Column(name = "name")
	private String name;

	/** The descr. */
	@Column(name = "descr")
	private String descr;

}
