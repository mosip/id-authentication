package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.RegistrationCenterDeviceErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserErrorCode;
import io.mosip.kernel.masterdata.dto.UserAndRegCenterMappingResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUser;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserHistoryPk;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.ZoneUser;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterUserID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.masterdata.repository.ZoneUserRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterUserService;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

/**
 * 
 * @author Megha Tanga
 *
 */

@Service
public class RegistrationCenterUserServiceImpl implements RegistrationCenterUserService {

	@Autowired
	RegistrationCenterUserRepository registrationCenterUserRepository;

	@Autowired
	RegistrationCenterRepository registrationCenterRepository;

	@Autowired
	RegistrationCenterUserHistoryRepository registrationCenterUserHistoryRepository;

	@Autowired
	ZoneUserRepository zoneUserRepository;

	@Autowired
	ZoneUtils zoneUtils;

	@Value("${mosip.primary-language}")
	private String primaryLang;

	@Transactional
	@Override
	public UserAndRegCenterMappingResponseDto unmapUserRegCenter(String userId, String regCenterId) {

		UserAndRegCenterMappingResponseDto responseDto = new UserAndRegCenterMappingResponseDto();
		try {

			// find given User id and registration center are in DB or not
			RegistrationCenterUser registrationCenterUser = registrationCenterUserRepository
					.findByUserIdAndRegCenterId(userId, regCenterId);
			if (registrationCenterUser != null) {

				List<String> zoneIds;
				// get user zone and child zones list
				List<Zone> userZones = zoneUtils.getUserZones();
				zoneIds = userZones.parallelStream().map(Zone::getCode).collect(Collectors.toList());

				List<String> zoneUsers;
				// get given user id zone
				List<ZoneUser> zoneUserList = zoneUserRepository.findByIdAndLangCode(userId, primaryLang);
				zoneUsers = zoneUserList.parallelStream().map(ZoneUser::getZoneCode).collect(Collectors.toList());
				
				// check the given user zones will come under access user zone
				if (!(zoneIds.containsAll(zoneUsers))) {
					throw new RequestException(RegistrationCenterDeviceErrorCode.INVALIDE_ZONE.getErrorCode(),
							RegistrationCenterDeviceErrorCode.INVALIDE_ZONE.getErrorMessage());
				}


				// get given registration center zone id
				RegistrationCenter regCenterZone = registrationCenterRepository.findByLangCodeAndId(regCenterId,
						primaryLang);
				// check the given registration center zones will come under user zone
				if (!(zoneIds.contains(regCenterZone.getZoneCode()))) {
					throw new RequestException(RegistrationCenterUserErrorCode.INVALIDE_ZONE.getErrorCode(),
							RegistrationCenterUserErrorCode.INVALIDE_ZONE.getErrorMessage());
				}

				
				// todo

				if (!registrationCenterUser.getIsActive()) {
					throw new RequestException(
							RegistrationCenterUserErrorCode.REGISTRATION_CENTER_USER_ALREADY_UNMAPPED_EXCEPTION
									.getErrorCode(),
									RegistrationCenterUserErrorCode.REGISTRATION_CENTER_USER_ALREADY_UNMAPPED_EXCEPTION
									.getErrorMessage());
				}
				if (registrationCenterUser.getIsActive()) {
					RegistrationCenterUser updRegistrationCenterUser;

					registrationCenterUser.setIsActive(false);
					registrationCenterUser.setUpdatedBy(MetaDataUtils.getContextUser());
					registrationCenterUser.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));

					updRegistrationCenterUser = registrationCenterUserRepository.update(registrationCenterUser);

					// ----------------update history-------------------------------

					RegistrationCenterUserHistory registrationCenterUserHistory = new RegistrationCenterUserHistory();
					MapperUtils.map(updRegistrationCenterUser, registrationCenterUserHistory);
					MapperUtils.setBaseFieldValue(updRegistrationCenterUser, registrationCenterUserHistory);

					registrationCenterUserHistory.setRegistrationCenterUserHistoryPk(MapperUtils.map(
							new RegistrationCenterUserID(regCenterId, userId), RegistrationCenterUserHistoryPk.class));
				

					registrationCenterUserHistory.getRegistrationCenterUserHistoryPk()
							.setEffectivetimes(updRegistrationCenterUser.getUpdatedDateTime());
					registrationCenterUserHistory.setUpdatedDateTime(updRegistrationCenterUser.getUpdatedDateTime());
					registrationCenterUserHistoryRepository.create(registrationCenterUserHistory);

					// set success response
					responseDto.setStatus(MasterDataConstant.UNMAPPED_SUCCESSFULLY);
					responseDto.setMessage(
							String.format(MasterDataConstant.USER_AND_REGISTRATION_CENTER_UNMAPPING_SUCCESS_MESSAGE,
									userId, regCenterId));

				}

			} else {
				throw new RequestException(
						RegistrationCenterUserErrorCode.USER_AND_REG_CENTER_MAPPING_NOT_FOUND_EXCEPTION
								.getErrorCode(),
						String.format(
								RegistrationCenterUserErrorCode.USER_AND_REG_CENTER_MAPPING_NOT_FOUND_EXCEPTION
										.getErrorMessage(),
								userId, regCenterId));
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterUserErrorCode.REGISTRATION_CENTER_USER_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterUserErrorCode.REGISTRATION_CENTER_USER_FETCH_EXCEPTION.getErrorMessage());
		}
		return responseDto;
	}

}
