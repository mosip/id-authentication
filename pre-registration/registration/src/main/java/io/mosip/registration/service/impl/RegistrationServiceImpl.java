package io.mosip.registration.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idgenerator.MosipPridGenerator;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.core.exceptions.DatabaseOperationException;
import io.mosip.registration.core.exceptions.TablenotAccessibleException;
import io.mosip.registration.dao.RegistrationDao;
import io.mosip.registration.dto.RegistrationDto;
import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.dto.ViewRegistrationResponseDto;
import io.mosip.registration.entity.RegistrationEntity;
import io.mosip.registration.exception.OperationNotAllowedException;
import io.mosip.registration.exception.utils.RegistrationErrorMessages;
import io.mosip.registration.repositary.DocumentRepository;
import io.mosip.registration.repositary.RegistrationRepositary;
import io.mosip.registration.service.RegistrationService;

@Component
public class RegistrationServiceImpl implements RegistrationService<String, RegistrationDto> {

	@Autowired
	private DocumentRepository documentRepository;

	/**
	 * Field for {@link #RegistrationDao}
	 */
	@Autowired
	private RegistrationDao registrationDao;

	/**
	 * Field for {@link #MosipPridGenerator<String>}
	 */
	@Autowired
	private MosipPridGenerator<String> pridGenerator;

	/**
	 * Field for {@link #RegistrationRepositary}
	 */
	@Autowired
	private RegistrationRepositary registrationRepositary;

	@Override
	public ResponseDto addRegistration(RegistrationDto registrationDto, String groupId)
			throws TablenotAccessibleException {
		RegistrationEntity entity = convertDtoToEntity(registrationDto);
		ResponseDto response = new ResponseDto();
		try {
			if (registrationDto.getPreRegistrationId().isEmpty()) {
				String prid = pridGenerator.generateId();
				entity.setPreRegistrationId(prid);
				entity.setGroupId(groupId);
				registrationDao.save(entity);
			} else {
				registrationDao.save(entity);

			}
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException("Could not add Information to table", e);
		}

		response.setPrId(entity.getPreRegistrationId());
		response.setCreateDateTime(entity.getCreateDateTime());
		response.setCreatedBy(entity.getCreatedBy());
		response.setGroupId(entity.getGroupId());
		response.setIsPrimary(entity.getIsPrimary());
		response.setUpdateDateTime(entity.getUpdateDateTime());
		response.setUpdatedBy(entity.getUpdatedBy());
		return response;

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
		registrationEntity.setUserId(dto.getUserId());
		return registrationEntity;
	}

	/**
	 * This Method is used to fetch all the applications created by User
	 * 
	 * @param userId
	 *            pass a userId through which user has logged in which can be either
	 *            email Id or phone number
	 * @return List of groupIds
	 * 
	 */
	@Override
	public List<ViewRegistrationResponseDto> getApplicationDetails(String userId) throws TablenotAccessibleException {

		List<ViewRegistrationResponseDto> response = new ArrayList<ViewRegistrationResponseDto>();

		int minCreateDateIndex = 0;

		try {
			List<String> groupIds = registrationRepositary.noOfGroupIds(userId);

			for (int j = 0; j < groupIds.size(); j++) {
				ViewRegistrationResponseDto responseDto = new ViewRegistrationResponseDto();

				List<RegistrationEntity> groupIdDetails = registrationRepositary.findBygroupId(groupIds.get(j));
				Timestamp maxDate = groupIdDetails.stream().map(RegistrationEntity::getUpdateDateTime)
						.max(Date::compareTo).get();
				Timestamp minDate = groupIdDetails.stream().map(RegistrationEntity::getCreateDateTime)
						.min(Date::compareTo).get();

				responseDto.setUpd_dtimesz(maxDate.toString());
				responseDto.setNoOfRecords(groupIdDetails.size());
				for (int i = 0; i < groupIdDetails.size(); i++) {

					if (groupIdDetails.get(i).getStatusCode().equalsIgnoreCase("Draft")) {
						responseDto.setStatus_code("Draft");

					} else {
						responseDto.setStatus_code(groupIdDetails.get(0).getStatusCode());
					}

					if (groupIdDetails.get(i).getCreateDateTime().equals(minDate)) {
						minCreateDateIndex = i;

					}
					responseDto.setGroup_id(groupIdDetails.get(i).getGroupId());
					if (groupIdDetails.get(i).getIsPrimary() == true) {
						responseDto.setFirstname(groupIdDetails.get(i).getFirstname());
					} else {
						responseDto.setFirstname(groupIdDetails.get(minCreateDateIndex).getFirstname());
					}

				}

				response.add(responseDto);
			}
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(RegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e);

		}

		return response;
	}

