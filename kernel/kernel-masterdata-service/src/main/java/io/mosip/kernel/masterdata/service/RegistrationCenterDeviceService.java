package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * RegistrationCenterDeviceService interface provide methods used to create
 * mapping between registration center id and device id.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */

public interface RegistrationCenterDeviceService {

	/**
	 * This method is used to create mapping between Registration center and Device.
	 * 
	 * @param requestDto
	 *            this object must contains registration center id and device. id
	 *            with status.
	 * 
	 * @return {@link ResponseRegistrationCenterDeviceDto} which contains the mapped
	 *         registration center id and device id.
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurs while mapping registration center id and
	 *             device id. Like if registration center id or device id is not
	 *             valid or not present in database.
	 */
	public ResponseRegistrationCenterDeviceDto createRegistrationCenterAndDevice(
			RequestDto<RegistrationCenterDeviceDto> requestDto);

}
