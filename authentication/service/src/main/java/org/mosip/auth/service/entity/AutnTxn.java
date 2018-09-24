package org.mosip.auth.service.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Rakesh Roshan
 */
@Data
@Table(name = "autn_txn", schema = "ida")
@Entity
public class AutnTxn implements Serializable {

	private static final long serialVersionUID = 7106100772059714728L;

	@Id
	@NotNull
	private String id;

	@NotNull
	@Column(name = "request_dtimes")
	private Date requestDTtimes;

	@NotNull
	@Column(name = "response_dtimes")
	private Date responseDTimes;

	@NotNull
	@Column(name = "request_txn_id")
	private String requestTxnId;

	@NotNull
	@Column(name = "auth_type_code")
	private String authTypeCode;

	@NotNull
	@Size(max = 16)
	@Column(name = "status_code")
	private String statusCode;

	@NotNull
	@Size(max = 256)
	@Column(name = "status_comment")
	private String statusComment;

	@Column(name = "static_tkn_id")
	private String staticTknId;

	@Column(name = "uin")
	private String uin;

	@Column(name = "vid")
	private String vid;

	@NotNull
	@Column(name = "is_active")
	private String isActive;

	@NotNull
	@Column(name = "cr_by")
	private String crBy;

	@NotNull
	@Column(name = "cr_dtimes")
	private Date crDTimes;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	private Date updDTimes;

}
