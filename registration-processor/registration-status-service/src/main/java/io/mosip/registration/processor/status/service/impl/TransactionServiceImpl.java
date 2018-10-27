package io.mosip.registration.processor.status.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.dataaccess.hibernate.exception.DataAccessLayerException;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.TransactionEntity;
import io.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
import io.mosip.registration.processor.status.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService<TransactionDto> {

	@Autowired
	RegistrationRepositary<TransactionEntity, String> transactionRepositary;

	@Override
	public TransactionEntity addRegistrationTransaction(TransactionDto transactionStatusDto) {
		try {
			TransactionEntity entity = convertDtoToEntity(transactionStatusDto);
			return transactionRepositary.save(entity);
		} catch (DataAccessLayerException e) {
			throw new TransactionTableNotAccessibleException("Could not add Information to Transaction table", e);
		}

	}

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
}
