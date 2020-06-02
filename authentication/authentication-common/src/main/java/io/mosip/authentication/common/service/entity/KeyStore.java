package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for KeyStore
 * 
 * @author Nagarjuna
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "key_store",schema = "ida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyStore {

	/**
	 * The field alias
	 */
	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String alias;

	/**
	 * The field publicKey
	 */
	@Column(name = "public_key")
	private byte[] publicKey;

	/**
	 * The field privateKey
	 */
	@Column(name = "private_key")
	private byte[] privateKey;

	/**
	 * The field masterAlias
	 */
	@Column(name = "master_key")
	private String masterAlias;
	
	/**
	 * The field createdBy
	 */
	@Column(name = "cr_by", length = 256)
	private String createdBy;

	/**
	 * The field createdtimes
	 */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdtimes;

	/**
	 * The field updatedBy
	 */
	@Column(name = "upd_by", length = 256)
	private String updatedBy;

	/**
	 * The field updatedtimes
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtimes;

	/**
	 * The field isDeleted
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * The field deletedtimes
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedtimes;

}

