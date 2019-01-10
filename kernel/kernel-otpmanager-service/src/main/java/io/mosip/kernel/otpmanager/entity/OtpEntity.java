package io.mosip.kernel.otpmanager.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.mosip.kernel.otpmanager.constant.OtpStatusConstants;
import lombok.Data;

/**
 * The entity class for OTP.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Entity
@Data
@Table(name = "otp_transaction", schema = "kernel")
public class OtpEntity {
	/**
	 * The variable that holds the unique ID.
	 */
	@Id
	@Column(name = "id")
	private String id;

	/**
	 * The variable that holds the time at which the OTP validation was last
	 * attempted. The default value is the generation time.
	 */
	@Column(name = "upd_dtimes", nullable = false)
	private LocalDateTime updatedDtimes;

	/**
	 * The variable that holds the generated OTP.
	 */
	@Column(name = "otp", length = 8)
	private String otp;

	/**
	 * The variable that holds the number of validation attempts.
	 */
	@Column(name = "validation_retry_count", nullable = false)
	private int validationRetryCount;

	/**
	 * The variable that holds the time at which the OTP was generated.
	 */
	@Column(name = "generated_dtimes")
	private LocalDateTime generatedDtimes;

	/**
	 * The variable that holds the status of the OTP.
	 */
	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "ref_id")
	private String refId;

	@Column(name = "ref_id_type")
	private String refIdType;

	@Column(name = "expiry_dtimes")
	private LocalDateTime expiryDTimes;

	@Column(name = "lang_code", length = 3)
	private String langCode;

	@Column(name = "cr_by")
	private String createdBy;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime crDtimes;

	@Column(name = "del_dtimes", nullable = false)
	private LocalDateTime delDtimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	/**
	 * The default constructor for OtpEntity.
	 */
	public OtpEntity() {
		generatedDtimes = LocalDateTime.now(ZoneId.of("UTC"));
		updatedDtimes = generatedDtimes;
		statusCode = OtpStatusConstants.UNUSED_OTP.getProperty();
	}
}
