package io.mosip.registration.service;

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

}
