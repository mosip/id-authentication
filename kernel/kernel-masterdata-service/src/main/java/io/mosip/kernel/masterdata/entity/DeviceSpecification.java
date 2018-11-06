package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author M1046571
 * 
 *
 */
@Entity
@Table(name = "device_spec", schema = "master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSpecification implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false)
	private String id;
	@Column(name = "name", nullable = false, length = 64)
	private String name;
	@Column(name = "brand", nullable = false, length = 32)
	private String brand;
	@Column(name = "model", nullable = false, length = 16)
	private String model;
	@Column(name = "dtyp_code", nullable = false, length = 36)
	private String deviceTypeCode;
	@Column(name = "min_driver_ver", nullable = false, length = 16)
	private String minDriverversion;
	@Column(name = "descr", length = 256)
	private String description;
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
	@Column(name = "is_active", nullable = false)
	private boolean isActive;
	@Column(name = "cr_by", nullable = false, length = 24)
	private String createdBy;
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdtime;
	@Column(name = "upd_by", length = 24)
	private String updatedBy;
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtime;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private LocalDateTime deletedtime;

}
