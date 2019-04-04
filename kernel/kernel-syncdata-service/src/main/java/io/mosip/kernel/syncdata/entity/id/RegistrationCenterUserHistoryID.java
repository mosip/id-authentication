package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RegistrationCenterUserHistoryID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1432161975437589419L;

	@Column(name = "regcntr_id", nullable = false)
	private String regCntrId;

	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectDateTimes;

	@Column(name = "usr_id", nullable = false, length = 3)
	private String userId;

}
