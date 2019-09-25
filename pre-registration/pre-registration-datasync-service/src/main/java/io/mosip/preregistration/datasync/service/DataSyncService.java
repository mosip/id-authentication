package io.mosip.preregistration.datasync.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentsMetaData;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.exception.util.DataSyncExceptionCatcher;
import io.mosip.preregistration.datasync.service.util.DataSyncServiceUtil;

/**
 * DataSync Service
 * 
 * @version 1.0.0
 * 
 * @author Jagadishwari
 * @author Sanober Noor
 *
 */
@Service
public class DataSyncService {
	/**
	 * Autowired reference for {@link #DataSyncServiceUtil}
	 */
	@Autowired
	private DataSyncServiceUtil serviceUtil;

	/**
	 * Autowired reference for {@link #AuditLogUtil}
	 */
	@Autowired
	AuditLogUtil auditLogUtil;

	/**
	 * This method acts as a post constructor to initialize the required request
	 * parameters.
	 */
	@PostConstruct
	public void setup() {
		requiredRequestMap.put("version", version);
	}

	/**
	 * Reference for ${mosip.id.preregistration.datasync.fetch.ids} from property
	 * file
	 */
	@Value("${mosip.id.preregistration.datasync.fetch.ids}")
	private String fetchAllId;

	/**
	 * Reference for ${mosip.id.preregistration.datasync.store} from property file
	 */
	@Value("${mosip.id.preregistration.datasync.store}")
	private String storeId;

	/**
	 * Reference for ${mosip.id.preregistration.datasync.fetch} from property file
	 */
	@Value("${mosip.id.preregistration.datasync.fetch}")
	private String fetchId;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${version}")
	private String version;

	/**
	 * Request map to store the id and version and this is to be passed to request
	 * validator method.
	 */
	Map<String, String> requiredRequestMap = new HashMap<>();

