package io.mosip.kernel.pridgenerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.pridgenerator.constant.PRIDGeneratorConstant;
import io.mosip.kernel.pridgenerator.constant.PRIDGeneratorErrorCode;
import io.mosip.kernel.pridgenerator.constant.PridLifecycleStatus;
import io.mosip.kernel.pridgenerator.dto.PridFetchResponseDto;
import io.mosip.kernel.pridgenerator.entity.PridEntity;
import io.mosip.kernel.pridgenerator.exception.PridGeneratorServiceException;
import io.mosip.kernel.pridgenerator.repository.PridRepository;
import io.mosip.kernel.pridgenerator.service.PridService;
import io.mosip.kernel.pridgenerator.utils.ExceptionUtils;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@Service
public class PridServiceImpl implements PridService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PridServiceImpl.class);

	@Autowired
	private PridRepository pridRepository;

	@Override
	@Transactional
	public PridFetchResponseDto fetchPrid() {
		PridFetchResponseDto pridFetchResponseDto = new PridFetchResponseDto();
		PridEntity pridEntity = null;
		try {
			pridEntity = pridRepository.findFirstByStatus(PridLifecycleStatus.AVAILABLE);
		} catch (DataAccessException exception) {
			LOGGER.error(ExceptionUtils.parseException(exception));
			throw new PridGeneratorServiceException(PRIDGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					exception.getMessage(), exception.getCause());
		} catch (Exception exception) {
			LOGGER.error(ExceptionUtils.parseException(exception));
			throw new PridGeneratorServiceException(PRIDGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					exception.getMessage(), exception.getCause());
		}
		if (pridEntity != null) {
			pridFetchResponseDto.setPrid(pridEntity.getPrid());
			try {
				pridRepository.updatePrid(PridLifecycleStatus.ASSIGNED, PRIDGeneratorConstant.DEFAULTADMIN_MOSIP_IO,
						DateUtils.getUTCCurrentDateTime(), pridEntity.getPrid());
			} catch (DataAccessException exception) {
				LOGGER.error(ExceptionUtils.parseException(exception));
				throw new PridGeneratorServiceException(PRIDGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
						exception.getMessage(), exception.getCause());
			} catch (Exception exception) {
				LOGGER.error(ExceptionUtils.parseException(exception));
				throw new PridGeneratorServiceException(PRIDGeneratorErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
						exception.getMessage(), exception.getCause());
			}
		} else {
			LOGGER.info("prid not available");
			throw new PridGeneratorServiceException(PRIDGeneratorErrorCode.PRID_NOT_AVAILABLE.getErrorCode(),
					PRIDGeneratorErrorCode.PRID_NOT_AVAILABLE.getErrorMessage());
		}
		return pridFetchResponseDto;
	}

	@Override
	public long fetchPridCount(String status) {
		long pridCount = 0;
		try {
			pridCount = pridRepository.countByStatusAndIsDeletedFalse(status);
		} catch (DataAccessException exception) {
			LOGGER.error(ExceptionUtils.parseException(exception));
		} catch (Exception exception) {
			LOGGER.error(ExceptionUtils.parseException(exception));
		}
		return pridCount;

	}

	@Override
	public boolean savePRID(PridEntity prid) {

		if (!this.pridRepository.existsById(prid.getPrid())) {
			try {
				this.pridRepository.saveAndFlush(prid);
			} catch (DataAccessException exception) {
				LOGGER.error(ExceptionUtils.parseException(exception));
				return false;
			} catch (Exception exception) {
				LOGGER.error(ExceptionUtils.parseException(exception));
				return false;
			}
			return true;
		} else {
			return false;
		}

	}
}
