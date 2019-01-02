package io.mosip.kernel.masterdata.entity.id;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RegistrationCenterHistoryID implements Serializable {

	private static final long serialVersionUID = -8541947587557590379L;

	@Column(name = "id",nullable = false, length = 36)
	private String id;

	@Column(name = "eff_dtimes",nullable = false)
	private LocalDateTime effectivetimes;
}