/**
 * 
 */
package io.mosip.registration.processor.request.handler.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.request.handler.service.LostPacketService;
import io.mosip.registration.processor.request.handler.service.dto.LostRequestDto;
import io.mosip.registration.processor.request.handler.service.dto.LostResponseDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;

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

	@Override
	public LostResponseDto getIdValue(LostRequestDto lostRequestDto) throws RegBaseCheckedException {

		LostResponseDto lostResponseDto = null;
		String idValue = null;

		if (validator.isValidIdTypeForLost(lostRequestDto.getIdType())
				&& validator.isValidName(lostRequestDto.getName())
				&& validator.isValidPostalCode(lostRequestDto.getPostalCode())
				&& validator.isValidContactType(lostRequestDto.getContactType())
				&& validator.isValidContactValue(lostRequestDto.getContactValue())) {
			Set<DemographicInfoDto> matchedRidset = searchRid(lostRequestDto);
			if (matchedRidset == null || matchedRidset.isEmpty()) {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION,
						PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION.getMessage(), new Throwable());
			} else {
				if (lostRequestDto.getIdType().equalsIgnoreCase("RID")) {
					idValue = findRID(matchedRidset);
				} else {
					idValue = findUIN(matchedRidset);
				}
			}
		}
		return lostResponseDto;
	}

	private String findUIN(Set<DemographicInfoDto> matchedRidset) {
		// TODO Auto-generated method stub
		return null;
	}

	private String findRID(Set<DemographicInfoDto> matchedRidset) {
		// TODO Auto-generated method stub
		return null;
	}

	private Set<DemographicInfoDto> searchRid(LostRequestDto lostRequestDto) {

		return null;
	}

}
