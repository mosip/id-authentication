package io.mosip.registration.service;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.DeviceDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserMachineMappingDTO;

/**
 * User Client Machine Mapping Service
 * @author YASWANTH S
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
	 * 
	 * @return the all devicetypes as UI response
	 */
	List<String> getAllDeviceTypes();

	/**
	 * it makes the DeviceDTO out of regcenterdevice
	 * 
	 * @param centerId
	 * @return DeviceDTO
	 *//*

	List<DeviceDTO> getAllDeviceBasedOnCenterId(String centerId);*/
	/**
	 * it makes the DeviceDTO out of regcentermachinedevice
	 * @param centerId
	 * @param machineId
	 * @return
	 */
	Map<String,List<DeviceDTO>> getDeviceMappingList(String centerId,String machineId);
	
	ResponseDTO updateMappedDevice(List<DeviceDTO > deletedList,List<DeviceDTO> addedList);



}
