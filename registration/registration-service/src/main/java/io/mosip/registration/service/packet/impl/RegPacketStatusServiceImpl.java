package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.RegPacketStatusService;

/**
 * This class will update the packet status in the table after sync with the
 * server.
 * 
 * @author Himaja Dhanyamraju
 */
@Service
public class RegPacketStatusServiceImpl extends BaseService implements RegPacketStatusService {

	@Autowired
	private RegPacketStatusDAO regPacketStatusDAO;

	private static final Logger LOGGER = AppConfig.getLogger(RegPacketStatusServiceImpl.class);

	/**
	 * Get all registrationIDs for which the clientStatus is post-sync
	 *
	 * @return List<String> list of registrationId's required for packet status sync
	 *         with server
	 */
	private List<String> getPacketIds() {
		return regPacketStatusDAO.getPacketIdsByStatusUploaded();
	}

	/**
	 * update status for all packets that are synced with server
	 *
	 * @param registrations
	 *            list of registration entities which are represented as
	 *            LinkedHashMap which maps the attributes of registration entity to
	 *            their respective values that are obtained after sync with server
	 */
	private void updatePacketIdsByServerStatus(List<LinkedHashMap<String, String>> registrations)
			throws RegBaseUncheckedException {
		List<RegPacketStatusDTO> packetStatusDTO = new ArrayList<>();
		for (Map<String, String> registration : registrations) {
			packetStatusDTO.add(
					new RegPacketStatusDTO(registration.get(RegistrationConstants.PACKET_STATUS_SYNC_REGISTRATION_ID),
							registration.get(RegistrationConstants.PACKET_STATUS_SYNC_STATUS_CODE)));
		}
		/** update server and client status. */
		regPacketStatusDAO.updatePacketIdsByServerStatus(packetStatusDTO);
	}

	@SuppressWarnings("unchecked")
	public ResponseDTO packetSyncStatus() {

		LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"packet status sync called");

		List<LinkedHashMap<String, String>> registrations = new ArrayList<>();
		List<String> packetIds = getPacketIds();
		LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"PacketIds for sync with server have been retrieved");
		/** Create Response to Return to UI layer */
		ResponseDTO response = new ResponseDTO();
		SuccessResponseDTO successResponse;

		/** Validator response service API creation */
		final String SERVICE_NAME = RegistrationConstants.PACKET_STATUS_SYNC_SERVICE_NAME;

		/** prepare request params to pass through URI */
		Map<String, String> requestParamMap = new HashMap<>();
		String packetIdList = packetIds.stream().map(Object::toString).collect(Collectors.joining(","));
		requestParamMap.put(RegistrationConstants.PACKET_STATUS_SYNC_URL_PARAMETER, packetIdList);

		try {
			/** Obtain RegistrationStatusDTO from service delegate util */
			registrations = (List<LinkedHashMap<String, String>>) serviceDelegateUtil.get(SERVICE_NAME,
					requestParamMap);
			if (!registrations.isEmpty()) {
				/** update the status of packets after sync with server */
				try {
					updatePacketIdsByServerStatus(registrations);
				} catch (RegBaseUncheckedException regBaseUncheckedException) {
					getErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE);
				}
				LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
						"packet status has been synced with server");
				/** Create Success response */
				successResponse = new SuccessResponseDTO();
				successResponse.setCode(RegistrationConstants.ALERT_INFORMATION);
				successResponse.setMessage(RegistrationConstants.PACKET_STATUS_SYNC_SUCCESS_MESSAGE);
				Map<String, Object> otherAttributes = new HashMap<>();
				otherAttributes.put(RegistrationConstants.PACKET_STATUS_SYNC_RESPONSE_ENTITY, registrations);
				successResponse.setOtherAttributes(otherAttributes);
				response.setSuccessResponseDTO(successResponse);
				LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
						"Success Response Created");
			} else {
				/** Create Error response */
				getErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE);
				LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
						"Error Response Created");
			}

		} catch (SocketTimeoutException | RegBaseCheckedException | IllegalArgumentException
				| HttpClientErrorException exception) {
			/** Create Error response */
			getErrorResponse(response, RegistrationConstants.PACKET_STATUS_SYNC_ERROR_RESPONSE);
			LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
					"Error Response Created");
		}
		LOGGER.debug("REGISTRATION - PACKET - STATUS - SYNC", APPLICATION_NAME, APPLICATION_ID,
				"Packet Status Sync ended");

		return response;
	}

}
