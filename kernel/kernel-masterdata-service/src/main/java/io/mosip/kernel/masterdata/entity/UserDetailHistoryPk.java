package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserDetailHistoryPk implements Serializable {

	private static final long serialVersionUID = 4120860305322977568L;

	@Column(name = "id", unique = true, nullable = false, length = 36)
	private String id;

	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effDTimes;

}
