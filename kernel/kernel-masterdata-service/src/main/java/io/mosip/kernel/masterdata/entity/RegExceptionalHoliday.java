package io.mosip.kernel.masterdata.entity;



import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.RegExceptionalHolidayID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "reg_exceptional_holiday",schema="master")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RegExceptionalHolidayID.class)
public class RegExceptionalHoliday extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1510743278106214553L;

	@Id
	@Column(name="regcntr_id",nullable=false,length=10)
	private String registrationCenterId;
	
	@Id
	@Column(name="hol_date", nullable=false)
	private LocalDate exceptionHolidayDate;
	
	@Column(name="hol_name", nullable=false, length=128)
	private String exceptionHolidayName;
	
	@Column(name="hol_reason", nullable=false, length=256)
	private String exceptionHolidayReson;
	
	@Column(name="lang_code",nullable=false,length=3)
	private String langCode;
	
	
}
