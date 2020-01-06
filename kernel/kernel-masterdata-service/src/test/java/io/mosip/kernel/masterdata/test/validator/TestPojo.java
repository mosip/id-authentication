package io.mosip.kernel.masterdata.test.validator;

import io.mosip.kernel.masterdata.validator.FilterType;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;
import lombok.Data;

@Data
public class TestPojo {
	@FilterType(types = { FilterTypeEnum.BETWEEN })
	private String column1;
	@FilterType(types = { FilterTypeEnum.CONTAINS })
	private String column2;
	@FilterType(types = { FilterTypeEnum.EQUALS })
	private String column3;
	@FilterType(types = { FilterTypeEnum.STARTSWITH })
	private String column4;
	private String column5;
}
