package io.mosip.registration.processor.manual.adjudication.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.manual.adjudication.dao.ManualAdjudicationDao;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.entity.ManualAdjudicationEntity;
import io.mosip.registration.processor.manual.adjudication.exception.FileNotPresentException;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;

/**
 * 
 * @author M1049617
 *
 */
@Service
public class ManualAdjudicationServiceImpl implements ManualAdjudicationService {

	ManualAdjudicationEntity manualAdjudicationEntity = new ManualAdjudicationEntity();
	@Autowired
	ManualAdjudicationDao manualAdjudicationDao;

	@Autowired
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	@Override
	public UserDto assignStatus(UserDto dto) {
		if (dto.getStatus() != null && dto.getStatus().equalsIgnoreCase("PENDING"))
			manualAdjudicationEntity.setStatus_code("ASSIGNED");
		manualAdjudicationDao.update(manualAdjudicationEntity);
		return dto;
	}

	@Override
	public byte[] getApplicantPhoto(String regId) {
		InputStream applicantPhoto = filesystemCephAdapterImpl.getFile(regId, PacketFiles.APPLICANTPHOTO.name());
		try {
			return IOUtils.toByteArray(applicantPhoto);
		} catch (IOException e) {
			throw new FileNotPresentException();
		}
	}

	@Override
	public byte[] getApplicantExceptionPhoto(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantProofOfAddress(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantProofOfIdentity(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantProofOfBirth(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantDetails(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantLeftEye(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantRightEye(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantBothThumbs(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantLeftPalm(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getApplicantRightPalm(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getPacketInfo(String regId) {
		// TODO Auto-generated method stub
		return null;
	}

}
