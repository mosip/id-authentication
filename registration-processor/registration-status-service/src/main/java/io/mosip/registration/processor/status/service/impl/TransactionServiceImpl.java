package io.mosip.registration.processor.status.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.TransactionEntity;
import io.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
import io.mosip.registration.processor.status.service.TransactionService;

// TODO: Auto-generated Javadoc
/**
 * The Class TransactionServiceImpl.
 */
@Service
public class TransactionServiceImpl implements TransactionService<TransactionDto> {

	/** The transaction repositary. */
	@Autowired
	RegistrationRepositary<TransactionEntity, String> transactionRepositary;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.status.service.TransactionService#
	 * addRegistrationTransaction(java.lang.Object)
	 */
	@Override
	public TransactionEntity addRegistrationTransaction(TransactionDto transactionStatusDto) {
		try {
			TransactionEntity entity = convertDtoToEntity(transactionStatusDto);
			return transactionRepositary.save(entity);
		} catch (DataAccessLayerException e) {
			throw new TransactionTableNotAccessibleException(
					PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		}

	}

	/**
	 * Convert dto to entity.
	 *
	 * @param dto
	 *            the dto
	 * @return the transaction entity
	 */
	private TransactionEntity convertDtoToEntity(TransactionDto dto) {
		TransactionEntity transcationEntity = new TransactionEntity(dto.getTransactionId(), dto.getRegistrationId(),
				dto.getParentid(), dto.getTrntypecode(), dto.getStatusCode(), dto.getStatusComment());
		transcationEntity.setRemarks(dto.getRemarks());
		transcationEntity.setStatusComment(dto.getStatusComment());
		transcationEntity.setCreatedBy("MOSIP_SYSTEM");
		transcationEntity.setLangCode("eng");
		transcationEntity.setReferenceId(dto.getReferenceId());
		transcationEntity.setReferenceIdType(dto.getReferenceIdType());
		return transcationEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.status.service.TransactionService#
	 * getTransactionByRegIdAndStatusCode(java.lang.String, java.lang.String)
	 */
	@Override
	public TransactionDto getTransactionByRegIdAndStatusCode(String regId, String statusCode) {
		TransactionDto dto = null;
		List<TransactionEntity> transactionEntityList = transactionRepositary.getTransactionByRegIdAndStatusCode(regId,
				statusCode);
		if (transactionEntityList != null) {
			dto = convertEntityToDto(transactionEntityList.get(0));
		}

		return dto;
	}

	/**
	 * Convert entity to dto.
	 *
	 * @param entity
	 *            the entity
	 * @return the transaction dto
	 */
	private TransactionDto convertEntityToDto(TransactionEntity entity) {
		return new TransactionDto(entity.getId(), entity.getRegistrationId(), entity.getParentid(),
				entity.getTrntypecode(), entity.getRemarks(), entity.getStatusCode(), entity.getStatusComment());

	}
}
