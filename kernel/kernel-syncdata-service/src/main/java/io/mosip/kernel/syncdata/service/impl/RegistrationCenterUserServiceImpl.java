package io.mosip.kernel.syncdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.syncdata.constant.RegistrationCenterUserErrorCode;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserDto;
import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.exception.DataNotFoundException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.syncdata.service.RegistrationCenterUserService;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * This class contains the business logic for CRUD opertaion
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Service
public class RegistrationCenterUserServiceImpl implements RegistrationCenterUserService {

	@Autowired
	RegistrationCenterUserRepository registrationCenterUserRepo;

	@Override
	public RegistrationCenterUserResponseDto getUsersBasedOnRegistrationCenterId(String regCenterId) {
		List<RegistrationCenterUser> registrationCenterUsers = null;
		List<RegistrationCenterUserDto> registrationCenterUserDtos = null;
		RegistrationCenterUserResponseDto registrationCenterUserResponseDto = new RegistrationCenterUserResponseDto();
		try {
			registrationCenterUsers = registrationCenterUserRepo.findByRegistrationCenterUserByRegCenterId(regCenterId);
		} catch (DataAccessException ex) {
			throw new SyncDataServiceException(
					RegistrationCenterUserErrorCode.REGISTRATION_USER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterUserErrorCode.REGISTRATION_USER_FETCH_EXCEPTION.getErrorMessage());
		}

		if (registrationCenterUsers.isEmpty()) {
			throw new DataNotFoundException(
					RegistrationCenterUserErrorCode.REGISTRATION_USER_DATA_NOT_FOUND_EXCEPTION.getErrorCode(),
					RegistrationCenterUserErrorCode.REGISTRATION_USER_DATA_NOT_FOUND_EXCEPTION.getErrorMessage());
		}

		registrationCenterUserDtos = MapperUtils.mapAll(registrationCenterUsers, RegistrationCenterUserDto.class);
		registrationCenterUserResponseDto.setRegistrationCenterUsers(registrationCenterUserDtos);
		return registrationCenterUserResponseDto;
	}

}
