package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Manoj SP
 *
 */
@NoArgsConstructor
@Data
@Table(name = "otp_transaction", schema = "ida")
@Entity
public class OtpTransaction {

	@Id
	private String id;

	@Column(name = "ref_id")
	private String refId;

	@Column(name = "otp_hash")
	private String otpHash;

	@Column(name = "generated_dtimes")
	private LocalDateTime generatedDtimes;

	@Column(name = "expiry_dtimes")
	private LocalDateTime expiryDtimes;

	@Column(name = "validation_retry_count")
	private Integer validationRetryCount;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes")
	private LocalDateTime crDtimes;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delDtimes;
}
