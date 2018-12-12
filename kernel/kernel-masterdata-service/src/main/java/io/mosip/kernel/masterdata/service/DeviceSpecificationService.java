package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to fetch and save Device Specification
 * Details
 * 
 * @author Uday Kumar
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface DeviceSpecificationService {
	/**
	 * This abstract method to fetch Device Specification Details for given language
	 * code
	 *
	 * @param languageCode
	 *            Language code given by user
	 * @return List
	 * 			 Device Specification Details for given language code
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving device Specification
	 * @throws DataNotFoundException
	 *             if no Device Specification found
	 *
	 */
	public List<DeviceSpecificationDto> findDeviceSpecificationByLangugeCode(String languageCode);

	/**
	 * This abstract method to fetch Device Specification Details for given language
	 * code and device Type Code
	 * 
	 * @param languageCode
	 *            Language Code given by user
	 * @param deviceTypeCode
	 *            DeviceTypeCode given by user
	 * @return List 
	 * 			Device Specification Details for given language code and deviceTypeCode
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving device Specification
	 * @throws DataNotFoundException
	 *             if no Device Specification found
	 *
	 */
	public List<DeviceSpecificationDto> findDeviceSpecByLangCodeAndDevTypeCode(String languageCode,
			String deviceTypeCode);

	/**
	 * Function to save Device Specification Details to the Database
	 * 
	 * @param deviceSpecification
	 *        input from user DeviceSpecification DTO
	 * 
	 * @return IdResponseDto
	 *        Device Specification ID which is successfully inserted
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving device Specification
	 */
	public IdResponseDto createDeviceSpecification(RequestDto<DeviceSpecificationDto> deviceSpecification);

}
