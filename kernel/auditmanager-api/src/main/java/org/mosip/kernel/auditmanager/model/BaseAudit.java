package org.mosip.kernel.auditmanager.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Base class for {@link Audit} with {@link #uuid} and {@link #timestamp}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@MappedSuperclass
@Data
@AllArgsConstructor
public class BaseAudit implements Serializable {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -1499777760652041150L;

	/**
	 * Field for immutable universally unique identifier (UUID)
	 */
	@Id
	@Column(name = "UUID", nullable = false, updatable = false)
	private String uuid;

	@Column(name = "CREATED_AT", nullable = false, updatable = false)
	private OffsetDateTime createdAt; // ,columnDefinition= "TIMESTAMP WITH TIME ZONE"

	/**
	 * Constructor to initialize {@link BaseAudit} with uuid and timestamp
	 */
	public BaseAudit() {
		uuid = UUID.randomUUID().toString();
		createdAt = OffsetDateTime.now();
	}

}
