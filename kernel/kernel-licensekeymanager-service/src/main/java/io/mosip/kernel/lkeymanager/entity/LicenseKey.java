package io.mosip.kernel.lkeymanager.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "master", name = "lkey")
public class LicenseKey {
	/**
	 * 
	 */
	@Id
	@Column(name = "tsp_id", nullable = false)
	private String tspId;

	/**
	 * 
	 */
	@Column(name = "lkey", nullable = false)
	private String lKey;

	/**
	 * 
	 */
	@Column(name = "cr_by")
	private String createdBy;

	/**
	 * 
	 */
	@Column(name = "cr_at")
	private LocalDateTime createdAt;
}
