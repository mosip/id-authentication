package io.mosip.pregistration.datasync.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.pregistration.datasync.dto.BookingRegistrationDTO;
import io.mosip.pregistration.datasync.dto.CreateDemographicDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.DocumentServiceDTO;
import io.mosip.pregistration.datasync.dto.MainRequestDTO;
import io.mosip.pregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.pregistration.datasync.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.pregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.repository.ReverseDataSyncRepo;
import io.mosip.pregistration.datasync.service.util.DataSyncServiceUtil;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;
import io.mosip.preregistration.core.util.ValidationUtil;

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
	 * Autowired reference for {@link #DataSyncRepository}
	 */
	@Autowired
	@Qualifier("dataSyncRepository")
	private DataSyncRepository dataSyncRepo;

	/**
	 * Autowired reference for {@link #ReverseDataSyncRepo}
	 */
	@Autowired
	private ReverseDataSyncRepo reverseDataSyncRepo;

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

	public DataSyncResponseDTO<PreRegistrationIdsDTO> retrieveAllPreRegIds(
			MainRequestDTO<DataSyncRequestDTO> dataSyncRequest) {
		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		DataSyncResponseDTO<PreRegistrationIdsDTO> responseDto = new DataSyncResponseDTO<>();
		List<String> preregIds;
		System.out.println("MainRequestDTO::"+dataSyncRequest);
		System.out.println("DataSyncRequestDTO::"+dataSyncRequest.getRequest());
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(dataSyncRequest), requiredRequestMap)
					&& serviceUtil.validateRequest(dataSyncRequest.getRequest())) {
				DataSyncRequestDTO dataSyncRequestDTO = dataSyncRequest.getRequest();
				preregIds = serviceUtil.callGetPreIdsRestService(dataSyncRequestDTO.getFromDate(),
						dataSyncRequestDTO.getToDate());
				PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = serviceUtil
						.callGetPreIdsByRegCenterIdRestService(dataSyncRequestDTO.getRegClientId(), preregIds);
				
				Map<String,String> preRegMap = serviceUtil
						.getLastUpdateTimeStamp(preRegIdsByRegCenterIdResponseDTO.getPre_registration_ids());
				
				preRegistrationIdsDTO.setPreRegistrationIds(preRegMap);
				preRegistrationIdsDTO.setTransactionId(UUIDGeneratorUtil.generateId());
				responseDto.setErr(null);
				responseDto.setStatus("true");
				responseDto.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
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
	public DataSyncResponseDTO<PreRegArchiveDTO> getPreRegistration(String preId) {
		DataSyncResponseDTO<PreRegArchiveDTO> responseDto = new DataSyncResponseDTO<>();
		PreRegArchiveDTO preRegArchiveDTO = null;

		try {
			CreateDemographicDTO preRegistrationDTO = serviceUtil.callGetPreRegInfoRestService(preId);
			List<DocumentServiceDTO> documentlist = serviceUtil.callGetDocRestService(preId);
			BookingRegistrationDTO bookingRegistrationDTO = serviceUtil.callGetAppointmentDetailsRestService(preId);
			preRegArchiveDTO = serviceUtil.archivingFiles(preRegistrationDTO, bookingRegistrationDTO, documentlist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseDto.setStatus("true");
		responseDto.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
		responseDto.setErr(null);
		responseDto.setResponse(preRegArchiveDTO);
		return responseDto;
	}

	/**
	 * @param reverseDto
	 * @return responseDTO
	 */
	public DataSyncResponseDTO<String> storeConsumedPreRegistrations(ReverseDataSyncDTO reverseDto) {
		DataSyncResponseDTO<String> responseDto = new DataSyncResponseDTO<>();
		List<ReverseDataSyncEntity> entityList = new ArrayList<>();
		List<PreRegistrationProcessedEntity> processedEntityList = new ArrayList<>();
		try {
			ReverseDataSyncRequestDTO reverseRequestDTO = reverseDto.getRequest();
			List<String> preIdList = reverseRequestDTO.getPre_registration_ids();
			if (preIdList != null && !preIdList.isEmpty()) {
				for (int i = 0; i < preIdList.size(); i++) {
					serviceUtil.reverseDatasyncEntitySetter(reverseDto, entityList, processedEntityList, preIdList, i);
				}
			}
			serviceUtil.reverseDatasyncSave(responseDto, entityList, processedEntityList, reverseDataSyncRepo,
					dataSyncRepo);
			responseDto.setStatus("true");
			responseDto.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
			responseDto.setErr(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseDto;
	}

}
