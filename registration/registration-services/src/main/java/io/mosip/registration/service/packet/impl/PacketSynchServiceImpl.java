package io.mosip.registration.service.packet.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.RegistrationPacketSyncDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.security.AESEncryptionService;
import io.mosip.registration.service.sync.PacketSynchService;

/**
 * The PacketSynchServiceImpl class is to sync the packets to server.
 * 
 * @author Saravana Kumar
 */
@Service
public class PacketSynchServiceImpl extends BaseService implements PacketSynchService {

	@Autowired
	private RegistrationDAO syncRegistrationDAO;

	@Autowired
	protected AuditManagerService auditFactory;
	
	@Autowired
	private AESEncryptionService aesEncryptionService;

	private static final Logger LOGGER = AppConfig.getLogger(PacketSynchServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.impl.PacketSynchStatus#fetchPacketsToBeSynched(
	 * )
	 */

	/**
	 * This method is used to synch the local packets with the server.
	 *
	 * @param packetsToBeSynched 
	 * 				the packets to be synched
	 * @return the string
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 */
	@Override
	public String packetSync(List<PacketStatusDTO> packetsToBeSynched) throws RegBaseCheckedException {
		LOGGER.info("REGISTRATION - SYNCH_PACKETS_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Sync the packets to the server");
		String syncErrorStatus = "";
		try {
			auditFactory.audit(AuditEvent.UPLOAD_PACKET, Components.UPLOAD_PACKET,
					SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			List<SyncRegistrationDTO> syncDtoList = new ArrayList<>();
			List<PacketStatusDTO> synchedPackets = new ArrayList<>();
			ResponseDTO responseDTO = new ResponseDTO();
			if (!packetsToBeSynched.isEmpty()) {
				for (PacketStatusDTO packetToBeSynch : packetsToBeSynched) {
					SyncRegistrationDTO syncDto = new SyncRegistrationDTO();
					syncDto.setLangCode(String.valueOf(ApplicationContext.map().get(RegistrationConstants.PRIMARY_LANGUAGE)));
					syncDto.setRegistrationId(packetToBeSynch.getFileName());
					syncDto.setRegistrationType(packetToBeSynch.getPacketStatus().toUpperCase());
					syncDto.setPacketHashValue(packetToBeSynch.getPacketHash());
					syncDto.setPacketSize(packetToBeSynch.getPacketSize());
					syncDto.setSupervisorStatus(packetToBeSynch.getSupervisorStatus());
					syncDto.setSupervisorComment(packetToBeSynch.getSupervisorComments());
					syncDtoList.add(syncDto);
				}
				RegistrationPacketSyncDTO registrationPacketSyncDTO = new RegistrationPacketSyncDTO();
				registrationPacketSyncDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
				registrationPacketSyncDTO.setSyncRegistrationDTOs(syncDtoList);
				registrationPacketSyncDTO.setId(RegistrationConstants.PACKET_SYNC_STATUS_ID);
				registrationPacketSyncDTO.setVersion(RegistrationConstants.PACKET_SYNC_VERSION);
				responseDTO = syncPacketsToServer(
						CryptoUtil.encodeBase64(
								aesEncryptionService.encrypt(javaObjectToJsonString(registrationPacketSyncDTO).getBytes())),
						RegistrationConstants.JOB_TRIGGER_POINT_USER);
			}
			if (responseDTO.getSuccessResponseDTO() != null) {

				for (PacketStatusDTO registration : packetsToBeSynched) {
					String status = (String) responseDTO.getSuccessResponseDTO().getOtherAttributes()
							.get(registration.getFileName());
					if (RegistrationConstants.SUCCESS.equalsIgnoreCase(status)) {

						registration.setPacketClientStatus(RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode());

						if (registration.getPacketClientStatus() != null && registration.getPacketClientStatus()
								.equalsIgnoreCase(RegistrationClientStatusCode.RE_REGISTER.getCode())) {

							String ackFileName = registration.getPacketPath();
							int lastIndex = ackFileName.indexOf(RegistrationConstants.ACKNOWLEDGEMENT_FILE);
							String packetPath = ackFileName.substring(0, lastIndex);
							File packet = FileUtils.getFile(packetPath + RegistrationConstants.ZIP_FILE_EXTENSION);
							if (packet.exists() && packet.delete()) {
								registration.setPacketClientStatus(RegistrationClientStatusCode.DELETED.getCode());
							}
						}
						synchedPackets.add(registration);
					}
				}
				updateSyncStatus(synchedPackets);
			} else {
				syncErrorStatus=RegistrationConstants.SYNC_FAILURE;
			}
		} catch (RegBaseCheckedException | JsonProcessingException | URISyntaxException exception) {
			LOGGER.error("REGISTRATION - SYNCH_PACKETS_TO_SERVER - PACKET_UPLOAD_CONTROLLER", APPLICATION_NAME,
					APPLICATION_ID,
					"Error while Synching packets to the server" + ExceptionUtils.getStackTrace(exception));

			syncErrorStatus = exception.getMessage();

		} catch (RegBaseUncheckedException regBaseUncheckedException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_SYNC_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_SYNC_EXCEPTION.getErrorMessage());
		}
		return syncErrorStatus;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.sync.PacketSynchService#fetchPacketsToBeSynched()
	 */
	@Override
	public List<PacketStatusDTO> fetchPacketsToBeSynched() {
		LOGGER.info("REGISTRATION - FETCH_PACKETS_TO_BE_SYNCHED - PACKET_SYNC_SERVICE", APPLICATION_NAME,
				APPLICATION_ID, "Fetch the packets that needs to be synched to the server");
		List<PacketStatusDTO> idsToBeSynched = new ArrayList<>();
		List<Registration> packetsToBeSynched = syncRegistrationDAO
				.fetchPacketsToUpload(RegistrationConstants.PACKET_STATUS_UPLOAD,RegistrationConstants.SERVER_STATUS_RESEND);
		packetsToBeSynched.forEach(reg -> {
			PacketStatusDTO packetStatusDTO = new PacketStatusDTO();
			packetStatusDTO.setFileName(reg.getId());
			packetStatusDTO.setPacketClientStatus(reg.getClientStatusCode());
			packetStatusDTO.setClientStatusComments(reg.getClientStatusComments());
			packetStatusDTO.setPacketServerStatus(reg.getServerStatusCode());
			packetStatusDTO.setPacketPath(reg.getAckFilename());
			packetStatusDTO.setUploadStatus(reg.getFileUploadStatus());
			packetStatusDTO.setPacketStatus(reg.getStatusCode());
			packetStatusDTO.setSupervisorStatus(reg.getClientStatusCode());
			packetStatusDTO.setSupervisorComments(reg.getClientStatusComments());
			packetStatusDTO.setCreatedTime(new SimpleDateFormat("dd-MM-yyyy").format(reg.getCrDtime()));
			idsToBeSynched.add(packetStatusDTO);
		});
		return idsToBeSynched;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.impl.PacketSynchStatus#syncPacketsToServer(java
	 * .util.List)
	 */

	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO syncPacketsToServer(String encodedString, String triggerPoint)
			throws RegBaseCheckedException, URISyntaxException, JsonProcessingException {
		LOGGER.info("REGISTRATION - SYNCH_PACKETS_TO_SERVER - PACKET_SYNC_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Sync the packets to the server");

		ResponseDTO responseDTO = new ResponseDTO();
		try {
			LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) serviceDelegateUtil
					.post(RegistrationConstants.PACKET_SYNC, javaObjectToJsonString(encodedString), triggerPoint);
			if (response.get("response") != null) {
				SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
				Map<String, Object> statusMap = new WeakHashMap<>();
				for (LinkedHashMap<String, Object> responseMap : (List<LinkedHashMap<String, Object>>) response
						.get("response")) {
					statusMap.put((String) responseMap.get("registrationId"), responseMap.get("status"));
				}
				successResponseDTO.setOtherAttributes(statusMap);
				responseDTO.setSuccessResponseDTO(successResponseDTO);
			} else if(response.get("errors") != null) {
				List<ErrorResponseDTO> errorResponseDTOs = new ArrayList<>();
				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setMessage(response.get("errors").toString());
				errorResponseDTOs.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(errorResponseDTOs);
				LOGGER.info("REGISTRATION - SYNCH_PACKETS_TO_SERVER - PACKET_SYNC_SERVICE", APPLICATION_NAME, APPLICATION_ID,
						response.get("errors").toString());
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error("REGISTRATION - SYNCH_PACKETS_TO_SERVER_CLIENT_ERROR - PACKET_SYNC_SERVICE", APPLICATION_NAME,
					APPLICATION_ID,
					e.getRawStatusCode() + "Error in sync packets to the server" + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(Integer.toString(e.getRawStatusCode()), e.getStatusText());
		} catch (RuntimeException e) {
			LOGGER.error("REGISTRATION - SYNCH_PACKETS_TO_SERVER_RUNTIME - PACKET_SYNC_SERVICE", APPLICATION_NAME,
					APPLICATION_ID,
					e.getMessage() + "Error in sync and push packets to the server" + ExceptionUtils.getStackTrace(e));
			throw new RegBaseUncheckedException(RegistrationExceptionConstants.REG_PACKET_SYNC_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_SYNC_EXCEPTION.getErrorMessage());
		} catch (SocketTimeoutException e) {
			LOGGER.error("REGISTRATION - SYNCH_PACKETS_TO_SERVER_SOCKET_ERROR - PACKET_SYNC_SERVICE", APPLICATION_NAME,
					APPLICATION_ID,
					e.getMessage() + "Error in sync packets to the server" + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException((e.getMessage()), e.getLocalizedMessage());
		}

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.impl.PacketSynchStatus#updateSyncStatus(java.
	 * util.List)
	 */

	@Override
	public Boolean updateSyncStatus(List<PacketStatusDTO> synchedPackets) {
		LOGGER.info("REGISTRATION -UPDATE_SYNC_STATUS - PACKET_SYNC_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updating the status of the synched packets to the database");
		for (PacketStatusDTO syncPacket : synchedPackets) {
			syncRegistrationDAO.updatePacketSyncStatus(syncPacket);
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.sync.PacketSynchService#getPacketToSync(java.
	 * lang.String)
	 */
	@Override
	public String packetSync(String rId) throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION -UPDATE_SYNC_STATUS - PACKET_SYNC_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Updating the status of the synched packets to the database");

		Registration registration = syncRegistrationDAO
				.getRegistrationById(RegistrationClientStatusCode.APPROVED.getCode(), rId);
		List<PacketStatusDTO> registrations = new ArrayList<>();
		registrations.add(packetStatusDtoPreperation(registration));
		return packetSync(registrations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.sync.PacketSynchService#syncEODPackets(java.
	 * util.List)
	 */
	@Override
	public String syncEODPackets(List<String> regIds) throws RegBaseCheckedException {
		List<Registration> registrations = syncRegistrationDAO.get(regIds);
		List<PacketStatusDTO> packetsToBeSynched = new ArrayList<>();
		registrations.forEach(reg -> {
			packetsToBeSynched.add(packetStatusDtoPreperation(reg));
		});
		return packetSync(packetsToBeSynched);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.sync.PacketSynchService#syncAllPackets()
	 */
	@Override
	public void syncAllPackets() throws RegBaseCheckedException {
		List<PacketStatusDTO> packetsToBeSynched = fetchPacketsToBeSynched();
		packetSync(packetsToBeSynched);

	}
	
	public Boolean fetchSynchedPacket(String rId) {
		Registration reg = syncRegistrationDAO.getRegistrationById(RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode(), rId);
		return reg != null && !reg.getId().isEmpty();
	}
}
