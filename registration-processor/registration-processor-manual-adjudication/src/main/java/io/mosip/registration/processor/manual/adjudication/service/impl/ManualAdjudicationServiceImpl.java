package io.mosip.registration.processor.manual.adjudication.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

/**
 * 
 * @author M1049617
 *
 */
@Service
public class ManualAdjudicationServiceImpl implements ManualAdjudicationService {

	@Autowired
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	@Autowired
	ManualAdjudicationDao manualAdjudicationDao;

	@Override
	public ManualVerificationDTO assignStatus(UserDto dto) {
		ManualVerificationDTO manualVerificationDTO = new ManualVerificationDTO();
		ManualVerificationEntity entity= manualAdjudicationDao.getAssignedApplicantDetails(dto.getUserId(), ManualVerificationStatus.ASSIGNED.name());
		if(entity!=null) {
			manualVerificationDTO.setRegId(entity.getId().getRegId());
			manualVerificationDTO.setMatchedRefId(entity.getId().getMatchedRefId());
			manualVerificationDTO.setMatchedRefType(entity.getId().getMatchedRefType());
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
					manualVerificationDTO.setRegId(updatedManualVerificationEntity.getId().getRegId());
					manualVerificationDTO.setMatchedRefId(updatedManualVerificationEntity.getId().getMatchedRefId());
					manualVerificationDTO.setMatchedRefType(updatedManualVerificationEntity.getId().getMatchedRefType());
					manualVerificationDTO.setMvUsrId(updatedManualVerificationEntity.getMvUsrId());
					manualVerificationDTO.setStatusCode(updatedManualVerificationEntity.getStatusCode());
				}
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
		ManualVerificationDTO dto=new ManualVerificationDTO();
		ManualVerificationEntity manualVerificationEntity = manualAdjudicationDao.getByRegId(manualVerificationDTO.getRegId(),manualVerificationDTO.getMvUsrId());
		manualVerificationEntity.setStatusCode(manualVerificationDTO.getStatusCode());
		ManualVerificationEntity updatedManualVerificationEntity=manualAdjudicationDao.update(manualVerificationEntity);
		if(updatedManualVerificationEntity!=null) {
			dto.setRegId(updatedManualVerificationEntity.getId().getRegId());
			dto.setMatchedRefId(updatedManualVerificationEntity.getId().getMatchedRefId());
			dto.setMatchedRefType(updatedManualVerificationEntity.getId().getMatchedRefType());
			dto.setMvUsrId(updatedManualVerificationEntity.getMvUsrId());
			dto.setStatusCode(updatedManualVerificationEntity.getStatusCode());
		}
		return dto;
	}
	
}