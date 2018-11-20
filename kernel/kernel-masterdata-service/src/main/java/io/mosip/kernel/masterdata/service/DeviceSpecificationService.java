package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceCreateSpecificationResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;

public interface DeviceSpecificationService {
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCode(String languageCode);

	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(String languageCode,
			String deviceTypeCode);
	
	public DeviceCreateSpecificationResponseDto addDeviceSpecification(List<DeviceSpecification> deviceSpecification);

}
