package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

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
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;

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

	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private RestClientUtil restClientUtil;

	@Value("${PACKET_UPLOAD_URL}")
	private String urlPath;

	@Value("${UPLOAD_API_READ_TIMEOUT}")
	private int readTimeout;

	@Value("${UPLOAD_API_WRITE_TIMEOUT}")
	private int connectTimeout;

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
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add(RegistrationConstants.PACKET_TYPE, new FileSystemResource(packet));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		requestHTTPDTO.setHttpEntity(requestEntity);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setUri(new URI(urlPath));
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		Object response = null;
		try {
			response = restClientUtil.invoke(setTimeout(requestHTTPDTO));
		} catch (HttpClientErrorException e) {
			LOGGER.error("REGISTRATION - PUSH_PACKET_CLIENT_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, e.getRawStatusCode() + "Http error while pushing packets to the server");
			throw new RegBaseCheckedException(Integer.toString(e.getRawStatusCode()), e.getStatusText());
		} catch (RuntimeException e) {
			LOGGER.error("REGISTRATION - PUSH_PACKET_CONNECTION_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, e.getMessage() + "Runtime error while pushing packets to the server");
			throw new RegBaseUncheckedException(RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_UPLOAD_ERROR.getErrorMessage());
		} catch (SocketTimeoutException e) {
			LOGGER.error("REGISTRATION - PUSH_PACKETS_TO_SERVER_SOCKET_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, e.getMessage() + "Error in sync packets to the server");
			throw new RegBaseCheckedException((e.getMessage()), e.getLocalizedMessage());
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

	private RequestHTTPDTO setTimeout(RequestHTTPDTO requestHTTPDTO) {
		// Timeout in milli second
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout(readTimeout);
		requestFactory.setConnectTimeout(connectTimeout);
		requestHTTPDTO.setSimpleClientHttpRequestFactory(requestFactory);
		return requestHTTPDTO;
	}

	@Override
	public void uploadPacket(String rid) {
		Registration syncedPacket = registrationDAO
				.getRegistrationById(RegistrationClientStatusCode.META_INFO_SYN_SERVER.getCode(), rid);
		List<Registration> packetUploadList = new ArrayList<>();

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
			LOGGER.error("REGISTRATION - HANDLE_PACKET_UPLOAD_RUNTIME_ERROR - PACKET_UPLOAD_SERVICE", APPLICATION_NAME,
					APPLICATION_ID, "Run time error while connecting to the server" + runtimeException.getMessage());

			syncedPacket.setFileUploadStatus(RegistrationClientStatusCode.UPLOAD_ERROR_STATUS.getCode());
			packetUploadList.add(syncedPacket);
		}
		updateStatus(packetUploadList);
	}
}
