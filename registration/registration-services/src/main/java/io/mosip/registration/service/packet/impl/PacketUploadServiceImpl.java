package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * This class will update the packet status in the table and also push the
 * packets to the server.
 * 
 * @author SaravanaKumar G
 * @since 1.0.0
 */
@Service
@Transactional
public class PacketUploadServiceImpl extends BaseService implements PacketUploadService {

	/** The registration DAO. */
	@Autowired
	private RegistrationDAO registrationDAO;

	/** The service delegate util. */
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(PacketUploadServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketUploadService#getSynchedPackets()
	 */
	@Override
	public List<Registration> getSynchedPackets() {
		LOGGER.info("REGISTRATION - GET_SYNCED_PACKETS - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching synced packets from the database");
		return registrationDAO.getRegistrationByStatus(RegistrationConstants.PACKET_UPLOAD_STATUS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.PacketUploadService#pushPacket(java.io.File)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseDTO pushPacket(File packet) throws URISyntaxException, RegBaseCheckedException {

		LOGGER.info("REGISTRATION - PUSH_PACKET - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Push packets to the server");
		ResponseDTO responseDTO = new ResponseDTO();
		if (packet.exists()) {
			LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			map.add(RegistrationConstants.PACKET_TYPE, new FileSystemResource(packet));
			List<ErrorResponseDTO> erResponseDTOs = new ArrayList<>();
			try {
				LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) serviceDelegateUtil
						.post(RegistrationConstants.PACKET_UPLOAD, map, RegistrationConstants.JOB_TRIGGER_POINT_USER);
				if ((response.get(RegistrationConstants.PACKET_STATUS_READER_RESPONSE) != null
						&& response.get(RegistrationConstants.ERRORS) == null)) {
					SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
					successResponseDTO.setCode(RegistrationConstants.SUCCESS);
					successResponseDTO.setMessage((String) response.get(RegistrationConstants.UPLOAD_STATUS));
					responseDTO.setSuccessResponseDTO(successResponseDTO);
				} else if (response.get(RegistrationConstants.ERRORS) != null) {
					ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
					errorResponseDTO.setCode(RegistrationConstants.ERROR);
					errorResponseDTO.setMessage(
							((List<LinkedHashMap<String, String>>) response.get(RegistrationConstants.ERRORS)).get(0)
									.get("message"));
					erResponseDTOs.add(errorResponseDTO);
					responseDTO.setErrorResponseDTOs(erResponseDTOs);
				}
			} catch (HttpClientErrorException clientException) {
				LOGGER.error("REGISTRATION - PUSH_PACKET_CLIENT_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
						APPLICATION_ID,
						clientException.getRawStatusCode() + "Http error while pushing packets to the server"
								+ ExceptionUtils.getStackTrace(clientException));
				throw new RegBaseCheckedException(Integer.toString(clientException.getRawStatusCode()),
						clientException.getStatusText());
			} catch (HttpServerErrorException serverException) {
				LOGGER.error("REGISTRATION - PUSH_PACKET_SERVER_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
						APPLICATION_ID,
						serverException.getRawStatusCode() + "Http server error while pushing packets to the server"
								+ ExceptionUtils.getStackTrace(serverException));
				throw new RegBaseCheckedException(Integer.toString(serverException.getRawStatusCode()),
						serverException.getResponseBodyAsString());

			} catch (RuntimeException runtimeException) {
				LOGGER.error("REGISTRATION - PUSH_PACKET_CONNECTION_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
						APPLICATION_ID,
						runtimeException.getMessage() + "Runtime error while pushing packets to the server"
								+ ExceptionUtils.getStackTrace(runtimeException));
				throw new RegBaseUncheckedException(
						RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorCode(),
						RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorMessage());
			} catch (SocketTimeoutException socketTimeoutException) {
				LOGGER.error("REGISTRATION - PUSH_PACKETS_TO_SERVER_SOCKET_ERROR - PACKET_UPLOAD_SERVICE",
						APPLICATION_NAME, APPLICATION_ID,
						socketTimeoutException.getMessage() + "Error in sync packets to the server");
				throw new RegBaseCheckedException(
						(socketTimeoutException.getMessage() + ExceptionUtils.getStackTrace(socketTimeoutException)),
						socketTimeoutException.getLocalizedMessage());
			}
		} else {
			LOGGER.error("REGISTRATION - PUSH_PACKETS_TO_SERVER_SOCKET_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, "File doesn't exist");
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_UPLD_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PKT_UPLD_EXCEPTION.getErrorMessage());
		}
		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.PacketUploadService#updateStatus(java.util.
	 * List)
	 */
	@Override
	public Boolean updateStatus(List<PacketStatusDTO> packetsUploadStatus) {
		LOGGER.info("REGISTRATION - UPDATE_STATUS - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Update the status of the uploaded packet");
		for (PacketStatusDTO registrationPacket : packetsUploadStatus) {
			try {
				if (checkPacketDto(registrationPacket)) {
					registrationDAO.updateRegStatus(registrationPacket);
				}
			} catch (RegBaseCheckedException regBaseCheckedException) {
				LOGGER.error("REGISTRATION - UPDATE_STATUS_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
						APPLICATION_ID, "Mandatory Fields missing");
			}
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.PacketUploadService#uploadPacket(java.
	 * lang.String)
	 */
	@Override
	public void uploadPacket(String rid) {
		if (StringUtils.isEmpty(rid)) {
			LOGGER.error("REGISTRATION - UPLOAD_PACKET_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
					"Registration id is missing");
		} else {
			Registration syncedPacket = registrationDAO
					.getRegistrationById(RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode(), rid);
			List<PacketStatusDTO> packetList = new ArrayList<>();
			packetList.add(packetStatusDtoPreperation(syncedPacket));

			uploadSyncedPacket(packetList);
		}
	}

	/**
	 * Upload synced packets.
	 *
	 * @param syncedPackets
	 *            the synced packets
	 */
	private void uploadSyncedPacket(List<PacketStatusDTO> syncedPackets) {

		List<PacketStatusDTO> packetUploadList = new ArrayList<>();

		if (!syncedPackets.isEmpty()) {
			for (PacketStatusDTO syncedPacket : syncedPackets) {
				if (syncedPacket != null) {
					String ackFileName = syncedPacket.getPacketPath();
					int lastIndex = ackFileName.indexOf(RegistrationConstants.ACKNOWLEDGEMENT_FILE);
					String packetPath = ackFileName.substring(0, lastIndex);
					File packet = FileUtils.getFile(packetPath + RegistrationConstants.ZIP_FILE_EXTENSION);
					try {
						if (packet.exists()) {
							ResponseDTO response = pushPacket(packet);

							if (response.getSuccessResponseDTO() != null) {
								syncedPacket.setPacketClientStatus(
										RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
								syncedPacket
										.setUploadStatus(RegistrationClientStatusCode.UPLOAD_SUCCESS_STATUS.getCode());
								syncedPacket.setPacketServerStatus(response.getSuccessResponseDTO().getMessage());
								packetUploadList.add(syncedPacket);

							} else if (response.getErrorResponseDTOs() != null) {
								String errMessage = response.getErrorResponseDTOs().get(0).getMessage();
								if (errMessage.contains(RegistrationConstants.PACKET_DUPLICATE)) {

									syncedPacket.setPacketClientStatus(
											RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
									syncedPacket.setUploadStatus(
											RegistrationClientStatusCode.UPLOAD_SUCCESS_STATUS.getCode());
									packetUploadList.add(syncedPacket);

								}
							} else {
								syncedPacket
										.setUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
								packetUploadList.add(syncedPacket);
							}
						}
					} catch (RegBaseCheckedException | URISyntaxException exception) {
						LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_ERROR - PACKET_UPLOAD_SERVICE",
								APPLICATION_NAME, APPLICATION_ID, "Error while pushing packets to the server"
										+ exception.getMessage() + ExceptionUtils.getStackTrace(exception));
						syncedPacket.setUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
						packetUploadList.add(syncedPacket);
					} catch (RuntimeException runtimeException) {
						LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_RUNTIME_ERROR - PACKET_UPLOAD_SERVICE",
								APPLICATION_NAME, APPLICATION_ID,
								"Run time error while connecting to the server" + runtimeException.getMessage()
										+ ExceptionUtils.getStackTrace(runtimeException));

						syncedPacket.setUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
						packetUploadList.add(syncedPacket);
					}
				}
			}
		}
		updateStatus(packetUploadList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.PacketUploadService#uploadEODPackets(
	 * java.util.List)
	 */
	@Override
	public void uploadEODPackets(List<String> regIds) {
		if(!regIds.isEmpty()) {
		regIds.forEach(regId -> {
			if(StringUtils.isEmpty(regId)) {
				LOGGER.error("REGISTRATION - UPLOAD_PACKET_EOD_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
						APPLICATION_ID, "Registration id is missing");
			}
		});
		List<Registration> registrations = registrationDAO.get(regIds);
		List<PacketStatusDTO> packetsToBeSynced = new ArrayList<>();
		registrations.forEach(reg -> {
			packetsToBeSynced.add(packetStatusDtoPreperation(reg));
		});
		uploadSyncedPacket(packetsToBeSynced);
		} else {
			LOGGER.error("REGISTRATION - UPLOAD_PACKET_EOD_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, "Registration id list is missing");
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.packet.PacketUploadService#
	 * uploadAllSyncedPackets()
	 */
	@Override
	public void uploadAllSyncedPackets() {

		List<Registration> synchedPackets = getSynchedPackets();
		List<PacketStatusDTO> packetsToBeSynced = new ArrayList<>();
		synchedPackets.forEach(reg -> {
			packetsToBeSynced.add(packetStatusDtoPreperation(reg));
		});
		uploadSyncedPacket(packetsToBeSynced);

	}

	private Boolean checkPacketDto(PacketStatusDTO packetStatusDTO) throws RegBaseCheckedException {

		if (StringUtils.isEmpty(packetStatusDTO.getFileName())) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_FILE_NAME_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PKT_FILE_NAME_EXCEPTION.getErrorMessage());
		} else if (StringUtils.isEmpty(packetStatusDTO.getPacketClientStatus())) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_CLIENT_STATUS.getErrorCode(),
					RegistrationExceptionConstants.REG_PKT_CLIENT_STATUS.getErrorMessage());
		}
		return true;

	}

}
