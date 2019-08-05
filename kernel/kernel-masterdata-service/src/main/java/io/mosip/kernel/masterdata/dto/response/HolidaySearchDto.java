package io.mosip.kernel.masterdata.dto.response;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
@ApiModel(value = "Holiday", description = "Holiday Detail resource")
public class HolidaySearchDto extends BaseDto {

	@NotNull
	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	private int holidayId;

	@Size(min = 1, max = 36)
	@NotBlank
	private String names;

	@NotNull
	@FilterType(types = { FilterTypeEnum.BETWEEN, FilterTypeEnum.EQUALS })
	private LocalDate holidayDate;

	@NotBlank
	@Size(min = 1, max = 64)
	@FilterType(types = { FilterTypeEnum.EQUALS, FilterTypeEnum.STARTSWITH, FilterTypeEnum.CONTAINS })
	private String holidayName;

	@Size(min = 0, max = 128)
	private String holidayDesc;

	@Size(min = 1, max = 3)
	@ValidLangCode
	@NotBlank
	private String langCode;

}
