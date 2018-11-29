package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayID implements Serializable {

	private static final long serialVersionUID = -1631873932622755759L;

	@Column(name = "id", unique = true, nullable = false)
	private int id;

	@Column(name = "location_code", nullable = false, length = 36)
	private String locationCode;

	@Column(name = "holiday_date", nullable = false)
	private LocalDate holidayDate;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
}
