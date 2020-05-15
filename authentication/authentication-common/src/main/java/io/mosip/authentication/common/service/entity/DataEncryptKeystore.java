package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author Manoj SP
 *
 */
@Entity
@Data
public class DataEncryptKeystore {

	@Id
	private Integer id;
	private String key;
	private String keyStatus;
	private String crBy;
	private LocalDateTime crDtimes;
	private String updBy;
	private LocalDateTime updDtimes;
}
