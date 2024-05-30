package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Manoj SP
 *
 */
@Entity
@Data
@Table(schema = "ida", name = "data_encrypt_keystore")
public class DataEncryptKeystore {

	@Id
	private Integer id;
	@Column(name = "key")
	private String key;
	@Column(name = "key_status")
	private String keyStatus;
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;
	@Column(name = "upd_by")
	private String updBy;
	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;
}
