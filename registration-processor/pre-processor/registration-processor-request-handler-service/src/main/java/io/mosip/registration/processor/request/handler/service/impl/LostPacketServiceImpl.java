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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
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
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class LostPacketServiceImpl.
 *
 * @author M1022006
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

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private static Logger regProcLogger = RegProcessorLogger.getLogger(LostPacketServiceImpl.class);

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.request.handler.service.LostPacketService#
	 * getIdValue(io.mosip.registration.processor.request.handler.service.dto.
	 * LostRequestDto)
	 */
	@Override
	public LostResponseDto getIdValue(LostRequestDto lostRequestDto) throws RegBaseCheckedException {

		LostResponseDto lostResponseDto = null;
		String idValue = null;

		LogDescription description = new LogDescription();
		boolean isTransactionSuccessful = false;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::getIdValue()::entry");

		try {
			if (validator.isValidIdTypeForLost(lostRequestDto.getIdType(), description)
					&& validator.isValidName(lostRequestDto.getName(), description)
					&& validator.isValidPostalCode(lostRequestDto.getPostalCode(), description)
					&& validator.isValidContactType(lostRequestDto.getContactType(), description)
					&& validator.isValidContactValue(lostRequestDto.getContactValue(), description)) {
				List<String> matchedRidList = searchRid(lostRequestDto);
				if (matchedRidList == null || matchedRidList.isEmpty()) {
					description.setMessage(PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION.getMessage());
					description.setCode(PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION.getCode());
					throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION,
							PlatformErrorMessages.RPR_PGS_NO_RECORDS_EXCEPTION.getMessage(), new Throwable());

				} else {
					if (lostRequestDto.getIdType().equalsIgnoreCase("RID")) {

						idValue = findRID(matchedRidList);
						lostResponseDto = new LostResponseDto();
						lostResponseDto.setIdValue(idValue);
						isTransactionSuccessful = true;
						description.setMessage(
								PlatformSuccessMessages.RPR_REQUEST_HANDLER_LOST_PACKET_SUCCESS.getMessage());

					} else {

						idValue = findUIN(matchedRidList);
						lostResponseDto = new LostResponseDto();
						lostResponseDto.setIdValue(idValue);
						isTransactionSuccessful = true;
						description.setMessage(
								PlatformSuccessMessages.RPR_REQUEST_HANDLER_LOST_PACKET_SUCCESS.getMessage());
					}
				}
			}
		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			/** Module-Id can be Both Success/Error code */
			String moduleId = isTransactionSuccessful
					? PlatformSuccessMessages.RPR_REQUEST_HANDLER_LOST_PACKET_SUCCESS.getCode()
					: description.getCode();
			String moduleName = ModuleName.REQUEST_HANDLER.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					moduleId, moduleName, "");
		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::getIdValue()::exit");
		return lostResponseDto;
	}

	/**
	 * Find UIN.
	 *
	 * @param matchedRidList
	 *            the matched rid list
	 * @return the string
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private String findUIN(List<String> matchedRidList) throws RegBaseCheckedException {
		String uin = null;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::findUIN()::entry");
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
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "LostPacketServiceImpl ::findUIN()::exit");
			return uin;
		} catch (ApisResourceAccessException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_API_RESOURCE_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_PGS_API_RESOURCE_EXCEPTION.getMessage(), e.getCause());
		} catch (IdRepoAppException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_IDENTITY_NOT_FOUND,
					PlatformErrorMessages.RPR_PGS_IDENTITY_NOT_FOUND.getMessage(), e.getCause());
		} catch (IOException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_IO_EXCEPTION,
					PlatformErrorMessages.RPR_PGS_IO_EXCEPTION.getMessage(), e.getCause());
		}

	}

	/**
	 * Find RID.
	 *
	 * @param matchedRidList
	 *            the matched rid list
	 * @return the string
	 */
	private String findRID(List<String> matchedRidList) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::findRID()::entry");
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

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "LostPacketServiceImpl ::findRID()::exit");
			return rid;
		}

	}

	/**
	 * Search rid.
	 *
	 * @param lostRequestDto
	 *            the lost request dto
	 * @return the list
	 */
	private List<String> searchRid(LostRequestDto lostRequestDto) {

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::searchRid()::entry");
		Set<String> matchedRidset = new HashSet<String>();
		List<DemographicInfoDto> matchedDemographicInfoDtoList = new ArrayList<DemographicInfoDto>();
		String hashedName = getHMACHashCode(lostRequestDto.getName().trim().toUpperCase());
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
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::searchRid()::exit");
		return matchedRidset.stream().collect(Collectors.toList());

	}

	/**
	 * Gets the HMAC hash code.
	 *
	 * @param value
	 *            the value
	 * @return the HMAC hash code
	 */
	public static String getHMACHashCode(String value) {
		if (value == null)
			return null;
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(value.getBytes()));

	}

	/**
	 * Gets the uin for multiple rids.
	 *
	 * @param matchedRidList
	 *            the matched rid list
	 * @return the uin for multiple rids
	 * @throws RegBaseCheckedException
	 *             the reg base checked
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String getUinForMultipleRids(List<String> matchedRidList)
			throws RegBaseCheckedException, ApisResourceAccessException, IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::getUinForMultipleRids()::entry");
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
			uin = value.get();
		} else {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_MULTIPLE_RECORDS_EXCEPTION,
					PlatformErrorMessages.RPR_PGS_MULTIPLE_RECORDS_EXCEPTION.getMessage(), new Throwable());
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"LostPacketServiceImpl ::getUinForMultipleRids()::exit");
		return uin;
	}
}
