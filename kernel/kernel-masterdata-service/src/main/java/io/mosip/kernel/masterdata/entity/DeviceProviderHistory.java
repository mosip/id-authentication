package io.mosip.kernel.masterdata.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Class DeviceProvider DTO.
 */
@Entity
@Table(name = "device_provider_h", schema = "master")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceProviderHistory extends BaseEntity {

	/** The id. */
	@Id
	@Column(name = "id")
	private String id;

	/** The vendor name. */
	@Column(name = "vendor_name")
	private String vendorName;

	/** The address. */
	@Column(name = "address")
	private String address;

	/** The email. */
	@Column(name = "email")
	private String email;

	/** The contact number. */
	@Column(name = "contact_number")
	private String contactNumber;

	/** The certificate alias. */
	@Column(name = "certificate_alias")
	private String certificateAlias;
	
	/** The effective date and time. */
	@Column(name = "eff_dtimes")
	private LocalDateTime effectivetimes;

}
