package io.mosip.dbentity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Data
@Entity
@Table(name = "oauth_access_token", schema = "iam")
public class AccessToken {

	@Column(name = "auth_token")
	private String authToken;
	
	@Id
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "refresh_Token")
	private String refreshToken;
	
	@Column(name = "expiration_time")
	private long expirationTime;
	
	@Column(name = "is_active", nullable = false)
	private long active;
	
	@Column(name = "cr_by", nullable = false)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;
	
}
