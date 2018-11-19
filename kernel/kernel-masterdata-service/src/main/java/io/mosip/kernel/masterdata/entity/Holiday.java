package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

//github.com/mosip/mosip.git

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 24-10-2018
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loc_holiday", schema = "master")
public class Holiday implements Serializable {

	private static final long serialVersionUID = 1329042436883315822L;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "id", nullable = false, length = 36)),
			@AttributeOverride(name = "locationCode", column = @Column(name = "location_code", nullable = false, length = 36)),
			@AttributeOverride(name = "holidayDate", column = @Column(name = "holiday_date", nullable = false, length = 36)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 36)) })
	private HolidayId holidayId;


	@Column(name = "holiday_name", nullable = false, length = 64)
	private String holidayName;

	@Column(name = "holiday_desc", length = 128)
	private String holidayDesc;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(name = "cr_by", nullable = false, length = 24)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdtime;

	@Column(name = "upd_by", length = 24)
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedtime;

}