	/**
	 * This Method is used to fetch status of particular groupId
	 * 
	 * @param groupId
	 * @return Map which will contain all PreRegistraton Ids in the group and status
	 * 
	 * 
	 */
	@Override
	public Map<String, String> getApplicationStatus(String groupId) throws TablenotAccessibleException {

		List<RegistrationEntity> details = new ArrayList<>();
		try {
			details = registrationRepositary.findBygroupId(groupId);
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(RegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, e);
		}
		Map<String, String> response = details.stream()
				.collect(Collectors.toMap(RegistrationEntity::getPreRegistrationId, RegistrationEntity::getStatusCode));

		return response;
	}

	/**
	 * This Method is used to delete the Individual Application and documents
	 * associated with it
	 * 
	 * @param groupId
	 * @param list
	 *            of preRegistrationIds
	 * 
	 * 
	 */
	@Override
	public List<ResponseDto> deleteIndividual(String groupId, List<String> preregIds) {
		List<ResponseDto> responseList = new ArrayList<>();
		try {
			for (String preregId : preregIds) {
				ResponseDto responseDto = new ResponseDto();
				RegistrationEntity applicant = registrationRepositary.findByGroupIdAndPreRegistrationId(groupId,
						preregId);
				if (applicant.getStatusCode().equals("Draft")) {
					if (!applicant.getIsPrimary()) {
						documentRepository.deleteAllByPreregId(preregId);
						registrationRepositary.deleteByGroupIdAndPreRegistrationId(groupId, preregId);
						responseDto.setGroupId(groupId);
						responseDto.setPrId(applicant.getPreRegistrationId());
						responseDto.setCreateDateTime(applicant.getCreateDateTime());
						responseDto.setCreatedBy(applicant.getCreatedBy());
						responseDto.setUpdateDateTime(applicant.getUpdateDateTime());
						responseDto.setUpdatedBy(applicant.getUpdatedBy());
						responseList.add(responseDto);
					} else {
						throw new OperationNotAllowedException(
								RegistrationErrorMessages.DELETE_OPERATION_NOT_ALLOWED_PRIMARY);
					}
				} else {
					throw new OperationNotAllowedException(
							RegistrationErrorMessages.DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT);
				}
			}
		} catch (DataAccessLayerException e) {
			throw new DatabaseOperationException("Failed to delete the appliation", e);
		}
		return responseList;
	}

	/**
	 * This Method is used to delete the Group Applications and documents associated
	 * with it
	 * 
	 * @param groupId
	 * 
	 * 
	 */
	@Override
	public List<ResponseDto> deleteGroup(String groupId) {
		List<ResponseDto> responseList = new ArrayList<>();
		try {
			List<RegistrationEntity> applications = registrationRepositary.findBygroupId(groupId);
			for (RegistrationEntity application : applications) {
				ResponseDto responseDto = new ResponseDto();
				responseDto.setGroupId(groupId);
				responseDto.setPrId(application.getPreRegistrationId());
				responseDto.setCreateDateTime(application.getCreateDateTime());
				responseDto.setCreatedBy(application.getCreatedBy());
				responseDto.setUpdateDateTime(application.getUpdateDateTime());
				responseDto.setUpdatedBy(application.getUpdatedBy());
				responseList.add(responseDto);
				documentRepository.deleteAllByPreregId(application.getPreRegistrationId());
			}
			registrationRepositary.deleteAllBygroupId(groupId);
		} catch (DataAccessLayerException e) {
			throw new DatabaseOperationException("Failed to delete the appliation", e);
		}
		return responseList;
	}

}
