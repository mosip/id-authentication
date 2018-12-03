package io.mosip.registration.processor.manual.adjudication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.manual.adjudication.dao.ManualAdjudicationDao;
import io.mosip.registration.processor.manual.adjudication.dto.ApplicantDetailsDto;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;

/**
 * 
 * @author M1049617
 *
 */
@Service
public class ManualAdjudicationServiceImpl implements ManualAdjudicationService {

	ManualVerificationEntity manualAdjudicationEntity = new ManualVerificationEntity();
	@Autowired
	ManualAdjudicationDao manualAdjudicationDao;

	@Override
	public ApplicantDetailsDto assignStatus(UserDto dto) {
		ApplicantDetailsDto adto = new ApplicantDetailsDto();

		if (dto.getStatus() != null && dto.getStatus().equalsIgnoreCase("PENDING")) {

			String regid = manualAdjudicationDao.getFirstApplicantDetails().get(0).getId().getRegId();
			manualAdjudicationEntity.setStatusCode(dto.getStatus());
			manualAdjudicationEntity.setMvUserId(dto.getUserId());
			manualAdjudicationEntity.getId().setRegId(regid);
			manualAdjudicationDao.update(manualAdjudicationEntity);
		}

		return adto;
	}

	@Override
	public byte[] getApplicantPhoto(String regId) {
		// TODO Auto-generated method stub
		return null;
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
