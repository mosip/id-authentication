package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

//github.com/mosip/mosip.git

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.HolidayID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 24-10-2018
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loc_holiday", schema = "master")
@NamedQueries({
	@NamedQuery(name="Holiday.updateHoliday",query="UPDATE Holiday h SET h.isActive = :isActive ,h.updatedBy = :updatedBy , h.updatedDateTime = :updatedDateTime, h.holidayDesc = :holidayDesc,h.holidayId.holidayDate=:newHolidayDate,h.holidayId.holidayName = :newHolidayName , h.holidayId.langCode = :langCode  WHERE h.holidayId.locationCode = :locationCode and h.holidayId.holidayName = :holidayName and h.holidayId.holidayDate = :holidayDate and h.holidayId.langCode = :langCode and (h.isDeleted is null or h.isDeleted = false)")
})
public class Holiday extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1329042436883315822L;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "locationCode", column = @Column(name = "location_code", nullable = false, length = 36)),
			@AttributeOverride(name = "holidayDate", column = @Column(name = "holiday_date", nullable = false)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)),
			@AttributeOverride(name = "holidayName", column = @Column(name = "holiday_name", nullable = false, length = 64)) })
	private HolidayID holidayId;

	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "holiday_desc", length = 128)
	private String holidayDesc;

}
