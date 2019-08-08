package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.DeviceRegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceLangCodeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.DeviceResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.DeviceSearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to fetch and save a Device Details
 * 
 * @author Megha Tanga
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */

public interface DeviceService {

	/**
	 * This abstract method to fetch all Devices details
	 * 
	 * @param langCode
	 *            language code from user
	 * @return DeviceResponseDto Returning all Devices Details
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving device
	 * @throws DataNotFoundException
	 *             if no Device found
	 *
	 */
	public DeviceResponseDto getDeviceLangCode(String langCode);

	/**
	 * This abstract method to fetch Devices details for given Language code and
	 * DeviceType Code
	 * 
	 * @param langCode
	 *            language code from user
	 * @param devideTypeCode
	 *            devideTypeCode from user
	 * @return DeviceLangCodeResponseDto Returning all Devices Details for given
	 *         Language code and DeviceType Code {@link DeviceLangCodeResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurs while retrieving device
	 * 
	 * @throws DataNotFoundException
	 *             if no Device found
	 *
	 */
	public DeviceLangCodeResponseDto getDeviceLangCodeAndDeviceType(String langCode, String devideTypeCode);

	/**
	 * This method is used to add a new Device to master data
	 * 
	 * @param deviceRequestDto
	 *            Device DTO to insert data
	 * @return IdResponseDto Device ID which is successfully inserted
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Device
	 */
	public IdAndLanguageCodeID createDevice(DeviceDto deviceRequestDto);

	// public IdAndLanguageCodeID createInactiveDevice(DeviceDto deviceRequestDto);

	/**
	 * This method is used to update an existing Device to master data
	 * 
	 * @param deviceRequestDto
	 *            Device DTO to update data
	 * @return IdResponseDto Device ID which is successfully updated
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while updating Device
	 */
	public IdAndLanguageCodeID updateDevice(DeviceDto deviceRequestDto);

	/**
	 * This method is used to delete an existing Device of master data
	 * 
	 * @param id
	 *            Device id to delete data
	 * @return IdResponseDto Device ID which is successfully deleted
	 *         {@link IdResponseDto}
	 * @throws MasterDataServiceException
	 *             if any error occurred while deleting Device
	 */
	public IdResponseDto deleteDevice(String id);

	/**
	 * Fetch all Device which are mapped with the given registration center Id
	 * 
	 * @param regCenterId
	 *            pass Registration center id as String
	 * @return DeviceRegistrationCenterDto List of DeviceRegistrationCenterDto which
	 *         has the list of Device mapped with given Registration Center id
	 *         {@link DeviceRegistrationCenterDto}
	 */
	public PageDto<DeviceRegistrationCenterDto> getDevicesByRegistrationCenter(String regCenterId, int page, int size,
			String orderBy, String direction);

	/**
	 * Method to search Devices based on filters provided.
	 * 
	 * @param searchRequestDto
	 *            the search DTO.
	 * @return the {@link PageResponseDto}.
	 */
	public PageResponseDto<DeviceSearchDto> searchDevice(SearchDto searchRequestDto);

	/**
	 * Method to filter Device based on column and type provided.
	 * 
	 * @param filterValueDto
	 *            the filter DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	public FilterResponseDto deviceFilterValues(FilterValueDto filterValueDto);

	/**
	 * Method to decommission device
	 * 
	 * @param deviceId
	 *            input from user
	 * @return device ID of decommissioned device
	 */
	public IdResponseDto decommissionDevice(String deviceId);
}
