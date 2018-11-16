package io.kernel.core.idrepo.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class UinDetail {
	
	@Id
	private String uinRefId;
	private byte[] uinData;
	private String createdBy;
	private Date createdDateTime;
	private String updatedBy;
	private Date updatedDateTime;
	private Boolean isDeleted;
	private Date deletedDateTime;
}
