package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.ExcptionalHolidayId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Kishan Rathore
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reg_exceptional_holiday", schema = "master")
@IdClass(ExcptionalHolidayId.class)
public class ExceptionalHoliday extends BaseEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 3849193075880488107L;

	@Id
	@Column(name = "regcntr_id", nullable = false, length = 10)
	private String registrationCenterId;

	@Id
	@Column(name = "hol_date", nullable = false)
	private LocalDate holidayDate;

	@Column(name = "hol_name", length = 128)
	private String holidayName;

	@Column(name = "hol_reason", length = 256)
	private String holidayReason;

	@Column(name = "lang_code", length = 3)
	private String langCode;

}
