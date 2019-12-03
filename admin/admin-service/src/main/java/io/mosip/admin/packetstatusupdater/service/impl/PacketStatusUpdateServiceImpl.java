package io.mosip.admin.packetstatusupdater.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.packetstatusupdater.constant.PacketStatusUpdateErrorCode;
import io.mosip.admin.packetstatusupdater.dto.PacketStatusUpdateDto;
import io.mosip.admin.packetstatusupdater.dto.PacketStatusUpdateResponseDto;
import io.mosip.admin.packetstatusupdater.exception.MasterDataServiceException;
import io.mosip.admin.packetstatusupdater.exception.ValidationException;
import io.mosip.admin.packetstatusupdater.service.PacketStatusUpdateService;
import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Packet Status Update service.
 * 
 * @author Srinivasan
 *
 */
@Component
public class PacketStatusUpdateServiceImpl implements PacketStatusUpdateService {

	/** The packet update status url. */
	@Value("${mosip.kernel.packet-status-update-url}")
	private String packetUpdateStatusUrl;

	/** The zone validation url. */
	@Value("${mosip.kernel.zone-validation-url}")
	private String zoneValidationUrl;

	@Value("${mosip.primary-language:eng}")
	private String primaryLang;

	/** The rest template. */
	@Autowired
	private RestTemplate restTemplate;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;

	private static final String SLASH = "/";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.admin.packetstatusupdater.service.PacketStatusUpdateService#
	 * getStatus(java.lang.String)
	 */
	@Override
	public PacketStatusUpdateResponseDto getStatus(String rId) {

		authorizeRidWithZone(rId);
		return getPacketStatus(rId);
	}

	/**
	 * Gets the packet status.
	 *
	 * @param rId
	 *            the r id
	 * @return the packet status
	 */
	@SuppressWarnings({ "unchecked" })
	private PacketStatusUpdateResponseDto getPacketStatus(String rId) {
		try {

			HttpHeaders packetHeaders = new HttpHeaders();
			packetHeaders.setContentType(MediaType.APPLICATION_JSON);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(packetUpdateStatusUrl).append(SLASH).append(primaryLang).append(SLASH).append(rId);
			ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, null,
					String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				List<PacketStatusUpdateDto> packetStatusUpdateDtos = getPacketResponse(ArrayList.class,
						response.getBody());
				PacketStatusUpdateResponseDto regProcPacketStatusRequestDto = new PacketStatusUpdateResponseDto();
				// packetStatusUpdateDtos.stream().sorted(Comparator.comparing(PacketStatusUpdateDto::getCreatedDateTimes).reversed());
				List<PacketStatusUpdateDto> packStautsDto = objectMapper.convertValue(
					    packetStatusUpdateDtos, 
					    new TypeReference<List<PacketStatusUpdateDto>>(){}
					);
				packStautsDto.sort(createdDateTimesComparator);
				packStautsDto=packStautsDto.stream().filter(distinctTypeCode(PacketStatusUpdateDto::getTransactionTypeCode)).collect(Collectors.toList());
				//packStautsDto.sort(createdDateTimesComparator);
				
				regProcPacketStatusRequestDto.setPacketStatusUpdateList(packStautsDto);
				return regProcPacketStatusRequestDto;
			}
		} catch (HttpServerErrorException | HttpClientErrorException ex) {
			throwRestExceptions(ex);
		}
		return null;

	}

	/**
	 * Throw rest exceptions.
	 *
	 * @param ex
	 *            the ex
	 */
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

	/**
	 * Authorize rid with zone.
	 *
	 * @param rId
	 *            the r id
	 * @return true, if successful
	 */
	private boolean authorizeRidWithZone(String rId) {
		try {
			HttpHeaders packetHeaders = new HttpHeaders();
			UriComponentsBuilder uribuilder = UriComponentsBuilder.fromUriString(zoneValidationUrl).queryParam("rid",
					rId);
			HttpEntity<RequestWrapper<String>> httpReq = new HttpEntity<>(null, packetHeaders);
			ResponseEntity<String> response = restTemplate.exchange(uribuilder.toUriString(), HttpMethod.GET, httpReq,
					String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				boolean isAuthorized = getPacketResponse(Boolean.class, response.getBody());
				return isAuthorized;
			}

		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throwRestExceptions(e);
		}
		return false;
	}

	/**
	 * Gets the packet response.
	 *
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @param responseBody
	 *            the response body
	 * @return the packet response
	 */

	private <T> T getPacketResponse(Class<T> clazz, String responseBody) {
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		T packetStatusUpdateDto = null;
		if (!validationErrorsList.isEmpty()) {
			throw new ValidationException(validationErrorsList);
		}
		ResponseWrapper<T> responseObject = null;
		try {

			responseObject = objectMapper.readValue(responseBody, new TypeReference<ResponseWrapper<T>>() {
			});
			packetStatusUpdateDto = responseObject.getResponse();
		} catch (NullPointerException | java.io.IOException exception) {
			throw new ParseResponseException(PacketStatusUpdateErrorCode.PACKET_JSON_PARSE_EXCEPTION.getErrorCode(),
					PacketStatusUpdateErrorCode.PACKET_JSON_PARSE_EXCEPTION.getErrorMessage());

		}
		return packetStatusUpdateDto;
	}

	Comparator<PacketStatusUpdateDto> createdDateTimesComparator = new Comparator<PacketStatusUpdateDto>() {

		@Override
		public int compare(PacketStatusUpdateDto o1, PacketStatusUpdateDto o2) {
			LocalDateTime o1CreatedDateTimes = DateUtils.parseToLocalDateTime(o1.getCreatedDateTimes());
			LocalDateTime o2CreatedDateTimes = DateUtils.parseToLocalDateTime(o2.getCreatedDateTimes());
			return o2CreatedDateTimes.compareTo(o1CreatedDateTimes);
		}
	};
	
	private static <T> Predicate<T> distinctTypeCode(Function<? super T, Object>... keyRetrievers) {

	    final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

	    return t -> {

	        final List<?> keys = Arrays.stream(keyRetrievers)
	            .map(key -> key.apply(t))
	            .collect(Collectors.toList());

	        return seen.putIfAbsent(keys, Boolean.TRUE) == null;

	    };

	}

}
