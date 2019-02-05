package io.mosip.preregistration.datasync.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.DocumentMultipartResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.preregistration.datasync.exception.util.DataSyncExceptionCatcher;
import io.mosip.preregistration.datasync.service.util.DataSyncServiceUtil;

/**
 * DataSync Service
 * 
 * @version 1.0.0
 * 
 * @author M1046129 - Jagadishwari
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
	 * Reference for ${id} from property file
	 */
	@Value("${id}")
	private String id;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${ver}")
	private String ver;

	/**
	 * Request map to store the id and version and this is to be passed to request
	 * validator method.
	 */
	Map<String, String> requiredRequestMap = new HashMap<>();

	private Logger log = LoggerConfiguration.logConfig(DataSyncService.class);

	/**
	 * This method acts as a post constructor to initialize the required request
	 * parameters.
	 */
	@PostConstruct
	public void setup() {
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
	}

	public MainResponseDTO<PreRegistrationIdsDTO> retrieveAllPreRegIds(
			MainRequestDTO<DataSyncRequestDTO> dataSyncRequest) {
		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		MainResponseDTO<PreRegistrationIdsDTO> responseDto = new MainResponseDTO<>();
		List<String> preregIds;
		log.info("sessionId", "idType", "id", "In retrieveAllPreRegIds method of datasync service ");
		boolean isRetrieveAllSuccess = false;
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(dataSyncRequest), requiredRequestMap)
					&& serviceUtil.validateDataSyncRequest(dataSyncRequest.getRequest())) {
				DataSyncRequestDTO dataSyncRequestDTO = dataSyncRequest.getRequest();
				preregIds = serviceUtil.callGetPreIdsRestService(dataSyncRequestDTO.getFromDate(),
						dataSyncRequestDTO.getToDate());
				PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = serviceUtil
						.callGetPreIdsByRegCenterIdRestService(dataSyncRequestDTO.getRegClientId(), preregIds);
				preRegistrationIdsDTO = serviceUtil
						.getLastUpdateTimeStamp(preRegIdsByRegCenterIdResponseDTO.getPreRegistrationIds());
				responseDto.setStatus(Boolean.TRUE);
				responseDto.setResTime(serviceUtil.getCurrentResponseTime());
				responseDto.setResponse(preRegistrationIdsDTO);
			}
			isRetrieveAllSuccess = true;
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id",
					"In retrieveAllPreRegIds method of datasync service - " + ex.getMessage());

			throw new TableNotAccessibleException(ErrorMessages.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In retrieveAllPreRegIds method of datasync service - " + ex.getMessage());

			new DataSyncExceptionCatcher().handle(ex);
		} finally {
			if (isRetrieveAllSuccess) {
				setAuditValues(EventId.PRE_406.toString(), EventName.SYNC.toString(), EventType.BUSINESS.toString(),
						"Retrieval of all the Preregistration Id is successful",
						AuditLogVariables.MULTIPLE_ID.toString());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Retrieval of all the Preregistration Id is unsuccessful", AuditLogVariables.NO_ID.toString());
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
		PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
		log.info("sessionId", "idType", "id", "In getPreRegistrationData method of datasync service ");
		boolean isRetrieveSuccess = false;
		try {
			DemographicResponseDTO preRegistrationDTO = serviceUtil.callGetPreRegInfoRestService(preId.trim());
			List<DocumentMultipartResponseDTO> documentlist = serviceUtil.callGetDocRestService(preId.trim());
			BookingRegistrationDTO bookingRegistrationDTO = serviceUtil
					.callGetAppointmentDetailsRestService(preId.trim());
			preRegArchiveDTO = serviceUtil.archivingFiles(preRegistrationDTO, bookingRegistrationDTO, documentlist);
			responseDto.setStatus(Boolean.TRUE);
			responseDto.setResTime(serviceUtil.getCurrentResponseTime());
			responseDto.setResponse(preRegArchiveDTO);
			isRetrieveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getPreRegistrationData method of datasync service - " + ex.getMessage());

			new DataSyncExceptionCatcher().handle(ex);
		} finally {
			if (isRetrieveSuccess) {
				setAuditValues(EventId.PRE_406.toString(), EventName.SYNC.toString(), EventType.BUSINESS.toString(),
						"Retrieval of the Preregistration data is successful",
						AuditLogVariables.MULTIPLE_ID.toString());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Retrieval of the Preregistration data is unsuccessful", AuditLogVariables.NO_ID.toString());
			}
		}
		return responseDto;
	}

	/**
	 * @param reverseDto
	 * @return responseDTO
	 */
	public MainResponseDTO<ReverseDatasyncReponseDTO> storeConsumedPreRegistrations(
			MainRequestDTO<ReverseDataSyncRequestDTO> reverseDataSyncRequest) {
		MainResponseDTO<ReverseDatasyncReponseDTO> responseDto = new MainResponseDTO<>();
		ReverseDatasyncReponseDTO reverseDatasyncReponse = new ReverseDatasyncReponseDTO();
		log.info("sessionId", "idType", "id", "In storeConsumedPreRegistrations method of datasync service ");
		boolean isSaveSuccess = false;
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(reverseDataSyncRequest),
					requiredRequestMap)
					&& serviceUtil.validateReverseDataSyncRequest(reverseDataSyncRequest.getRequest())) {
				reverseDatasyncReponse = serviceUtil.reverseDateSyncSave(reverseDataSyncRequest.getReqTime(),
						reverseDataSyncRequest.getRequest());
				responseDto.setStatus(Boolean.TRUE);
				responseDto.setResponse(reverseDatasyncReponse);
				responseDto.setResTime(serviceUtil.getCurrentResponseTime());
				responseDto.setErr(null);
			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In storeConsumedPreRegistrations method of datasync service - " + ex.getMessage());

			new DataSyncExceptionCatcher().handle(ex);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_408.toString(), EventName.REVERSESYNC.toString(),
						EventType.BUSINESS.toString(),
						"Reverse Data sync & the consumed PreRegistration ids successfully saved in the database",
						AuditLogVariables.MULTIPLE_ID.toString());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Reverse Data sync failed", AuditLogVariables.NO_ID.toString());
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
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setId(idType);
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
