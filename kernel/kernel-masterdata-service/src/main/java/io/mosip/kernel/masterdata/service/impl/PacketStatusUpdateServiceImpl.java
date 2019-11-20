package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.packetstatusupdater.dto.PacketStatusUpdateDto;
import io.mosip.kernel.core.packetstatusupdater.dto.PacketUpdateStatusRequestDto;
import io.mosip.kernel.core.packetstatusupdater.spi.PacketStatusUpdateService;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.masterdata.constant.PacketStatusUpdateErrorCode;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.exception.ValidationException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

/**
 * Packet Status Update service.
 * 
 * @author Srinivasan
 *
 */
@Service
public class PacketStatusUpdateServiceImpl implements PacketStatusUpdateService {

	@Value("${mosip.kernel.registrationcenterid.length}")
	private int centerIdLength;

	@Value("${mosip.kernel.packet-status-update-url}")
	private String packetUpdateStatusUrl;

	@Value("${mosip.primary-language}")
	private String primaryLangCode;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RegistrationCenterRepository registrationCenterRepo;

	@Autowired
	private ZoneUtils zoneUtils;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public PacketStatusUpdateDto getStatus(String rId) {

		String centerId = rId.substring(0, centerIdLength);
		String zoneCode = getZoneBasedOnTheRId(centerId);
		List<Zone> zones = zoneUtils.getUserLeafZones(primaryLangCode);
		boolean isAuthorized = zones.stream().anyMatch(zone -> zone.getCode().equals(zoneCode));
		if (!isAuthorized) {
			throw new RequestException(PacketStatusUpdateErrorCode.ADMIN_UNAUTHORIZED.getErrorCode(),
					PacketStatusUpdateErrorCode.ADMIN_UNAUTHORIZED.getErrorMessage());
		}
		return getPacketStatus(rId);
	}

	private String getZoneBasedOnTheRId(String centerId) {
		RegistrationCenter registrationCenter = null;
		try {
			registrationCenter = registrationCenterRepo.findByIdAndLangCode(centerId, primaryLangCode);
		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException("ADM-PKT-500", "Error occured while fetching packet");
		}
		Objects.nonNull(registrationCenter);
		return registrationCenter.getZoneCode();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PacketStatusUpdateDto getPacketStatus(String rId) {
		try {
			RequestWrapper<Object> req = new RequestWrapper<>();
			req.setId("mosip.registration.status");
			req.setVersion("1.0");
			req.setRequesttime(LocalDateTime.now(ZoneOffset.UTC));

			PacketUpdateStatusRequestDto packetUpdateStatusRequestDto = new PacketUpdateStatusRequestDto();
			packetUpdateStatusRequestDto.setRegistrationId(rId);
			req.setRequest(Arrays.asList(packetUpdateStatusRequestDto));
			String requestString = objectMapper.writeValueAsString(req);

			HttpHeaders packetHeaders = new HttpHeaders();
			packetHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> httpReq = new HttpEntity<>(requestString, packetHeaders);
			ResponseEntity<String> response = restTemplate.postForEntity(packetUpdateStatusUrl, httpReq, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return getPacketResponse(response.getBody());

			}
		} catch (HttpServerErrorException | HttpClientErrorException ex) {
			throwRestExceptions(ex);
		} catch (JsonProcessingException e) {
			throw new ParseResponseException(PacketStatusUpdateErrorCode.PACKET_JSON_PARSE_EXCEPTION.getErrorCode(),
					PacketStatusUpdateErrorCode.PACKET_JSON_PARSE_EXCEPTION.getErrorMessage());
		}
		return null;

	}

	private void throwRestExceptions(HttpStatusCodeException ex) {
		List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

		if (ex.getRawStatusCode() == 401) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthNException(validationErrorsList);
			} else {
				throw new BadCredentialsException("Authentication failed from AuthManager");
			}
		}
		if (ex.getRawStatusCode() == 403) {
			if (!validationErrorsList.isEmpty()) {
				throw new AuthZException(validationErrorsList);
			} else {
				throw new AccessDeniedException("Access denied from AuthManager");
			}
		}
		throw new MasterDataServiceException(PacketStatusUpdateErrorCode.PACKET_FETCH_EXCEPTION.getErrorCode(),
				PacketStatusUpdateErrorCode.PACKET_FETCH_EXCEPTION.getErrorMessage(), ex);

	}

	@SuppressWarnings("unchecked")
	private PacketStatusUpdateDto getPacketResponse(String responseBody) {
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		List<PacketStatusUpdateDto> packetStatusUpdateDto = null;
		if (!validationErrorsList.isEmpty()) {
			throw new ValidationException(validationErrorsList);
		}
		ResponseWrapper<PacketStatusUpdateDto> responseObject = null;
		try {

			responseObject = objectMapper.readValue(responseBody,
					new TypeReference<ResponseWrapper<List<PacketStatusUpdateDto>>>() {
					});
			packetStatusUpdateDto = (List<PacketStatusUpdateDto>) responseObject.getResponse();
		} catch (NullPointerException | java.io.IOException exception) {
			throw new ParseResponseException(PacketStatusUpdateErrorCode.PACKET_JSON_PARSE_EXCEPTION.getErrorCode(),
					PacketStatusUpdateErrorCode.PACKET_JSON_PARSE_EXCEPTION.getErrorMessage());

		}
		return packetStatusUpdateDto.get(0);
	}

}
