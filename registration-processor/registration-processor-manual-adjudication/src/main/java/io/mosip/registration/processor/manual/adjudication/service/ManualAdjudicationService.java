package io.mosip.registration.processor.manual.adjudication.service;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
/**
 * 
 * @author M1049617
 *
 */
@Service
public interface ManualAdjudicationService {
	
	public UserDto assignStatus(UserDto dto);
	public byte[] getApplicantPhoto(String regId);
	public byte[] getApplicantExceptionPhoto(String regId);
	public byte[] getApplicantProofOfAddress(String regId);
	public byte[] getApplicantProofOfIdentity(String regId);
	public byte[] getApplicantProofOfBirth(String regId);
	public byte[] getApplicantDetails(String regId);
	public byte[] getApplicantLeftEye(String regId);
	public byte[] getApplicantRightEye(String regId);
	public byte[] getApplicantBothThumbs(String regId);
	public byte[] getApplicantLeftPalm(String regId);
	public byte[] getApplicantRightPalm(String regId);
	public byte[] getPacketInfo(String regId);
}
