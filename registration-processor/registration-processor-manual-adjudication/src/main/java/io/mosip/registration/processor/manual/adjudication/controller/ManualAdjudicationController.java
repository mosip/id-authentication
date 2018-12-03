package io.mosip.registration.processor.manual.adjudication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v0.1/registration-processor/manual-adjudication")
@Api(tags = "Manual Adjudication")
public class ManualAdjudicationController {
	
	@Autowired
	private ManualAdjudicationService manualAdjudicationService;
	
	@PostMapping(value="/applicantPhoto", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantPhoto(String regId) {
		byte[] applicationPhoto = manualAdjudicationService.getApplicantPhoto(regId);
		return ResponseEntity.ok().body(applicationPhoto);
	}
	
	@PostMapping(value="/applicantExceptionPhoto", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantExceptionPhoto(String regId) {
		byte[] applicationExceptionPhoto = manualAdjudicationService.getApplicantExceptionPhoto(regId);
		return ResponseEntity.ok().body(applicationExceptionPhoto);
	}
	
	@PostMapping(value="/applicantProofOfAddress", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantProofOfAddress(String regId) {
		byte[] applicantProofOfAddress = manualAdjudicationService.getApplicantProofOfAddress(regId);
		return ResponseEntity.ok().body(applicantProofOfAddress);
	}
	
	@PostMapping(value="/applicantProofOfIdentity", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantProofOfIdentity(String regId) {
		byte[] applicantProofOfIdentity = manualAdjudicationService.getApplicantProofOfIdentity(regId);
		return ResponseEntity.ok().body(applicantProofOfIdentity);
	}
	
	@PostMapping(value="/applicantProofOfBirth", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantProofOfBirth(String regId) {
		byte[] applicantProofOfBirth = manualAdjudicationService.getApplicantProofOfBirth(regId);
		return ResponseEntity.ok().body(applicantProofOfBirth);
	}
	
	@PostMapping(value="/applicantDetails", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantDetails(String regId) {
		byte[] applicantDetails = manualAdjudicationService.getApplicantDetails(regId);
		return ResponseEntity.ok().body(applicantDetails);
	}
	
	@PostMapping(value="/applicantLeftEye", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantLeftEye(String regId) {
		byte[] applicantLeftEye = manualAdjudicationService.getApplicantLeftEye(regId);
		return ResponseEntity.ok().body(applicantLeftEye);
	}
	
	@PostMapping(value="/applicantRightEye", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantRightEye(String regId) {
		byte[] applicantRightEye = manualAdjudicationService.getApplicantRightEye(regId);
		return ResponseEntity.ok().body(applicantRightEye);
	}
	
	@PostMapping(value="/applicantBothThumbs", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantBothThumbs(String regId) {
		byte[] applicantBothThumbs = manualAdjudicationService.getApplicantBothThumbs(regId);
		return ResponseEntity.ok().body(applicantBothThumbs);
	}
	
	@PostMapping(value="/applicantLeftPalm", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantLeftPalm(String regId) {
		byte[] applicantLeftPalm = manualAdjudicationService.getApplicantLeftPalm(regId);
		return ResponseEntity.ok().body(applicantLeftPalm);
	}
	
	@PostMapping(value="/applicantRightPalm", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getApplicantRightPalm(String regId) {
		byte[] applicantRightPalm = manualAdjudicationService.getApplicantRightPalm(regId);
		return ResponseEntity.ok().body(applicantRightPalm);
	}
	
	@PostMapping(value="/packetInfo", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getPacketInfo(String regId) {
		byte[] packetInfo = manualAdjudicationService.getPacketInfo(regId);
		return ResponseEntity.ok().body(packetInfo);
	}
		

}
