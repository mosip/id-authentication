package io.mosip.preregistration.datasync.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.datasync.dto.BookingRegistrationDTO;
import io.mosip.preregistration.datasync.dto.CreateDemographicDTO;
import io.mosip.preregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.DocumentServiceDTO;
import io.mosip.preregistration.datasync.dto.MainRequestDTO;
import io.mosip.preregistration.datasync.dto.MainResponseDTO;
import io.mosip.preregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.preregistration.datasync.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.preregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.preregistration.datasync.dto.ReverseDatasyncReponseDTO;
import io.mosip.preregistration.datasync.errorcodes.ErrorMessages;
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
		PreRegistrationIdsDTO preRegistrationIdsDTO = null;
		MainResponseDTO<PreRegistrationIdsDTO> responseDto = new MainResponseDTO<>();
		List<String> preregIds;
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
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorMessages.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());
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
		try{
		
			CreateDemographicDTO preRegistrationDTO = serviceUtil.callGetPreRegInfoRestService(preId);
			List<DocumentServiceDTO> documentlist = serviceUtil.callGetDocRestService(preId);
			BookingRegistrationDTO bookingRegistrationDTO = serviceUtil.callGetAppointmentDetailsRestService(preId);
			preRegArchiveDTO = serviceUtil.archivingFiles(preRegistrationDTO, bookingRegistrationDTO, documentlist);
			responseDto.setStatus(Boolean.TRUE);
			responseDto.setResTime(serviceUtil.getCurrentResponseTime());
			responseDto.setResponse(preRegArchiveDTO);
		} catch (Exception e) {
			e.printStackTrace();
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
		ReverseDatasyncReponseDTO reverseDatasyncReponse = null;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}
}
