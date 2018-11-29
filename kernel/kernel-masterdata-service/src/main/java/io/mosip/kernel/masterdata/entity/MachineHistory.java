
package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Table(name = "machine_master_h", schema = "master")
@IdClass(IdAndEffectDtimes.class)
public class MachineHistory extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false, length = 36)),
			@AttributeOverride(name = "effectDtimes", column = @Column(name = "eff_dtimes", nullable = false)) })
	private String id;
	private LocalDateTime effectDtimes;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;
	/**
	 * Field for machine name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	/**
	 * Field for machine mac address
	 */
	@Column(name = "mac_address", nullable = false, length = 64)
	private String macAddress;

	/**
	 * Field for machine serial number
	 */
	@Column(name = "serial_num", nullable = false)
	private String serialNum;

	/**
	 * Field for machine ip address
	 */
	@Column(name = "ip_address", length = 64)
	private String ipAddress;

	/**
	 * Field for machine specific id
	 */
	@Column(name = "mspec_id", nullable = false, length = 36)
	private String mspecId;

	/**
	 * Field for language code
	 */
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
	/**
	 * Field to hold effected date and time
	 */
	@Column(name = "validity_end_dtimes")
	private LocalDateTime valEndDtimes;

}
