package io.mosip.kernel.masterdata.dto.getresponse.extn;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ZoneExtnDto extends BaseDto {

	private String code;

	private String langCode;

	private String name;

	private short hierarchyLevel;

	private String hierarchyName;

	private String parentZoneCode;

	private String hierarchyPath;
}
