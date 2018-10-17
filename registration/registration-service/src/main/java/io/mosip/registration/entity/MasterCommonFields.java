package io.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
/**
 * 
 * @author Brahmananda Reddy
 *
 */

@MappedSuperclass
@Data
public class MasterCommonFields {
	@Column(name="lang_code",length=3,nullable=false)
	private String languageCode;
	@Column(name="cr_by",length=24,nullable=false)
	private String createdBy;
	@Column(name="cr_dtimesz",nullable=false)
	private OffsetDateTime createdTimesZone;
	@Column(name="upd_by",length=24,nullable=true)
	private String updatedBy;
	@Column(name="upd_dtimesz",nullable=true)
	private OffsetDateTime updatedTimesZone;
	@Column(name="is_deleted")
	private boolean isDeleted;
	@Column(name="del_dtimesz")
	private OffsetDateTime deletedTimesZone;
	

}
