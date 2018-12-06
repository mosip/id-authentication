package io.mosip.registration.processor.manual.adjudication.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.manual.adjudication.dao.ManualAdjudicationDao;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationStatus;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.exception.FileNotPresentException;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.mosip.registration.processor.manual.adjudication.util.StatusMessage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;


/**
 * 
 * @author M1049617
 *
 */
@RefreshScope
@Service
public class ManualAdjudicationServiceImpl implements ManualAdjudicationService {
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

	@Override
	public ManualVerificationDTO assignStatus(UserDto dto) {
		ManualVerificationDTO manualVerificationDTO = new ManualVerificationDTO();
		ManualVerificationEntity manualVerificationEntity = manualAdjudicationDao.getFirstApplicantDetails().get(0);
		if (manualVerificationEntity.getStatusCode().equals(ManualVerificationStatus.PENDING.name())) {
			manualVerificationEntity.setStatusCode(ManualVerificationStatus.ASSIGNED.name());
			manualVerificationEntity.setMvUsrId(dto.getUserId());
			ManualVerificationEntity updatedManualVerificationEntity = manualAdjudicationDao.update(manualVerificationEntity);
			if(updatedManualVerificationEntity!=null) {
				manualVerificationDTO.setRegId(updatedManualVerificationEntity.getId().getRegId());
				manualVerificationDTO.setMatchedRefId(updatedManualVerificationEntity.getId().getMatchedRefId());
				manualVerificationDTO.setMatchedRefType(updatedManualVerificationEntity.getId().getMatchedRefType());
				manualVerificationDTO.setMvUsrId(updatedManualVerificationEntity.getMvUsrId());
				manualVerificationDTO.setStatusCode(updatedManualVerificationEntity.getStatusCode());
			}
		}
		return manualVerificationDTO;
	}

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
			throw new FileNotPresentException("INVALID FILE NAME REQUESTED");
		}
		try {
			file = IOUtils.toByteArray(fileInStream);
		} catch (IOException e) {
			// TODO Catch Exceptions
		}
		return file;
	}

	@Override
	public byte[] getApplicantData(String regId, String fileName) {
		byte[] file = null;
		InputStream fileInStream = null;
		if (fileName.equals(PacketFiles.DEMOGRAPHICINFO.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.DEMOGRAPHICINFO);
		} else if (fileName.equals(PacketFiles.PACKETMETAINFO.name())) {
			fileInStream = filesystemCephAdapterImpl.getFile(regId, PacketStructure.PACKETMETAINFO);
		} else {
			//TODO Create a Error Code and Error message to remove Hard coded exception value
			throw new FileNotPresentException("INVALID FILE NAME REQUESTED");
		}
		try {
			file = IOUtils.toByteArray(fileInStream);
		} catch (IOException e) {
			// TODO Catch this exception
		}
		return file;
	}

	@Override
	public ManualVerificationDTO updatePacketStatus(ManualVerificationDTO manualVerificationDTO) {
		// TODO Update the status either approved or rejected coming from front end corresponding to a reg id and mvUserId
		String registrationId = manualVerificationDTO.getRegId();
		String eventId = "";
		String eventName = "";
		String eventType = "";
		String description = "";
		boolean isTransactionSuccessful = false;
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService.getRegistrationStatus(registrationId);
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
		registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
		eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
				: EventName.EXCEPTION.toString();
		eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
				: EventType.SYSTEM.toString();
		registrationStatusDto.setUpdatedBy(USER);
		auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
				registrationId);
		
		return manualVerificationDTO;
	}

}