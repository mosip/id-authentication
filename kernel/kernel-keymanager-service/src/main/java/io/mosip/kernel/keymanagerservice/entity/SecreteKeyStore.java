/*package io.mosip.kernel.keymanagerservice.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "dao_key_store", schema = "kernel")
public class SecreteKeyStore {

	@Id
	private String id;

	private String key;

	@Column(name = "is_expired")
	private Boolean expired;

	
	@Column(name = "key_gen_dtimes")
	private LocalDateTime genratedtimes;

	
	@Column(name = "key_expire_dtimes")
	private LocalDateTime expiryDate;

	@Column(name = "cr_by")
	private String createdBy;

	@Column(name = "cr_dtimes")
	private LocalDateTime createDateTime;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updateDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedtimes;

}
*/