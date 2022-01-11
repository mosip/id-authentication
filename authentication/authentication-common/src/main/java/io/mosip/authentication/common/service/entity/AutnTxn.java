package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class mapped with table "autn_txn" and used by {@link AutnTxnRepository}
 * to fetch records.
 * 
 * @author Rakesh Roshan
 */
@NoArgsConstructor
@Data
@Table(name = "auth_transaction", schema = "ida")
@Entity
public class AutnTxn {

	@Id
	@NotNull
	private String id;

	@NotNull
	@Column(name = "request_dtimes")
	private LocalDateTime requestDTtimes;

	@NotNull
	@Column(name = "response_dtimes")
	private LocalDateTime responseDTimes;

	@NotNull
	@Column(name = "request_trn_id")
	private String requestTrnId;

	@NotNull
	@Column(name = "auth_type_code")
	private String authTypeCode;

	@NotNull
	@Size(max = 36)
	@Column(name = "status_code")
	private String statusCode;

	@NotNull
	@Size(max = 1024)
	@Column(name = "status_comment")
	private String statusComment;
	
	@NotNull
	@Size(max = 3)
	@Column(name = "lang_code")
	private String langCode;


	@Column(name = "ref_id_type")
	private String refIdType;

	@Column(name = "ref_id")
	private String refId;
	

	@Column(name = "static_tkn_id")
	private String authTknId;

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
	
	@NotNull
	@Column(name = "token_id")
	private String token;
	
	@Column(name = "requested_entity_type")
	private String entitytype;
	
	@Column(name = "requested_entity_id")
	private String entityId;
	
	@Column(name = "requested_entity_name")
	private String entityName;
	
	@Column(name = "request_signature")
	private String requestSignature;
	
	@Column(name = "response_signature")
	private String responseSignature;
	
	/**
	 * The constructor used in retrieval of the specific fields.
	 * @param requestTrnId
	 * @param requestDTtimes
	 * @param authTypeCode
	 * @param statusCode
	 * @param statusComment
	 * @param refId
	 * @param entityName
	 */
	public AutnTxn(String requestTrnId, LocalDateTime requestDTtimes, String authTypeCode, String statusCode,
			String statusComment, String refId, String refIdType, String entityName, String requestSignature,
			String responseSignature) {
		this.requestTrnId = requestTrnId;
		this.requestDTtimes = requestDTtimes; 
		this.authTypeCode = authTypeCode; 
		this.statusCode = statusCode; 
		this.statusComment = statusComment; 
		this.refId = refId;
		this.refIdType = refIdType; 
		this.entityName = entityName;
		this.requestSignature = requestSignature;
		this.responseSignature = responseSignature;
	}
	
	/**
	 * The constructor used in retrieval of the specific fields.
	 * 
	 * @param refId
	 * @param uinHash
	 */
	public AutnTxn(String token, String refIdType, String entityId) {
		this.token = token;
		this.refIdType = refIdType;
		this.entityId = entityId; 
	}
	
	
}
