package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.constant.RegistrationCenterTypeErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterTypeRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Implementation class for {@link RegistrationCenterTypeService}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Service
@Transactional
public class RegistrationCenterTypeServiceImpl implements RegistrationCenterTypeService {

	/**
	 * Autowired reference for {@link RegistrationCenterTypeRepository}.
	 */
	@Autowired
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;

	/**
	 * Autowired reference for {@link RegistrationCenteRepository}.
	 */
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterTypeService#
	 * createRegistrationCenterType(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createRegistrationCenterType(
			RegistrationCenterTypeDto registrationCenterTypeRequestDto) {
		RegistrationCenterType entity = MetaDataUtils.setCreateMetaData(registrationCenterTypeRequestDto,
				RegistrationCenterType.class);
		RegistrationCenterType registrationCenterType;
		try {
			registrationCenterType = registrationCenterTypeRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		CodeAndLanguageCodeID codeAndLanguageCodeID = new CodeAndLanguageCodeID();
		MapperUtils.map(registrationCenterType, codeAndLanguageCodeID);
		return codeAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterTypeService#
	 * updateRegistrationCenterType(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID updateRegistrationCenterType(RegistrationCenterTypeDto registrationCenterTypeDto) {
		CodeAndLanguageCodeID registrationCenterTypeId = new CodeAndLanguageCodeID();
		MapperUtils.mapFieldValues(registrationCenterTypeDto, registrationCenterTypeId);
		try {
			RegistrationCenterType registrationCenterTypeEntity = registrationCenterTypeRepository
					.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(registrationCenterTypeDto.getCode(),
							registrationCenterTypeDto.getLangCode());
			if (registrationCenterTypeEntity != null) {
				MetaDataUtils.setUpdateMetaData(registrationCenterTypeDto, registrationCenterTypeEntity, false);
				registrationCenterTypeRepository.update(registrationCenterTypeEntity);
			} else {
				throw new RequestException(
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(
					RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_UPDATE_EXCEPTION.getErrorCode(),
					RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_UPDATE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		return registrationCenterTypeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterTypeService#
	 * deleteRegistrationCenterType(java.lang.String)
	 */
	@Override
	public CodeResponseDto deleteRegistrationCenterType(String registrationCenterTypeCode) {
		try {
			List<RegistrationCenter> mappedRegistrationCenters = registrationCenterRepository
					.findByCenterTypeCode(registrationCenterTypeCode);
			if (!mappedRegistrationCenters.isEmpty()) {
				throw new MasterDataServiceException(
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_DELETE_DEPENDENCY_EXCEPTION
								.getErrorCode(),
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_DELETE_DEPENDENCY_EXCEPTION
								.getErrorMessage());
			}
			int deletedRegistrationCenterTypes = registrationCenterTypeRepository.deleteRegistrationCenterType(
					LocalDateTime.now(ZoneId.of("UTC")), registrationCenterTypeCode, MetaDataUtils.getContextUser());
			if (deletedRegistrationCenterTypes < 1) {
				throw new RequestException(
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(
					RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_DELETE_EXCEPTION.getErrorCode(),
					RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_DELETE_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		CodeResponseDto responseDto = new CodeResponseDto();
		responseDto.setCode(registrationCenterTypeCode);
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterTypeService#
	 * getAllRegistrationCenterTypes(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<RegistrationCenterTypeExtnDto> getAllRegistrationCenterTypes(int pageNumber, int pageSize,
			String sortBy, String orderBy) {
		List<RegistrationCenterTypeExtnDto> regCenterTypes = null;
		PageDto<RegistrationCenterTypeExtnDto> pageDto = null;
		try {

			Page<RegistrationCenterType> page = registrationCenterTypeRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (page != null && page.getContent() != null && !page.getContent().isEmpty()) {
				regCenterTypes = MapperUtils.mapAll(page.getContent(), RegistrationCenterTypeExtnDto.class);
				pageDto = new PageDto<>(page.getNumber(), page.getTotalPages(), page.getTotalElements(),
						regCenterTypes);
			} else {
				throw new DataNotFoundException(
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(
					RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_FETCH_EXCEPTION.getErrorCode(),
					RegistrationCenterTypeErrorCode.REGISTRATION_CENTER_TYPE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		return pageDto;
	}
}
