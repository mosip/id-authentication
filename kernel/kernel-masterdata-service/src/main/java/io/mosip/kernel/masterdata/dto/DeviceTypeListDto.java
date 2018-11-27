package io.mosip.kernel.masterdata.dto;


import antlr.collections.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeListDto {
	private List<DeviceTypeDto> deviceTypeDtos;

}
