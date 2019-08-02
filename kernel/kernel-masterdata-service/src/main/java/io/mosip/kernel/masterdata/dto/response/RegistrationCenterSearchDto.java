package io.mosip.kernel.masterdata.dto.response;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.masterdata.dto.getresponse.extn.BaseDto;
import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import io.mosip.kernel.masterdata.validator.ValidLangCode;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Registration Centers", description = "Registration centers")
public class RegistrationCenterSearchDto extends BaseDto {

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@NotBlank
	@Size(min = 1, max = 10)
	private String id;

	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	@NotBlank
	@Size(min = 1, max = 128)
	private String name;

	@Size(min = 1, max = 36)
	@FilterType(types = { FilterTypeEnum.EQUALS })
	private String centerTypeCode;

	private String centerTypeName;

	@Size(min = 1, max = 256)
	private String addressLine1;

	@Size(min = 1, max = 256)
	private String addressLine2;

	@Size(min = 1, max = 256)
	private String addressLine3;

	@Size(min = 1, max = 32)
	private String latitude;

	@Size(min = 1, max = 32)
	private String longitude;

	@FilterType(types = { FilterTypeEnum.EQUALS })
	@NotBlank
	@Size(min = 1, max = 36)
	private String locationCode;

	@Size(min = 1, max = 36)
	private String holidayLocationCode;

	private String holidayLocation;

	@Size(min = 1, max = 16)
	private String contactPhone;

	@Size(min = 1, max = 32)
	private String workingHours;

	@ValidLangCode
	private String langCode;

	private Short numberOfKiosks;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime perKioskProcessTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerStartTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerEndTime;

	@Size(min = 1, max = 64)
	private String timeZone;

	@Size(min = 1, max = 128)
	private String contactPerson;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchStartTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchEndTime;

	private long devices;

	private long machines;

	private long users;

	private String province;

	private String provinceCode;

	private String region;

	private String regionCode;

	private String postalCode;

	private String administrativeZone;

	private String administrativeZoneCode;

	private String city;

	private String cityCode;

	private String zoneCode;

	private String zone;

}
