package org.mosip.registration.processor.status.service.impl;

import org.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import org.mosip.registration.processor.status.dto.TransactionDto;
import org.mosip.registration.processor.status.entity.TransactionEntity;
import org.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import org.mosip.registration.processor.status.repositary.TransactionRepositary;
import org.mosip.registration.processor.status.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService<TransactionDto> {

	@Autowired
	TransactionRepositary transactionRepositary;

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
