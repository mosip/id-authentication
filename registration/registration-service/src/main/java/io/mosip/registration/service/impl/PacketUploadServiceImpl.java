package io.mosip.registration.service.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.RegTransactionDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.PacketUploadService;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;

/**
 * 
 * This class will update the packet status in the table after the Packets got
 * uploaded in the FTP server.
 * 
 * @author M1046564
 *
 */
@Service
@Transactional
public class PacketUploadServiceImpl implements PacketUploadService {
	/** Object for Logger. */
	private static MosipLogger LOGGER;

	private static final List<String> PACKET_STATUS = Arrays.asList("I", "H", "A", "S");

	@Autowired
	private RegistrationDAO registrationDAO;

	@Autowired
	private RegTransactionDAO regTransactionDAO;

	@Autowired
	private RestClientUtil restClientUtil;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	public List<Registration> getSynchedPackets() {
		return registrationDAO.getRegistrationByStatus(PACKET_STATUS);
	}

	public Object pushPacket(File packet) throws URISyntaxException, RegBaseCheckedException {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		String uriPath = "http://104.211.209.102:8080/v0.1/registration-processor/packet-receiver/registrationpackets";
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", new FileSystemResource(packet));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		requestHTTPDTO.setHttpEntity(requestEntity);
		requestHTTPDTO.setClazz(Object.class);
		requestHTTPDTO.setUri(new URI(uriPath));
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		Object response = null;
		try {
			response = restClientUtil.invoke(requestHTTPDTO);
		} catch (HttpClientErrorException e) {
			throw new RegBaseCheckedException(Integer.toString(e.getRawStatusCode()), e.getStatusText());
		}
		return response;

	}

	/**
	 * This method is used to update the packet status that are uploaded.
	 * 
	 * @param uploadedPackets
	 * @return
	 */
	public Boolean updateStatus(Map<String, String> packetStatus) {
		LOGGER.debug("REGISTRATION - UPDATE_STATUS - PACKET_UPLOAD_SERVICE", APPLICATION_NAME, APPLICATION_ID,
				"Update the status of the uploaded packet");
		List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
		for (Map.Entry<String, String> status : packetStatus.entrySet()) {
			String regId = status.getKey();
			String statusCode = status.getValue();
			registrationDAO.updateRegStatus(regId, statusCode);
			registrationTransactions.add(regTransactionDAO.buildRegTrans(regId, statusCode));
		}
		regTransactionDAO.insertPacketTransDetails(registrationTransactions);
		return true;

	}
}