	private Logger log = LoggerConfiguration.logConfig(DataSyncService.class);

	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public MainResponseDTO<PreRegistrationIdsDTO> retrieveAllPreRegIds(
			MainRequestDTO<DataSyncRequestDTO> dataSyncRequest) {
		PreRegistrationIdsDTO preRegistrationIdsDTO = null;
		MainResponseDTO<PreRegistrationIdsDTO> responseDto = new MainResponseDTO<>();
		log.info("sessionId", "idType", "id", "In retrieveAllPreRegIds method of datasync service ");
		boolean isRetrieveAllSuccess = false;
		responseDto.setId(fetchAllId);
		responseDto.setVersion(version);
		requiredRequestMap.put("id", fetchAllId);
		try {
			ValidationUtil.requestValidator(dataSyncRequest);
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(dataSyncRequest), requiredRequestMap)) {
				serviceUtil.validateDataSyncRequest(dataSyncRequest.getRequest(), responseDto);
				DataSyncRequestDTO dataSyncRequestDTO = dataSyncRequest.getRequest();
				if (serviceUtil.isNull(dataSyncRequestDTO.getToDate())) {
					dataSyncRequestDTO.setToDate(dataSyncRequestDTO.getFromDate());
				}
				PreRegIdsByRegCenterIdResponseDTO preRegIdsDTO = serviceUtil
						.getBookedPreIdsByDateAndRegCenterIdRestService(dataSyncRequestDTO.getFromDate(),
								dataSyncRequestDTO.getToDate(), dataSyncRequestDTO.getRegistrationCenterId());
				PreRegIdsByRegCenterIdDTO byRegCenterIdDTO = new PreRegIdsByRegCenterIdDTO();
				byRegCenterIdDTO.setPreRegistrationIds(preRegIdsDTO.getPreRegistrationIds());
				preRegistrationIdsDTO = serviceUtil.getLastUpdateTimeStamp(byRegCenterIdDTO);
				responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
				responseDto.setResponse(preRegistrationIdsDTO);
			}
			isRetrieveAllSuccess = true;
		} catch (

		Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In retrieveAllPreRegIds method of datasync service - " + ex.getMessage());
			new DataSyncExceptionCatcher().handle(ex, responseDto);
		} finally {
			if (isRetrieveAllSuccess) {
				setAuditValues(EventId.PRE_406.toString(), EventName.SYNC.toString(), EventType.BUSINESS.toString(),
						"Retrieval of all the Preregistration Id is successful",
						AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), dataSyncRequest.getRequest().getRegistrationCenterId());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Retrieval of all the Preregistration Id is unsuccessful", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername(),
						dataSyncRequest.getRequest().getRegistrationCenterId());
			}
		}
		return responseDto;
	}

	/**
	 * @param preId
	 * @return Zipped File
	 * @throws Exception
	 */
	public MainResponseDTO<PreRegArchiveDTO> getPreRegistrationData(String preId) {
		MainResponseDTO<PreRegArchiveDTO> responseDto = new MainResponseDTO<>();
		PreRegArchiveDTO preRegArchiveDTO = null;
		log.info("sessionId", "idType", "id", "In getPreRegistrationData method of datasync service ");
		boolean isRetrieveSuccess = false;
		responseDto.setId(fetchId);
		responseDto.setVersion(version);
		try {
//			serviceUtil.parsejson();
			DemographicResponseDTO preRegistrationDTO = serviceUtil.getPreRegistrationData(preId.trim());
			DocumentsMetaData documentsMetaData = serviceUtil.getDocDetails(preId.trim());
			BookingRegistrationDTO bookingRegistrationDTO = serviceUtil.getAppointmentDetails(preId.trim());
			preRegArchiveDTO = serviceUtil.archivingFiles(preRegistrationDTO, bookingRegistrationDTO,
					documentsMetaData);
			responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
			responseDto.setResponse(preRegArchiveDTO);
			isRetrieveSuccess = true;
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In getPreRegistrationData method of datasync service - " + ex.getMessage());
			new DataSyncExceptionCatcher().handle(ex, responseDto);
		} finally {
			if (isRetrieveSuccess) {
				setAuditValues(EventId.PRE_406.toString(), EventName.SYNC.toString(), EventType.BUSINESS.toString(),
						"Retrieval of the Preregistration data is successful", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername(), null);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Retrieval of the Preregistration data is unsuccessful", AuditLogVariables.NO_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername(), null);
			}
		}
		return responseDto;
	}

	/**
	 * @param reverseDto
	 * @return responseDTO
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<ReverseDatasyncReponseDTO> storeConsumedPreRegistrations(
			MainRequestDTO<ReverseDataSyncRequestDTO> reverseDataSyncRequest) {
		MainResponseDTO<ReverseDatasyncReponseDTO> responseDto = new MainResponseDTO<>();
		ReverseDatasyncReponseDTO reverseDatasyncReponse = null;
		log.info("sessionId", "idType", "id", "In storeConsumedPreRegistrations method of datasync service ");
		boolean isSaveSuccess = false;
		responseDto.setId(storeId);
		responseDto.setVersion(version);
		requiredRequestMap.put("id", storeId);
		try {
			if (ValidationUtil.requestValidator(reverseDataSyncRequest)
					&& serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequest.getRequest(), responseDto)) {
				if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(reverseDataSyncRequest),
						requiredRequestMap)) {
					reverseDatasyncReponse = serviceUtil.reverseDateSyncSave(reverseDataSyncRequest.getRequesttime(),
							reverseDataSyncRequest.getRequest(), "user");
					responseDto.setResponse(reverseDatasyncReponse);
					responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
					responseDto.setErrors(null);
				}
			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In storeConsumedPreRegistrations method of datasync service - " + ex.getMessage());

			new DataSyncExceptionCatcher().handle(ex, responseDto);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_408.toString(), EventName.REVERSESYNC.toString(),
						EventType.BUSINESS.toString(),
						"Reverse Data sync & the consumed PreRegistration ids successfully saved in the database",
						AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), null);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Reverse Data sync failed", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), null);
			}
		}
		return responseDto;
	}

	/**
	 * This method is used to audit all the datasync & reverse datasync events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName, String refId) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setDescription(description);
		auditRequestDto.setIdType(idType);
		auditRequestDto.setId(refId);
		if (!eventName.equalsIgnoreCase("REVERSESYNC")) {
			auditRequestDto.setModuleId(AuditLogVariables.DAT.toString());
			auditRequestDto.setModuleName(AuditLogVariables.DATASYNC_SERVICE.toString());
		} else {
			auditRequestDto.setModuleId(AuditLogVariables.REV.toString());
			auditRequestDto.setModuleName(AuditLogVariables.REVERSE_DATASYNC_SERVICE.toString());
		}
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

}
