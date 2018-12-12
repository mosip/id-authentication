package io.kernel.core.idrepo.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * The Class UinDetailHistory.
 *
 * @author Manoj SP
 */
@Data
@Entity
public class UinDetailHistory {

	/** The uin ref id. */
	@Id
	private String uinRefId;
	
	/** The effective date time. */
	private Date effectiveDateTime;
	
	/** The uin data. */
	private Byte[] uinData;
	
	/** The status code. */
	private String statusCode;
	
	/** The created by. */
	private String createdBy;
	
	/** The created date time. */
	private Date createdDateTime;
	
	/** The updated by. */
	private String updatedBy;
	
	/** The updated date time. */
	private Date updatedDateTime;
	
	/** The is deleted. */
	private Boolean isDeleted;
	
	/** The deleted date time. */
	private Date deletedDateTime;
}
