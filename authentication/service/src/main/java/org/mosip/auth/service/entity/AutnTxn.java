package org.mosip.auth.service.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 
 * @author Rakesh Roshan
 */
@Data
@Table(name = "uin", schema = "ida")
public class AutnTxn implements Serializable {

	private static final long serialVersionUID = 7106100772059714728L;

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

	@Pattern(regexp = "[A-Za-z0-9] {16}")
	@Column(name = "status_code")
	private String statusCode;

	@Pattern(regexp = "[A-Za-z0-9]")
	@Size(min = 10, max = 256)
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
