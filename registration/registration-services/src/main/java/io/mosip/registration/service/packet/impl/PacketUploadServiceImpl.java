package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

// TODO: Auto-generated Javadoc
/**
 * 
 * This class will update the packet status in the table and also push the
 * packets to the server.
 * 
 * @author SaravanaKumar G
 *
 */
@Service
@Transactional
public class PacketUploadServiceImpl implements PacketUploadService {

	/** The registration DAO. */
	@Autowired
	private RegistrationDAO registrationDAO;

	/** The service delegate util. */
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	/** The url path. */
	@Value("${PACKET_UPLOAD_URL}")
	private String urlPath;

	/** The read timeout. */
	@Value("${UPLOAD_API_READ_TIMEOUT}")
	private int readTimeout;

	/** The connect timeout. */
	@Value("${UPLOAD_API_WRITE_TIMEOUT}")
	private int connectTimeout;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = AppConfig.getLogger(PacketUploadServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketUploadService#getSynchedPackets()
	 */
	@SuppressWarnings("unchecked")
	public List<Registration> getSynchedPackets() {
		LOGGER.info("REGISTRATION - GET_SYNCHED_PACKETS - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Fetching synched packets from the database");
		return registrationDAO.getRegistrationByStatus(RegistrationConstants.getStatus());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.PacketUploadService#pushPacket(java.io.File)
	 */
	public Object pushPacket(File packet) throws URISyntaxException, RegBaseCheckedException {
		LOGGER.info("REGISTRATION - PUSH_PACKET - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Push packets to the server");

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add(RegistrationConstants.PACKET_TYPE, new FileSystemResource(packet));
		Object response = null;
		try {
			response = serviceDelegateUtil.post(RegistrationConstants.PACKET_UPLOAD, map);
		} catch (HttpClientErrorException clientException) {
			LOGGER.error("REGISTRATION - PUSH_PACKET_CLIENT_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID,
					clientException.getRawStatusCode() + "Http error while pushing packets to the server");
			throw new RegBaseCheckedException(Integer.toString(clientException.getRawStatusCode()),
					clientException.getStatusText());
		} catch (HttpServerErrorException serverException) {
			LOGGER.error("REGISTRATION - PUSH_PACKET_SERVER_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID,
					serverException.getRawStatusCode() + "Http server error while pushing packets to the server");
			throw new RegBaseCheckedException(Integer.toString(serverException.getRawStatusCode()),
					serverException.getResponseBodyAsString());

		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - PUSH_PACKET_CONNECTION_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID,
					runtimeException.getMessage() + "Runtime error while pushing packets to the server");
			throw new RegBaseUncheckedException(RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorMessage());
		} catch (SocketTimeoutException socketTimeoutException) {
			LOGGER.error("REGISTRATION - PUSH_PACKETS_TO_SERVER_SOCKET_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, socketTimeoutException.getMessage() + "Error in sync packets to the server");
			throw new RegBaseCheckedException((socketTimeoutException.getMessage()),
					socketTimeoutException.getLocalizedMessage());
		}
		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.PacketUploadService#updateStatus(java.util.
	 * List)
	 */
	public Boolean updateStatus(List<Registration> packetsUploadStatus) {
		LOGGER.info("REGISTRATION - UPDATE_STATUS - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Update the status of the uploaded packet");
		for (Registration registrationPacket : packetsUploadStatus) {
			registrationDAO.updateRegStatus(registrationPacket);
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
		Registration syncedPacket = registrationDAO
				.getRegistrationById(RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode(), rid);
		List<Registration> packetList = new ArrayList<>();
		packetList.add(syncedPacket);

		uploadSyncedPacket(packetList);
	}

	/**
	 * Upload synced packets.
	 *
	 * @param syncedPackets the synced packets
	 */
	private void uploadSyncedPacket(List<Registration> syncedPackets) {

		List<Registration> packetUploadList = new ArrayList<>();

		for (Registration syncedPacket : syncedPackets) {

			syncedPacket.setUploadCount((short) (syncedPacket.getUploadCount() + 1));
			String ackFileName = syncedPacket.getAckFilename();
			int lastIndex = ackFileName.indexOf(RegistrationConstants.ACKNOWLEDGEMENT_FILE);
			String packetPath = ackFileName.substring(0, lastIndex);
			File packet = new File(packetPath + RegistrationConstants.ZIP_FILE_EXTENSION);
			try {
				if (packet.exists()) {
					Object response = pushPacket(packet);

					String responseCode = response.toString();
					if (responseCode.equals("PACKET_UPLOADED_TO_VIRUS_SCAN")) {
						syncedPacket.setClientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());
						syncedPacket.setFileUploadStatus(RegistrationClientStatusCode.UPLOAD_SUCCESS_STATUS.getCode());
						packetUploadList.add(syncedPacket);

					} else {
						syncedPacket.setFileUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
						packetUploadList.add(syncedPacket);
					}
				}
			} catch (RegBaseCheckedException | URISyntaxException exception) {
				LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
						APPLICATION_ID, "Error while pushing packets to the server" + exception.getMessage());
				syncedPacket.setFileUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
				packetUploadList.add(syncedPacket);
			} catch (RuntimeException runtimeException) {
				LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_RUNTIME_ERROR - PACKET_UPLOAD_SERVICE",
						APPLICATION_NAME, APPLICATION_ID,
						"Run time error while connecting to the server" + runtimeException.getMessage());

				syncedPacket.setFileUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
				packetUploadList.add(syncedPacket);
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
		List<Registration> registrations = registrationDAO.get(regIds);
		uploadSyncedPacket(registrations);
	}
}
