package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeCodeAndLanguageCode {
	
	private String id;
	private String deviceTypeCode;
	private String langCode;
	

}
