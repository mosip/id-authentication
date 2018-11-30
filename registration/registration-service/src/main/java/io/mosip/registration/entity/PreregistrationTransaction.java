package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Pre Registration Transaction entity
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "pre_registration_transaction", schema = "reg")
public class PreregistrationTransaction extends RegistrationCommonFields {

	@Id
	private String id;
	@Column(name = "pre_reg_id", length = 36, nullable = false)	
	private String preRegId;
	@Column(name = "reg_id", length = 36, nullable = true)
	private String regId;
	@Column(name = "file_path", length = 128, nullable = true)
	private String filePath;
	@Column(name = "session_key", length = 128, nullable = true)
	private String sessionKey;
	@Column(name = "status_code", length = 36, nullable = false)
	private String statusCode;
	@Column(name = "status_comments", length = 1024, nullable = true)
	private String statusComments;
	@Column(name = "lang_code", length = 3, nullable = false)
	private String langCode;
	@Column(name = "is_deleted", nullable = true)
	private Boolean isDeleted;
	@Column(name = "del_dtimes", nullable = true)
	private String delDtimes;
	
}
