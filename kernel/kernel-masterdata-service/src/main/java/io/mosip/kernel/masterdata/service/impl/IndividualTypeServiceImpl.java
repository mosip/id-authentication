package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.IndividualTypeErrorCode;
import io.mosip.kernel.masterdata.dto.IndividualTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.IndividualTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.IndividualTypeExtnDto;
import io.mosip.kernel.masterdata.entity.IndividualType;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.IndividualTypeRepository;
import io.mosip.kernel.masterdata.service.IndividualTypeService;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * @author Bal Vikash Sharma
 *
 */
@Service
public class IndividualTypeServiceImpl implements IndividualTypeService {

	@Autowired
	private IndividualTypeRepository individualTypeRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.IndividualTypeService#
	 * getAllIndividualTypes()
	 */
	@Override
	public IndividualTypeResponseDto getAllIndividualTypes() {
		IndividualTypeResponseDto responseDto = new IndividualTypeResponseDto();
		try {
			List<IndividualType> list = individualTypeRepository.findAll();
			if (!EmptyCheckUtils.isNullEmpty(list)) {
				for (IndividualType individualType : list) {
					responseDto.getIndividualTypes().add(MapperUtils.map(individualType, new IndividualTypeDto()));
				}
			} else {
				throw new DataNotFoundException(
						IndividualTypeErrorCode.NO_INDIVIDUAL_TYPE_FOUND_EXCEPTION.getErrorCode(),
						IndividualTypeErrorCode.NO_INDIVIDUAL_TYPE_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(IndividualTypeErrorCode.INDIVIDUAL_TYPE_FETCH_EXCEPTION.getErrorCode(),
					IndividualTypeErrorCode.INDIVIDUAL_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.IndividualTypeService#getIndividualTypes(
	 * int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<IndividualTypeExtnDto> getIndividualTypes(int pageNumber, int pageSize, String sortBy,
			String orderBy) {
		List<IndividualTypeExtnDto> individualTypes = null;
		PageDto<IndividualTypeExtnDto> pageDto = null;
		try {
			Page<IndividualType> pageData = individualTypeRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				individualTypes = MapperUtils.mapAll(pageData.getContent(), IndividualTypeExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						individualTypes);
			} else {
				throw new DataNotFoundException(
						IndividualTypeErrorCode.NO_INDIVIDUAL_TYPE_FOUND_EXCEPTION.getErrorCode(),
						IndividualTypeErrorCode.NO_INDIVIDUAL_TYPE_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(IndividualTypeErrorCode.INDIVIDUAL_TYPE_FETCH_EXCEPTION.getErrorCode(),
					IndividualTypeErrorCode.INDIVIDUAL_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}
		return pageDto;
	}

}
