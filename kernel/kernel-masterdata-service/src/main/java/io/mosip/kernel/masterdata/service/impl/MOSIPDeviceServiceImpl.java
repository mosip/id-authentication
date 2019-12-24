package io.mosip.kernel.masterdata.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MOSIPDeviceServiceErrorCode;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceDto;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServiceExtDto;
import io.mosip.kernel.masterdata.dto.MOSIPDeviceServicePUTDto;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceServiceHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceHistoryRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceRepository;
import io.mosip.kernel.masterdata.repository.RegistrationDeviceSubTypeRepository;
import io.mosip.kernel.masterdata.repository.RegistrationDeviceTypeRepository;
import io.mosip.kernel.masterdata.service.MOSIPDeviceServices;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Megha Tanga
 * 
 */
@Service
public class MOSIPDeviceServiceImpl implements MOSIPDeviceServices {

	
	@Autowired
	MOSIPDeviceServiceRepository mosipDeviceServiceRepository;

	@Autowired
	MOSIPDeviceServiceHistoryRepository mosipDeviceServiceHistoryRepository;

	@Autowired
	RegistrationDeviceTypeRepository registrationDeviceTypeRepository;

	@Autowired
	RegistrationDeviceSubTypeRepository registrationDeviceSubTypeRepository;

	@Autowired
	DeviceProviderRepository deviceProviderRepository;



	@Override
	@Transactional
	public MOSIPDeviceServiceExtDto createMOSIPDeviceService(MOSIPDeviceServiceDto dto) {
		MOSIPDeviceService crtMosipDeviceService = null;
		MOSIPDeviceService entity = null;

		try {

			if ((registrationDeviceTypeRepository
					.findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getRegDeviceTypeCode())) == null) {
				throw new RequestException(MOSIPDeviceServiceErrorCode.REG_DEVICE_TYPE_NOT_FOUND.getErrorCode(),
						MOSIPDeviceServiceErrorCode.REG_DEVICE_TYPE_NOT_FOUND.getErrorMessage());
			}
			if ((registrationDeviceSubTypeRepository
					.findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getRegDeviceSubCode())) == null) {
				throw new RequestException(MOSIPDeviceServiceErrorCode.REG_DEVICE_SUB_TYPE_NOT_FOUND.getErrorCode(),
						MOSIPDeviceServiceErrorCode.REG_DEVICE_SUB_TYPE_NOT_FOUND.getErrorMessage());
			}
			if ((deviceProviderRepository
					.findByIdAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getDeviceProviderId())) == null) {
				throw new RequestException(MOSIPDeviceServiceErrorCode.DEVICE_PROVIDER_NOT_FOUND.getErrorCode(),
						MOSIPDeviceServiceErrorCode.DEVICE_PROVIDER_NOT_FOUND.getErrorMessage());
			}

			entity = MetaDataUtils.setCreateMetaData(dto, MOSIPDeviceService.class);
			String id  = UUID.randomUUID().toString();
			entity.setId(id);
			crtMosipDeviceService = mosipDeviceServiceRepository.create(entity);

			MOSIPDeviceServiceHistory entityHistory = new MOSIPDeviceServiceHistory();
			MapperUtils.map(crtMosipDeviceService, entityHistory);
			MapperUtils.setBaseFieldValue(crtMosipDeviceService, entityHistory);
			entityHistory.setEffectDateTime(crtMosipDeviceService.getCreatedDateTime());
			entityHistory.setCreatedDateTime(crtMosipDeviceService.getCreatedDateTime());
			mosipDeviceServiceHistoryRepository.create(entityHistory);

		} catch (DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(MOSIPDeviceServiceErrorCode.MDS_INSERTION_EXCEPTION.getErrorCode(),
					MOSIPDeviceServiceErrorCode.MDS_INSERTION_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(exception));
		}
		return MapperUtils.map(crtMosipDeviceService, MOSIPDeviceServiceExtDto.class);

	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.MOSIPDeviceServices#updateMOSIPDeviceService(io.mosip.kernel.masterdata.dto.MOSIPDeviceServicePUTDto)
	 */
	@Override
	@Transactional
	public MOSIPDeviceServiceExtDto updateMOSIPDeviceService(MOSIPDeviceServicePUTDto dto) {
		MOSIPDeviceService updMosipDeviceService =null;
		MOSIPDeviceService renEntity=null;
		MOSIPDeviceService entity = null;
		try {
			renEntity = mosipDeviceServiceRepository.findByIdAndIsDeletedFalseOrIsDeletedIsNull(dto.getId());
			if(renEntity == null) {
				throw new RequestException(MOSIPDeviceServiceErrorCode.MDS_NOT_FOUND.getErrorCode(),
						String.format(MOSIPDeviceServiceErrorCode.MDS_NOT_FOUND.getErrorMessage(),dto.getId()));		
			}
			if(deviceProviderRepository.findByIdAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(dto.getDeviceProviderId()) == null) {
				throw new RequestException(MOSIPDeviceServiceErrorCode.DEVICE_PROVIDER_NOT_FOUND.getErrorCode(),
						MOSIPDeviceServiceErrorCode.DEVICE_PROVIDER_NOT_FOUND.getErrorMessage());
			}
			
			entity = MetaDataUtils.setUpdateMetaData(dto, renEntity, false);
			entity.setIsActive(dto.getIsActive());
			
			updMosipDeviceService = mosipDeviceServiceRepository.update(entity);

			MOSIPDeviceServiceHistory entityHistory = new MOSIPDeviceServiceHistory();
			MapperUtils.map(updMosipDeviceService, entityHistory);
			MapperUtils.setBaseFieldValue(updMosipDeviceService, entityHistory);
			entityHistory.setEffectDateTime(updMosipDeviceService.getUpdatedDateTime());
			entityHistory.setCreatedDateTime(updMosipDeviceService.getUpdatedDateTime());
			mosipDeviceServiceHistoryRepository.create(entityHistory);
			
			
		} catch (DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(MOSIPDeviceServiceErrorCode.MDS_DB_UPDATION_ERROR.getErrorCode(),
					MOSIPDeviceServiceErrorCode.MDS_DB_UPDATION_ERROR.getErrorMessage() + " "
							+ ExceptionUtils.parseException(exception));
		}
		return MapperUtils.map(updMosipDeviceService, MOSIPDeviceServiceExtDto.class);
	}

}