package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class failed message entity.
 * 
 * @author Loganathan Sekar
 */
@NoArgsConstructor
@Data
@Table(name = "failed_message_store", schema = "ida")
@Entity
public class FailedMessageEntity {
	
	/** The id. */
	@Id
	@NotNull
	@Column(name = "id")
	private String id;
	
	/** The topic. */
	@NotNull
	@Column(name = "topic")
	private String topic;
	
	/** The published on dtimes. */
	@Column(name = "published_on_dtimes")
	private LocalDateTime publishedOnDtimes;

	/** The status code. */
	@NotNull
	@Size(max = 36)
	@Column(name = "status_code")
	private String statusCode;
	
	/** The event object. */
	@NotNull
	@Column(name = "message")
	private String message;
	
	/** The failed D times. */
	@NotNull
	@Column(name = "failed_dtimes")
	private LocalDateTime failedDTimes;

	/** The cr by. */
	@NotNull
	@Column(name = "cr_by")
	private String crBy;

	/** The cr D times. */
	@NotNull
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy;

	/** The upd D times. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	/** The del D times. */
	@Column(name = "del_dtimes")
	private LocalDateTime delDTimes;

}
