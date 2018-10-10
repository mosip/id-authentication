package io.mosip.registration.service.impl;

import java.time.OffsetDateTime;
import java.util.Random;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.core.spi.idgenerator.MosipPridGenerator;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.code.AuditLogTempConstant;
import io.mosip.registration.code.FormType;
import io.mosip.registration.dao.RegistrationDao;
import io.mosip.registration.dto.AddressDto;
import io.mosip.registration.dto.ContactDto;
import io.mosip.registration.dto.NameDto;
import io.mosip.registration.dto.RegistrationDto;
import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.entity.RegistrationEntity;
import io.mosip.registration.service.RegistrationService;
import io.mosip.registration.core.exceptions.TablenotAccessibleException;
import io.mosip.registration.core.generator.MosipGroupIdGenerator;

@Component
public class RegistrationServiceImpl implements RegistrationService<String, RegistrationDto> {

	@Autowired
	private RegistrationDao registrationDao;

	private static final String COULD_NOT_GET = "Could not get Information from table";

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private MosipPridGenerator<String> pridGenerator;

	@Override
	public RegistrationDto getRegistration(String userID) {

		createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
				AuditLogTempConstant.APPLICATION_NAME.toString(), "", AuditLogTempConstant.EVENT_ID.toString(),
				AuditLogTempConstant.EVENT_TYPE.toString(), AuditLogTempConstant.EVENT_TYPE.toString());
		return null;
	}

	@Override
	public ResponseDto addRegistration(RegistrationDto registrationDto, String groupId) {
		RegistrationEntity entity = convertDtoToEntity(registrationDto);
		ResponseDto response = new ResponseDto();

		if (registrationDto.getPreRegistrationId().isEmpty()) {
			String prid = pridGenerator.generateId();
			entity.setPreRegistrationId(prid);
			entity.setGroupId(groupId);
			try {
				registrationDao.save(entity);
			} catch (DataAccessLayerException e) {
				throw new TablenotAccessibleException("Could not add Information to table", e);
			}

		} else {
			try {
				registrationDao.save(entity);
			} catch (DataAccessLayerException e) {
				throw new TablenotAccessibleException("Could not add Information to table", e);
			}
		}

		// createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
		// AuditLogTempConstant.APPLICATION_NAME.toString(), "",
		// AuditLogTempConstant.EVENT_ID.toString(),
		// AuditLogTempConstant.EVENT_TYPE.toString(),
		// AuditLogTempConstant.EVENT_TYPE.toString());

		response.setPrId(entity.getPreRegistrationId());
		response.setCreateDateTime(entity.getCreateDateTime());
		response.setCreatedBy(entity.getCreatedBy());
		response.setGroupId(entity.getGroupId());
		response.setIsPrimary(entity.getIsPrimary());
		response.setUpdateDateTime(entity.getUpdateDateTime());
		response.setUpdatedBy(entity.getUpdatedBy());
		return response;

	}


	@Override
	public void updateRegistration(RegistrationDto registrationDto) {

		createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
				AuditLogTempConstant.APPLICATION_NAME.toString(), "", AuditLogTempConstant.EVENT_ID.toString(),
				AuditLogTempConstant.EVENT_TYPE.toString(), AuditLogTempConstant.EVENT_TYPE.toString());

	}

	public int getThreshholdTime() {
		return this.getThreshholdTime();
	}

	public String generateId() {
		return UUID.randomUUID().toString();
	}

	private RegistrationEntity convertDtoToEntity(RegistrationDto dto) {
		RegistrationEntity registrationEntity = new RegistrationEntity();
		registrationEntity.setAddrLine1(dto.getAddress().getAddrLine1());
		registrationEntity.setAddrLine2(dto.getAddress().getAddrLine2());
		registrationEntity.setAddrLine3(dto.getAddress().getAddrLine3());
		registrationEntity.setAge(dto.getAge());
		registrationEntity.setApplicantType(dto.getApplicantType());
		registrationEntity.setCreateDateTime(dto.getCreateDateTime());
		registrationEntity.setCreatedBy(dto.getCreatedBy());
		registrationEntity.setDeletedDateTime(dto.getDeletedDateTime());
		registrationEntity.setDob(dto.getDob());
		registrationEntity.setPreRegistrationId(dto.getPreRegistrationId());
		registrationEntity.setGroupId(dto.getGroupId());
		registrationEntity.setEmail(dto.getContact().getEmail());
		registrationEntity.setFamilyname(dto.getName().getFamilyname());
		registrationEntity.setFirstname(dto.getName().getFirstname());
		registrationEntity.setForename(dto.getName().getForename());
		registrationEntity.setGenderCode(dto.getGenderCode());
		registrationEntity.setGivenname(dto.getName().getGivenname());
		registrationEntity.setIsPrimary(dto.getIsPrimary());
		registrationEntity.setIsDeleted(dto.getIsDeleted());
		registrationEntity.setLangCode(dto.getLangCode());
		registrationEntity.setLastname(dto.getName().getLastname());
		registrationEntity.setLocationCode(dto.getAddress().getLocationCode());
		registrationEntity.setMiddleinitial(dto.getName().getMiddleinitial());
		registrationEntity.setMiddlename(dto.getName().getMiddlename());
		registrationEntity.setMobile(dto.getContact().getMobile());
		registrationEntity.setParentFullName(dto.getParentFullName());
		registrationEntity.setParentRefId(dto.getParentRefId());
		registrationEntity.setStatusCode(dto.getStatusCode());
		registrationEntity.setSurname(dto.getName().getSurname());
		registrationEntity.setUpdateDateTime(dto.getUpdateDateTime());
		registrationEntity.setUpdatedBy(dto.getUpdatedBy());
		return registrationEntity;
	}

	private RegistrationDto convertToDTO(RegistrationEntity entity) {
		RegistrationDto regDto = new RegistrationDto();
		NameDto nameDto = new NameDto();
		ContactDto contactDto = new ContactDto();
		AddressDto addrDto = new AddressDto();

		nameDto.setFamilyname(entity.getFamilyname());
		nameDto.setFirstname(entity.getFirstname());
		nameDto.setForename(entity.getForename());
		nameDto.setFullname(entity.getFullname());
		nameDto.setGivenname(entity.getGivenname());
		nameDto.setLastname(entity.getLastname());
		nameDto.setMiddleinitial(entity.getMiddleinitial());
		nameDto.setMiddlename(entity.getMiddlename());
		nameDto.setSurname(entity.getSurname());

		contactDto.setEmail(entity.getEmail());
		contactDto.setMobile(entity.getMobile());

		addrDto.setAddrLine1(entity.getAddrLine1());
		addrDto.setAddrLine2(entity.getAddrLine2());
		addrDto.setAddrLine3(entity.getAddrLine3());
		addrDto.setLocationCode(entity.getLocationCode());

		regDto.setAddress(addrDto);
		regDto.setContact(contactDto);
		regDto.setName(nameDto);

		regDto.setAge(entity.getAge());
		regDto.setApplicantType(entity.getApplicantType());
		regDto.setCreateDateTime(entity.getCreateDateTime());
		regDto.setCreatedBy(entity.getCreatedBy());
		regDto.setDeletedDateTime(entity.getDeletedDateTime());
		regDto.setDob(entity.getDob());
		regDto.setGenderCode(entity.getGenderCode());
		regDto.setGroupId(entity.getGroupId());
		regDto.setNationalid(entity.getNationalid());
		regDto.setParentFullName(entity.getParentFullName());
		regDto.setParentRefId(entity.getParentRefId());
		regDto.setParentRefIdType(entity.getParentRefIdType());
		regDto.setPreRegistrationId(entity.getPreRegistrationId());
		regDto.setStatusCode(entity.getStatusCode());
		regDto.setUpdateDateTime(entity.getUpdateDateTime());
		regDto.setUpdatedBy(entity.getUpdatedBy());
		regDto.setIsPrimary(entity.getIsPrimary());
		return regDto;

	}

	public void createAuditRequestBuilder(String applicationId, String applicationName, String description,
			String eventId, String eventName, String eventType) {
		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId(applicationId)
				.setApplicationName(applicationName).setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
				.setDescription(description).setEventId(eventId).setEventName(eventName).setEventType(eventType)
				.setHostIp(AuditLogTempConstant.HOST_IP.toString())
				.setHostName(AuditLogTempConstant.HOST_NAME.toString()).setId(AuditLogTempConstant.ID.toString())
				.setIdType(AuditLogTempConstant.ID_TYPE.toString())
				.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
				.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
				.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
				.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());

		AuditRequestDto auditRequestDto = auditRequestBuilder.build();
		auditHandler.writeAudit(auditRequestDto);
	}

}
