package org.mosip.registration.processor.status.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import org.mosip.registration.processor.status.code.TransactionConstants;
import org.mosip.registration.processor.status.dao.RegistrationStatusDao;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.dto.TransactionDto;
import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.mosip.registration.processor.status.entity.TransactionEntity;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.mosip.registration.processor.status.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegistrationStatusServiceImpl implements RegistrationStatusService<String, RegistrationStatusDto> {

	@Value("${landingZone_To_VirusScan_Interval_Threshhold_time}")

	private int threshholdTime;

	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	@Autowired
	private TransactionService<TransactionDto> transcationStatusService;

	private static final String COULD_NOT_GET = "Could not get Information from table";

	@Override
	public RegistrationStatusDto getRegistrationStatus(String registrationId) {

		TransactionDto transactionDto = new TransactionDto(generateId(), registrationId, null,
				TransactionConstants.STARTED.toString(), "Get by Registration Id operation",
				TransactionConstants.GET.toString(), "Get by Registration Id started");
		transactionDto.setReferenceId(registrationId);
		transactionDto.setReferenceIdType("Get by registration id");
		try {
			transcationStatusService.addRegistrationTransaction(transactionDto);
			RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), registrationId, null,
					TransactionConstants.SUCCESS.toString(), "Get by Registration Id",
					TransactionConstants.GET.toString(), "Get by Registration Id success");
			return entity != null ? convertEntityToDto(entity) : null;
		} catch (DataAccessLayerException e) {
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), registrationId, null,
					TransactionConstants.FAILURE.toString(), "Get by Registration Id",
					TransactionConstants.GET.toString(), "Get by Registration Id failure");

			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			transactionDto.setReferenceId(registrationId);
			transactionDto.setReferenceIdType("Get by registration id");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		}

	}

	@Override
	public List<RegistrationStatusDto> findbyfilesByThreshold(String statusCode) {

		TransactionDto transactionDto = new TransactionDto(generateId(), null, null,
				TransactionConstants.STARTED.toString(), "Find files by Threshold operation",
				TransactionConstants.GET.toString(), "Find files by Threshold started");
		transactionDto.setReferenceId(statusCode);
		transactionDto.setReferenceIdType("Find by status code");
		try {

			transcationStatusService.addRegistrationTransaction(transactionDto);
			List<RegistrationStatusEntity> entities = registrationStatusDao.findbyfilesByThreshold(statusCode,
					getThreshholdTime());
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), null, null,
					TransactionConstants.SUCCESS.toString(), "Find files by Threshold",
					TransactionConstants.GET.toString(), "Find files by Threshold success");
			return convertEntityListToDtoList(entities);
		} catch (DataAccessLayerException e) {
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), null, null,
					TransactionConstants.FAILURE.toString(), "Find files by Threshold",
					TransactionConstants.GET.toString(), "Find files by Threshold failure");
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			transactionDto.setReferenceId(statusCode);
			transactionDto.setReferenceIdType("Find by status code");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		}

	}

	@Override
	public void addRegistrationStatus(RegistrationStatusDto registrationStatusDto) {

		TransactionDto transactionDto = new TransactionDto(generateId(), registrationStatusDto.getRegistrationId(),
				null, TransactionConstants.STARTED.toString(), "Add Registration operation",
				TransactionConstants.ADD.toString(), "Add Registration started");
		transactionDto.setReferenceId(null);
		transactionDto.setReferenceIdType("Adding new registration record");
		try {
			TransactionEntity transactionEntity = transcationStatusService.addRegistrationTransaction(transactionDto);
			registrationStatusDto.setLatestRegistrationTransactionId(transactionEntity.getTransactionId());
			registrationStatusDto.setLatestTransactionTypeCode(transactionDto.getTrntypecode());
			registrationStatusDto.setLatestTransactionStatusCode(transactionDto.getStatusCode());
			registrationStatusDto.setLatestTransactionLanguageCode(transactionEntity.getLangCode());
			registrationStatusDto.setLatestRegistrationTransactionDateTime(transactionEntity.getCreateDateTime());
			RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
			registrationStatusDao.save(entity);
			transactionDto = new TransactionDto(transactionDto.getTransactionId(),
					registrationStatusDto.getRegistrationId(), null, TransactionConstants.SUCCESS.toString(),
					"Add Registration", TransactionConstants.ADD.toString(), "Add Registration Success");
		} catch (DataAccessLayerException e) {
			transactionDto = new TransactionDto(transactionDto.getTransactionId(),
					registrationStatusDto.getRegistrationId(), null, TransactionConstants.FAILURE.toString(),
					"Add Registration", TransactionConstants.ADD.toString(), "Add Enrolment failure");
			throw new TablenotAccessibleException("Could not add Information to table", e);
		} finally {
			transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
			transactionDto.setReferenceIdType("Adding new registration record");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		}
	}

	@Override
	public void updateRegistrationStatus(RegistrationStatusDto registrationStatusDto) {

		TransactionDto transactionDto = new TransactionDto(generateId(), registrationStatusDto.getRegistrationId(),
				null, TransactionConstants.STARTED.toString(), "Update Enrolment status",
				TransactionConstants.UPDATE.toString(), "Update Enrolment started");
		transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
		transactionDto.setReferenceIdType("Updating registration record");
		try {
			TransactionEntity transactionEntity = transcationStatusService.addRegistrationTransaction(transactionDto);
			registrationStatusDto.setLatestRegistrationTransactionId(transactionEntity.getTransactionId());
			registrationStatusDto.setLatestTransactionTypeCode(transactionDto.getTrntypecode());
			registrationStatusDto.setLatestTransactionStatusCode(transactionDto.getStatusCode());
			registrationStatusDto.setLatestTransactionLanguageCode(transactionEntity.getLangCode());
			registrationStatusDto.setLatestRegistrationTransactionDateTime(transactionEntity.getCreateDateTime());
			RegistrationStatusDto dto = getRegistrationStatus(registrationStatusDto.getRegistrationId());
			if (dto != null) {
				RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
				entity.setCreateDateTime(dto.getCreateDateTime());
				registrationStatusDao.save(entity);
				transactionDto = new TransactionDto(transactionDto.getTransactionId(),
						registrationStatusDto.getRegistrationId(), null, TransactionConstants.SUCCESS.toString(),
						"Update Registration", TransactionConstants.UPDATE.toString(), "Update Registration Success");
			}
		} catch (DataAccessLayerException e) {
			transactionDto = new TransactionDto(transactionDto.getTransactionId(),
					registrationStatusDto.getRegistrationId(), null, TransactionConstants.FAILURE.toString(),
					"Update Registration", TransactionConstants.UPDATE.toString(), "Update Registration failure");
			throw new TablenotAccessibleException("Could not update Information to table", e);
		} finally {
			transactionDto.setReferenceId(registrationStatusDto.getRegistrationId());
			transactionDto.setReferenceIdType("Updating registration record");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		}
	}

	@Override
	public List<RegistrationStatusDto> getByStatus(String status) {

		TransactionDto transactionDto = new TransactionDto(generateId(), null, null,
				TransactionConstants.STARTED.toString(), "Get by status operation", TransactionConstants.GET.toString(),
				"Get by status Started");
		transactionDto.setReferenceId(status);
		transactionDto.setReferenceIdType("Get records by status");
		try {
			transcationStatusService.addRegistrationTransaction(transactionDto);
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusDao
					.getEnrolmentStatusByStatusCode(status);
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), null, null,
					TransactionConstants.SUCCESS.toString(), "Get by status", TransactionConstants.GET.toString(),
					"Get by status Success");
			return convertEntityListToDtoList(registrationStatusEntityList);
		} catch (DataAccessLayerException e) {
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), null, null,
					TransactionConstants.FAILURE.toString(), "Get by status", TransactionConstants.GET.toString(),
					"Get by status failure");

			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			transactionDto.setReferenceId(status);
			transactionDto.setReferenceIdType("Get records by status");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		}

	}

	@Override
	public List<RegistrationStatusDto> getByIds(String ids) {

		TransactionDto transactionDto = new TransactionDto(generateId(), null, null,
				TransactionConstants.STARTED.toString(), "Get by ids operation", TransactionConstants.GET.toString(),
				"Get by ids Started");
		transactionDto.setReferenceId(ids);
		transactionDto.setReferenceIdType("Get records by ids");
		try {
			String[] registrationIdArray = ids.split(",");
			List<String> registrationIds = Arrays.asList(registrationIdArray);
			List<RegistrationStatusEntity> registrationStatusEntityList = registrationStatusDao
					.getByIds(registrationIds);
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), null, null,
					TransactionConstants.SUCCESS.toString(), "Get by ids", TransactionConstants.GET.toString(),
					"Get by status Success");
			return convertEntityListToDtoList(registrationStatusEntityList);

		} catch (DataAccessLayerException e) {
			transactionDto = new TransactionDto(transactionDto.getTransactionId(), null, null,
					TransactionConstants.FAILURE.toString(), "Get by ids", TransactionConstants.GET.toString(),
					"Get by ids failure");
			throw new TablenotAccessibleException(COULD_NOT_GET, e);
		} finally {
			transactionDto.setReferenceId(ids);
			transactionDto.setReferenceIdType("Get records by ids");
			transcationStatusService.addRegistrationTransaction(transactionDto);
		}
	}

	private List<RegistrationStatusDto> convertEntityListToDtoList(List<RegistrationStatusEntity> entities) {
		List<RegistrationStatusDto> list = new ArrayList<>();
		if (entities != null) {
			for (RegistrationStatusEntity entity : entities) {
				list.add(convertEntityToDto(entity));
			}

		}
		return list;
	}

	private RegistrationStatusDto convertEntityToDto(RegistrationStatusEntity entity) {
		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto();
		registrationStatusDto.setRegistrationId(entity.getRegistrationId());
		registrationStatusDto.setRegistrationType(entity.getRegistrationType());
		registrationStatusDto.setReferenceRegistrationId(entity.getReferenceRegistrationId());
		registrationStatusDto.setStatusCode(entity.getStatusCode());
		registrationStatusDto.setLangCode(entity.getLangCode());
		registrationStatusDto.setStatusComment(entity.getStatusComment());
		registrationStatusDto.setLatestRegistrationTransactionId(entity.getLatestRegistrationTransactionId());
		registrationStatusDto.setLatestTransactionTypeCode(entity.getLatestTransactionTypeCode());
		registrationStatusDto.setLatestTransactionStatusCode(entity.getLatestTransactionStatusCode());
		registrationStatusDto.setLatestTransactionLanguageCode(entity.getLatestTransactionLanguageCode());
		registrationStatusDto
				.setLatestRegistrationTransactionDateTime(entity.getLatestRegistrationTransactionDateTime());
		registrationStatusDto.setIsActive(entity.isActive());
		registrationStatusDto.setCreatedBy(entity.getCreatedBy());
		registrationStatusDto.setCreateDateTime(entity.getCreateDateTime());
		registrationStatusDto.setUpdatedBy(entity.getUpdatedBy());
		registrationStatusDto.setUpdateDateTime(entity.getUpdateDateTime());
		registrationStatusDto.setIsDeleted(entity.isDeleted());
		registrationStatusDto.setDeletedDateTime(entity.getDeletedDateTime());
		registrationStatusDto.setRetryCount(entity.getRetryCount());
		return registrationStatusDto;
	}

	private RegistrationStatusEntity convertDtoToEntity(RegistrationStatusDto dto) {
		RegistrationStatusEntity registrationStatusEntity = new RegistrationStatusEntity();
		registrationStatusEntity.setRegistrationId(dto.getRegistrationId());
		registrationStatusEntity.setRegistrationType(dto.getRegistrationType());
		registrationStatusEntity.setReferenceRegistrationId(dto.getReferenceRegistrationId());
		registrationStatusEntity.setStatusCode(dto.getStatusCode());
		registrationStatusEntity.setLangCode(dto.getLangCode());
		registrationStatusEntity.setStatusComment(dto.getStatusComment());
		registrationStatusEntity.setLatestRegistrationTransactionId(dto.getLatestRegistrationTransactionId());
		registrationStatusEntity.setLatestTransactionTypeCode(dto.getLatestTransactionTypeCode());
		registrationStatusEntity.setLatestTransactionStatusCode(dto.getLatestTransactionStatusCode());
		registrationStatusEntity.setLatestTransactionLanguageCode(dto.getLatestTransactionLanguageCode());
		registrationStatusEntity
				.setLatestRegistrationTransactionDateTime(dto.getLatestRegistrationTransactionDateTime());
		registrationStatusEntity.setIsActive(dto.isActive());
		registrationStatusEntity.setCreatedBy(dto.getCreatedBy());
		registrationStatusEntity.setCreateDateTime(dto.getCreateDateTime());
		registrationStatusEntity.setUpdatedBy(dto.getUpdatedBy());
		registrationStatusEntity.setUpdateDateTime(dto.getUpdateDateTime());
		registrationStatusEntity.setIsDeleted(dto.isDeleted());
		registrationStatusEntity.setDeletedDateTime(dto.getDeletedDateTime());
		registrationStatusEntity.setRetryCount(dto.getRetryCount());
		return registrationStatusEntity;
	}

	public int getThreshholdTime() {
		return this.threshholdTime;
	}

	public String generateId() {
		return UUID.randomUUID().toString();
	}

}
