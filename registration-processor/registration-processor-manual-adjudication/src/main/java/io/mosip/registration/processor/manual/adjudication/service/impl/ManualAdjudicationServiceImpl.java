package io.mosip.registration.processor.manual.adjudication.service.impl;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.manual.adjudication.dao.ManualAdjudicationDao;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationStatus;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.adjudication.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.adjudication.exception.NoRecordAssignedException;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.mosip.registration.processor.manual.adjudication.util.StatusMessage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@Component
public class ManualAdjudicationServiceImpl implements ManualAdjudicationService {
	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(ManualAdjudicationServiceImpl.class);
	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";
	/** The audit log request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	@Autowired
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	@Autowired
	ManualAdjudicationDao manualAdjudicationDao;

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService#assignStatus(io.mosip.registration.processor.manual.adjudication.dto.UserDto)
	 */
	@Override
	public ManualVerificationDTO assignStatus(UserDto dto) {
		ManualVerificationDTO manualVerificationDTO = new ManualVerificationDTO();

		ManualVerificationEntity entity= manualAdjudicationDao.getAssignedApplicantDetails(dto.getUserId(), ManualVerificationStatus.ASSIGNED.name());
		if(entity!=null) {
			manualVerificationDTO.setRegId(entity.getPkId().getRegId());
			manualVerificationDTO.setMatchedRefId(entity.getPkId().getMatchedRefId());
			manualVerificationDTO.setMatchedRefType(entity.getPkId().getMatchedRefType());
			manualVerificationDTO.setMvUsrId(entity.getMvUsrId());
			manualVerificationDTO.setStatusCode(entity.getStatusCode());
		}
		else {
			ManualVerificationEntity manualVerificationEntity = manualAdjudicationDao.getFirstApplicantDetails(ManualVerificationStatus.PENDING.name()).get(0);
			if(manualVerificationEntity.getStatusCode().equals(ManualVerificationStatus.PENDING.name())) {
				manualVerificationEntity.setStatusCode(ManualVerificationStatus.ASSIGNED.name());
				manualVerificationEntity.setMvUsrId(dto.getUserId());
				ManualVerificationEntity updatedManualVerificationEntity = manualAdjudicationDao.update(manualVerificationEntity);
				if(updatedManualVerificationEntity!=null) {
					manualVerificationDTO.setRegId(updatedManualVerificationEntity.getPkId().getRegId());
					manualVerificationDTO.setMatchedRefId(updatedManualVerificationEntity.getPkId().getMatchedRefId());
					manualVerificationDTO.setMatchedRefType(updatedManualVerificationEntity.getPkId().getMatchedRefType());
					manualVerificationDTO.setMvUsrId(updatedManualVerificationEntity.getMvUsrId());
					manualVerificationDTO.setStatusCode(updatedManualVerificationEntity.getStatusCode());
				}

			}
		
		}
			
		return manualVerificationDTO;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService#getApplicantFile(java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] getApplicantFile(String regId, String fileName) {
		byte[] file = null;
		InputStream fileInStream = null;
		if (fileName.equals(PacketFiles.APPLICANTPHOTO.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.APPLICANTPHOTO);
		} else if (fileName.equals(PacketFiles.PROOFOFADDRESS.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.PROOFOFADDRESS);
		} else if (fileName.equals(PacketFiles.PROOFOFIDENTITY.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.PROOFOFIDENTITY);
		} else if (fileName.equals(PacketFiles.EXCEPTIONPHOTO.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.EXCEPTIONPHOTO);
		} else if (fileName.equals(PacketFiles.RIGHTPALM.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.RIGHTPALM);
		} else if (fileName.equals(PacketFiles.LEFTPALM.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.LEFTPALM);
		} else if (fileName.equals(PacketFiles.BOTHTHUMBS.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.BOTHTHUMBS);
		} else if (fileName.equals(PacketFiles.LEFTEYE.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.LEFTEYE);
		} else if (fileName.equals(PacketFiles.RIGHTEYE.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.RIGHTEYE);
		} else {
			throw new InvalidFileNameException(PlatformErrorMessages.RPR_MVS_INVALID_FILE_REQUEST.getCode(),
					PlatformErrorMessages.RPR_MVS_INVALID_FILE_REQUEST.getMessage());
		}
		try {
			file = IOUtils.toByteArray(fileInStream);
		} catch (IOException e) {
			// TODO
		}
		return file;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService#getApplicantData(java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] getApplicantData(String regId, String fileName) {
		byte[] file = null;
		InputStream fileInStream = null;
		if (fileName.equals(PacketFiles.DEMOGRAPHICINFO.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.DEMOGRAPHICINFO);
		} else if (fileName.equals(PacketFiles.PACKETMETAINFO.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.PACKETMETAINFO);
		} else {
			throw new InvalidFileNameException(PlatformErrorMessages.RPR_MVS_INVALID_FILE_REQUEST.getCode(),
					PlatformErrorMessages.RPR_MVS_INVALID_FILE_REQUEST.getMessage());
		}
		try {
			file = IOUtils.toByteArray(fileInStream);
		} catch (IOException e) {
			// TODO
		}
		return file;
	}

	
	
	
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService#updatePacketStatus(io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationDTO)
	 */
	@Override
	public ManualVerificationDTO updatePacketStatus(ManualVerificationDTO manualVerificationDTO) {

		// TODO Update the status either approved or rejected coming from front end corresponding to a reg id and mvUserId
		String registrationId = manualVerificationDTO.getRegId();
		
		String description = "";
		boolean isTransactionSuccessful = false;
		ManualVerificationEntity manualVerificationEntity;
		if (!manualVerificationDTO.getStatusCode().equalsIgnoreCase(ManualVerificationStatus.REJECTED.name())&& !manualVerificationDTO.getStatusCode().equalsIgnoreCase(ManualVerificationStatus.APPROVED.name())) {
			throw new InvalidUpdateException(PlatformErrorMessages.RPR_MVS_INVALID_STATUS_UPDATE.getCode(),
					PlatformErrorMessages.RPR_MVS_INVALID_STATUS_UPDATE.getMessage());
		}
		manualVerificationEntity = manualAdjudicationDao.getByRegId(manualVerificationDTO.getRegId(), manualVerificationDTO.getMatchedRefId(), manualVerificationDTO.getMvUsrId());
		if (manualVerificationEntity == null) {
			throw new NoRecordAssignedException(PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getCode(),
					PlatformErrorMessages.RPR_MVS_NO_ASSIGNED_RECORD.getMessage());
		}
		try {
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
		manualVerificationEntity.setStatusCode(manualVerificationDTO.getStatusCode());
			if(manualVerificationDTO.getStatusCode().equalsIgnoreCase("APPROVED"))
			{
				registrationStatusDto.setStatusComment(StatusMessage.MANUAL_VERFICATION_PACKET_APPROVED);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.MANUAL_ADJUDICATION_SUCCESS.toString());
				isTransactionSuccessful = true;
				description = "Manual verification approved for registration id : " + registrationId;
			}
			else
			{
				registrationStatusDto.setStatusCode(RegistrationStatusCode.MANUAL_ADJUDICATION_FAILED.toString());
				registrationStatusDto.setStatusComment(StatusMessage.MANUAL_VERFICATION_PACKET_REJECTED);
				description = "Manual verification rejected for registration id : " + registrationId;
			}
			manualAdjudicationDao.update(manualVerificationEntity);
			manualVerificationEntity = manualAdjudicationDao.getAssignedApplicantDetails(manualVerificationDTO.getMvUsrId(), ManualVerificationStatus.ASSIGNED.name());
			if(manualVerificationEntity!=null) {
				manualVerificationDTO.setRegId(manualVerificationEntity.getPkId().getRegId());
				manualVerificationDTO.setMatchedRefId(manualVerificationEntity.getPkId().getMatchedRefId());
				manualVerificationDTO.setMatchedRefType(manualVerificationEntity.getPkId().getMatchedRefType());
				manualVerificationDTO.setMvUsrId(manualVerificationEntity.getMvUsrId());
				manualVerificationDTO.setStatusCode(manualVerificationEntity.getStatusCode());
			}
			else {
				UserDto userDto = new UserDto();
				userDto.setUserId(manualVerificationDTO.getMvUsrId());
				userDto.setOffice(manualVerificationDTO.getOffice());
				userDto.setStatus(ManualVerificationStatus.PENDING.name());
				userDto.setName(manualVerificationDTO.getName());
				manualVerificationDTO = assignStatus(userDto);
				if(manualVerificationDTO==null) {
					throw new NoRecordAssignedException("no record", "no record");
				}
			}
			
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		} 
		
		finally
		{
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);
		}
		
		return manualVerificationDTO;
	}
	
}
