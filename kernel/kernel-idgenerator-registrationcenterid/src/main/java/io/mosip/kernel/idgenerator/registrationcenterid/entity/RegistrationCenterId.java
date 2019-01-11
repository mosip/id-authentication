package io.mosip.kernel.idgenerator.registrationcenterid.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class for Registration Center ID.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Entity
@Table(schema = "ids", name = "rcid")
@Data
public class RegistrationCenterId {
	/**
	 * the registration center id.
	 */
	@Id
	@Column(name = "reg_center_id")
	private int rcid;
}
