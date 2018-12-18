package io.mosip.registration.service.mapping;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.DeviceDTO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegistrationCenter;

/**
 * This service interface updates the mapping of users and devices to the
 * Registration Center Machine
 * 
 * @author YASWANTH S
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface MapMachineService {

	/**
	 * Update or save User Details in Machine Mapping
	 * @param userDto the details of user to update machine mapping
	 * @return responseDTO is UI response
	 */
	ResponseDTO saveOrUpdate(UserMachineMappingDTO userDTO);
	
	/**
	 * view list of Users to get mapping and already mapped
	 * @return responseDTO is UI response
	 */
	ResponseDTO view();
	
	/**
	 * Returns the active device types
	 * 
	 * @return the all active device types as UI response
	 */
	List<String> getAllDeviceTypes();

	/**
	 * Fetches valid devices associated or mapped to the registration center and
	 * devices associated or mapped to the registration center machine.
	 * 
	 * @param centerId
	 *            the id of {@link RegistrationCenter}
	 * @param machineId
	 *            the id of {@link MachineMaster}
	 * @return a map containing two lists. One list contains the devices mapped to
	 *         the given Registration Center Machine and another contains the
	 *         devices mapped to the given Registration Center excluding the devices
	 *         already mapped to given Registration Center Machine.
	 */
	Map<String, List<DeviceDTO>> getDeviceMappingList(String centerId, String machineId);

	/**
	 * Updates the devices unmapped from the registration center machine and the
	 * devices mapped to the registration center machine
	 * 
	 * @param deletedList
	 *            the list of devices unmapped from the registration center machine
	 * @param addedList
	 *            the list of devices mapped to the registration center machine
	 * @return the {@link ResponseDTO} object. If devices mapping is updated
	 *         successfully, the {@link SuccessResponseDTO} object will be
	 *         populated, else the {@link ErrorResponseDTO} object will be
	 *         populated.
	 */
	ResponseDTO updateMappedDevice(List<DeviceDTO> deletedList, List<DeviceDTO> addedList);

}
