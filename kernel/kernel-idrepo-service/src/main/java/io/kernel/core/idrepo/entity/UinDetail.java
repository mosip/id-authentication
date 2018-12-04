package io.kernel.core.idrepo.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * The Class UinDetail.
 *
 * @author Manoj SP
 */
@Data
@Entity
public class UinDetail {

	/** The uin ref id. */
	@Id
	private String uinRefId;
	
	/** The uin data. */
	private byte[] uinData;
	
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
