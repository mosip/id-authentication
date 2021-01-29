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

@NoArgsConstructor
@Data
@Table(name = "credential_event_store", schema = "ida")
@Entity
public class CredentialEventStore {
	
	@Id
	@NotNull
	@Column(name = "event_id")
	private String eventId;
	
	@NotNull
	@Column(name = "event_topic")
	private String eventTopic;
	
	@NotNull
	@Column(name = "credential_transaction_id")
	private String credentialTransactionId;
	
	@Column(name = "publisher")
	private String publisher;

	@Column(name = "published_on_dtimes")
	private LocalDateTime publishedOnDtimes;

	@Column(name = "retry_count")
	private int retryCount;

	@NotNull
	@Size(max = 36)
	@Column(name = "status_code")
	private String statusCode;
	
	@NotNull
	@Column(name = "event_object")
	private String eventObject;

	@NotNull
	@Column(name = "cr_by")
	private String crBy;

	@NotNull
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column(name = "del_dtimes")
	private LocalDateTime delDTimes;

}
