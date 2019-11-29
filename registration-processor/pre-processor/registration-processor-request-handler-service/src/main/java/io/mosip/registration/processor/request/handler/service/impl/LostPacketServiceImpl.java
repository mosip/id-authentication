/**
 * 
 */
package io.mosip.registration.processor.request.handler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.request.handler.service.LostPacketService;
import io.mosip.registration.processor.request.handler.service.dto.LostRequestDto;
import io.mosip.registration.processor.request.handler.service.dto.LostResponseDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * @author M1022006
 *
 */
@Service
public class LostPacketServiceImpl implements LostPacketService {

	/** The validator. */
	@Autowired
	private RequestHandlerRequestValidator validator;

	/** The packet info dao. */
	@Autowired
	private PacketInfoDao packetInfoDao;

	/** The Constant VID. */
	private static final String EMAIL = "Email";

	/** The utilities. */
	@Autowired
	private Utilities utilities;

	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Override
	public LostResponseDto getIdValue(LostRequestDto lostRequestDto) throws RegBaseCheckedException {

		LostResponseDto lostResponseDto = null;
		String idValue = null;

		if (validator.isValidIdTypeForLost(lostRequestDto.getIdType())
				&& validator.isValidName(lostRequestDto.getName())
				&& validator.isValidPostalCode(lostRequestDto.getPostalCode())
				&& validator.isValidContactType(lostRequestDto.getContactType())
				&& validator.isValidContactValue(lostRequestDto.getContactValue())) {
			List<String> matchedRidList = searchRid(lostRequestDto);
			if (matchedRidList == null || matchedRidList.isEmpty()) {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION,
						PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION.getMessage(), new Throwable());
			} else {
				if (lostRequestDto.getIdType().equalsIgnoreCase("RID")) {
					idValue = findRID(matchedRidList);

				} else {
					idValue = findUIN(matchedRidList);
				}
			}
		}
		return lostResponseDto;
	}

	private String findUIN(List<String> matchedRidList) throws RegBaseCheckedException {
		String uin = null;
		try {
			if (matchedRidList.size() == 1) {
				JSONObject jsonObject = utilities.retrieveUIN(matchedRidList.get(0));
				Long value = JsonUtil.getJSONValue(jsonObject, IdType.UIN.toString());
				if (value == null) {
					throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION,
							PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION.getMessage(), new Throwable());
				}
				uin = value.toString();

			} else {
				uin = getUinForMultipleRids(matchedRidList);
			}
			return uin;
		} catch (ApisResourceAccessException e) {

		} catch (IdRepoAppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String findRID(List<String> matchedRidList) {
		if (matchedRidList.size() == 1) {
			return matchedRidList.get(0);
		} else {
			String rid = null;
			List<InternalRegistrationStatusDto> registrationStatusList = registrationStatusService
					.getByIdsAndTimestamp(matchedRidList);
			for (InternalRegistrationStatusDto internalRegistrationStatusDto : registrationStatusList) {
				if (RegistrationStatusCode.PROCESSED.toString().equals(internalRegistrationStatusDto.getStatusCode())) {
					rid = internalRegistrationStatusDto.getRegistrationId();
					break;
				}
			}
			if (rid == null && registrationStatusList != null && !registrationStatusList.isEmpty()) {
				rid = registrationStatusList.get(registrationStatusList.size() - 1).getRegistrationId();
			}
			return rid;
		}

	}

	private List<String> searchRid(LostRequestDto lostRequestDto) {
		Set<String> matchedRidset = new HashSet<String>();
		List<DemographicInfoDto> matchedDemographicInfoDtoList = new ArrayList<DemographicInfoDto>();
		String hashedName = getHMACHashCode(lostRequestDto.getName());
		String hashedPostalCode = getHMACHashCode(lostRequestDto.getPostalCode());

		if (EMAIL.equalsIgnoreCase(lostRequestDto.getContactType())) {
			String hashedEmail = getHMACHashCode(lostRequestDto.getContactValue());
			matchedDemographicInfoDtoList
					.addAll(packetInfoDao.getMatchedDemographicDtosByEmail(hashedName, hashedPostalCode, hashedEmail));

		} else {
			String hashedPhone = getHMACHashCode(lostRequestDto.getContactValue());
			matchedDemographicInfoDtoList
					.addAll(packetInfoDao.getMatchedDemographicDtosByPhone(hashedName, hashedPostalCode, hashedPhone));
		}
		for (DemographicInfoDto DemographicInfoDto : matchedDemographicInfoDtoList) {
			matchedRidset.add(DemographicInfoDto.getRegId());
		}
		return matchedRidset.stream().collect(Collectors.toList());

	}

	public static String getHMACHashCode(String value) {
		if (value == null)
			return null;
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(value.getBytes()));

	}

	public String getUinForMultipleRids(List<String> matchedRidList)
			throws RegBaseCheckedException, ApisResourceAccessException, IOException {
		String uin = null;
		Set<String> uinSet = new HashSet<String>();
		for (String regId : matchedRidList) {
			JSONObject jsonObject = utilities.retrieveUIN(regId);
			Long value = JsonUtil.getJSONValue(jsonObject, IdType.UIN.toString());
			if (value != null) {
				uinSet.add(value.toString());
			}
		}
		if (uinSet.isEmpty()) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION,
					PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION.getMessage(), new Throwable());
		} else if (uinSet.size() == 1) {
			Optional<String> value = uinSet.stream().findFirst();
			uin = value.toString();
		} else {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_MULTIPLE_RECORDS_EXCEPTION,
					PlatformErrorMessages.RPR_PGS_MULTIPLE_RECORDS_EXCEPTION.getMessage(), new Throwable());
		}

		return uin;
	}
}
