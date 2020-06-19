package io.mosip.authentication.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

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
