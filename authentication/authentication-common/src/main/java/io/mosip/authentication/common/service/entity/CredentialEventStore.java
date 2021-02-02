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
 * Credential event store entity.
 * @author Loganathan Sekar
 */
@NoArgsConstructor
@Data
@Table(name = "credential_event_store", schema = "ida")
@Entity
public class CredentialEventStore {
	
	/** The event id. */
	@Id
	@NotNull
	@Column(name = "event_id")
	private String eventId;
	
	/** The event topic. */
	@NotNull
	@Column(name = "event_topic")
	private String eventTopic;
	
	/** The credential transaction id. */
	@NotNull
	@Column(name = "credential_transaction_id")
	private String credentialTransactionId;
	
	/** The publisher. */
	@Column(name = "publisher")
	private String publisher;

	/** The published on dtimes. */
	@Column(name = "published_on_dtimes")
	private LocalDateTime publishedOnDtimes;

	/** The retry count. */
	@Column(name = "retry_count")
	private int retryCount;

	/** The status code. */
	@NotNull
	@Size(max = 36)
	@Column(name = "status_code")
	private String statusCode;
	
	/** The event object. */
	@NotNull
	@Column(name = "event_object")
	private String eventObject;

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
