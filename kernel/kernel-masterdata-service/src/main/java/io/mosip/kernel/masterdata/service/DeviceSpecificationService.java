package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceSpecPostResponseDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationRequestDto;

/**
 * This interface has abstract methods to fetch and save Device Specification
 * Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface DeviceSpecificationService {
	/**
	 * This abstract method to fetch Device Specification Details for given language
	 * code
	 *
	 * @param langCode
	 *            Language code given by user
	 * @return List<DeviceSpecificationDto> Device Specification Details for given
	 *         language code
	 *
	 */
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCode(String languageCode);

	/**
	 * This abstract method to fetch Device Specification Details for given language
	 * code and device Type Code
	 * 
	 * @param langCode
	 *            Language Code given by user
	 * @param deviceTypeCode
	 *            DeviceTypeCode given by user
	 * @return List<DeviceSpecificationDto> Device Specification Details for given
	 *         language code and deviceTypeCode
	 *
	 */
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCodeAndDeviceTypeCode(String languageCode,
			String deviceTypeCode);

	/**
	 * Function to save Device Specification Details to the Database
	 * 
	 * @param deviceTypes
	 * 
	 * @return {@link DeviceSpecificationDto}
	 */
	public DeviceSpecPostResponseDto addDeviceSpecifications(DeviceSpecificationRequestDto deviceSpecification);

}
