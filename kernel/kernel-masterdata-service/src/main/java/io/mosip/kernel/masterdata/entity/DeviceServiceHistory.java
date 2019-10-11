/**
 * 
 */
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
 * @author Ramadurai Pandian
 *
 */
@Entity
@Table(name = "mosip_device_service_h", schema = "master")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceServiceHistory extends BaseEntity {
	


	/** The id. */
	@Id
	@Column(name = "id")
	private String id;

	/** The sw binary hash. */
	@Column(name = "sw_binary_hash")
	private byte[] swBinaryHash;

	/** The sw version. */
	@Column(name = "sw_version")
	private String swVersion;

	/** The d provider id. */
	@Column(name = "dprovider_id")
	private String dProviderId;

	/** The d type code. */
	@Column(name = "dtype_code")
	private String dTypeCode;

	/** The ds type code. */
	@Column(name = "dstype_code")
	private String dsTypeCode;

	/** The make. */
	@Column(name = "make")
	private String make;

	/** The model. */
	@Column(name = "model")
	private String model;

	/** The sw created time. */
	@Column(name = "sw_cr_dtimes")
	private LocalDateTime swCreatedTime;

	/** The sw expiry time. */
	@Column(name = "sw_expiry_dtimes")
	private LocalDateTime swExpiryTime;

	private LocalDateTime effectivetimes;
}
