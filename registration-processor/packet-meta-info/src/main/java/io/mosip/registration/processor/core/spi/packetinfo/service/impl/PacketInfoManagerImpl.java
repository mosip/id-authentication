package io.mosip.registration.processor.core.spi.packetinfo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.DocumentDetail;
import io.mosip.registration.processor.core.packet.dto.ExceptionFingerprint;
import io.mosip.registration.processor.core.packet.dto.ExceptionIris;
import io.mosip.registration.processor.core.packet.dto.Fingerprint;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.packet.dto.Iris;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.OsiData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantDocumentPKEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantFingerprintPKEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantIrisPKEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantPhotographPKEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.BiometricExceptionPKEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.RegOsiEntity;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantDocumentRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantFingerprintRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantIrisRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantPhotographRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.BiometricExceptionRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.RegOsiRepository;
import io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager;

/**
 * 
 * @author Horteppa M1048399
 *
 */
@Service
public class PacketInfoManagerImpl implements PacketInfoManager<PacketInfo, BiometericData, DocumentDetail> {

	@Autowired
	private ApplicantDocumentRepository applicantDocumentRepository;

	@Autowired
	private BiometricExceptionRepository biometricExceptionRepository;

	@Autowired
	ApplicantFingerprintRepository applicantFingerprintRepository;

	@Autowired
	ApplicantIrisRepository applicantIrisRepository;
	
	@Autowired
	ApplicantPhotographRepository applicantPhotographRepository;
	
	@Autowired
	RegOsiRepository regOsiRepository;

	private FileSystemAdapter fileSystemAdapter;
	private BiometericData biometricData;
	private HashMap<String, String> checksumMap;
	private Document documentDto;
	private HashSequence hashSequence;
	private MetaData metaData;
	private OsiData osiData;
	private Photograph photoGraphData;

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager#savePacketData(java.lang.Object)
	 */
	@Override
	public void savePacketData(PacketInfo packetInfo) throws Exception {

		biometricData = packetInfo.getBiometericData();
		documentDto = packetInfo.getDocument();
		metaData = packetInfo.getMetaData();
		osiData = packetInfo.getOsiData();
		photoGraphData = packetInfo.getPhotograph();
		hashSequence = packetInfo.getHashSequence();
		checksumMap = packetInfo.getCheckSumMap();

		List<DocumentDetail> documentDetails = documentDto.getDocumentDetails();
		for (DocumentDetail documentDetail : documentDetails) {
			saveDemographicData(documentDetail);
		}

		saveBioMetricData(biometricData);
		
		savePhotoGraphData(photoGraphData);
		
		saveOsiData(osiData);
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager#saveDemographicData(java.lang.Object)
	 */
	@Override
	public void saveDemographicData(DocumentDetail demograficData) throws IOException {
		System.out.println(demograficData);
		ApplicantDocumentEntity applicantDocumentEntity = convertAppDocDtoToAppDocEntity(demograficData);
		
	/*	if (photoGraphData.isHasExceptionPhoto()) {
			byte[] docBytes = getPhotoGraphDataInByteArray(metaData.getRegistrationId(),photoGraphData.getExceptionPhotoName());
			applicantDocumentEntity.setDocStore(docBytes);
			if (applicantDocumentRepository.save(applicantDocumentEntity) != null) {
				System.out.println("ExceptionPhotograph is SAVED");
			}
		}*/
		 
		// byte[] docBytes =getPhotoGraphDataInByteArray(metaData.getRegistrationId(),photoGraphData.getPhotographName());
		byte[] docBytes = { 10, 20, 40 };
		applicantDocumentEntity.setDocStore(docBytes);
		if (applicantDocumentRepository.save(applicantDocumentEntity) != null) {
			System.out.println("DATA SAVED");
		}
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager#saveBioMetricData(java.lang.Object)
	 */
	@Override
	public void saveBioMetricData(BiometericData bioMetricData) throws IOException {
		List<Fingerprint> fingerprints = bioMetricData.getFingerprintData().getFingerprints();
		List<ExceptionFingerprint> exceptionFingerprints = bioMetricData.getFingerprintData().getExceptionFingerprints();
		List<Iris> irisList = bioMetricData.getIrisData().getIris();
		List<ExceptionIris> exceptionIrisList = bioMetricData.getIrisData().getExceptionIris();

		for (Fingerprint fingerprint : fingerprints) {
			ApplicantFingerprintEntity fingerprintEntity = convertFingerprintToFingerprintEntity(fingerprint);
			if (applicantFingerprintRepository.save(fingerprintEntity) != null) {
				System.out.println("Fingerprint DATA SAVED");
			}
		}

		for (ExceptionFingerprint exceptionFingerprint : exceptionFingerprints) {
			BiometricExceptionEntity biometricExceptionEntity = convertBiometricExcToBiometricExcEntity(exceptionFingerprint);
			if (biometricExceptionRepository.save(biometricExceptionEntity) != null) {
				System.out.println("Biometric DATA SAVED");
			}
		}

		for (Iris iris : irisList) {
			ApplicantIrisEntity applicantIrisEntity = convertIrisToIrisEntity(iris);
			if (applicantIrisRepository.save(applicantIrisEntity) != null) {
				System.out.println("Iris DATA SAVED");
			}
		}

		for (ExceptionIris exceptionIris : exceptionIrisList) {
			BiometricExceptionEntity biometricIrisExceptionEntity = convertIrisToIrisExcEntity(exceptionIris);
			if (biometricExceptionRepository.save(biometricIrisExceptionEntity) != null) {
				System.out.println("Biometric Iris exception DATA SAVED");
			}
		}
	}
	
	/**
	 * Save osi data.
	 *
	 * @param osiData the osi data
	 * @throws Exception the exception
	 */
	private void saveOsiData(OsiData osiData)  throws Exception{
		RegOsiEntity regOsiEntity =  convertOsiDataToOsiEntity(osiData);
		if (regOsiRepository.save(regOsiEntity) != null) {
			System.out.println("OSI DATA SAVED");
		}
		
	}

	/**
	 * Save photo graph data.
	 *
	 * @param photoGraphData the photo graph data
	 * @throws Exception the exception
	 */
	private void savePhotoGraphData(Photograph photoGraphData) throws Exception {
		ApplicantPhotographEntity applicantPhotographEntity= convertPhotoGraphDataToPhotoGraphEntity(photoGraphData);
		if (applicantPhotographRepository.save(applicantPhotographEntity) != null) {
			System.out.println("Photograph DATA SAVED");
		}
	}

	/**
	 * Convert app doc dto to app doc entity.
	 *
	 * @param documentDto the document dto
	 * @return the applicant document entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ApplicantDocumentEntity convertAppDocDtoToAppDocEntity(DocumentDetail documentDto) throws IOException {

		ApplicantDocumentEntity applicantDocumentEntity = new ApplicantDocumentEntity();
		ApplicantDocumentPKEntity applicantDocumentPKEntity = new ApplicantDocumentPKEntity();
		applicantDocumentPKEntity.setDocCatCode(documentDto.getDocumentCategory());
		applicantDocumentPKEntity.setDocTypCode(documentDto.getDocumentType());
		applicantDocumentPKEntity.setRegId(metaData.getRegistrationId());

		applicantDocumentEntity.setId(applicantDocumentPKEntity);
		applicantDocumentEntity.setPreregId(metaData.getPreRegistrationId());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocName(documentDto.getDocumentName());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocFileFormat(".zip");

		return applicantDocumentEntity;
	}

	/**
	 * Convert iris to iris exc entity.
	 *
	 * @param exceptionIris the exception iris
	 * @return the biometric exception entity
	 */
	private BiometricExceptionEntity convertIrisToIrisExcEntity(ExceptionIris exceptionIris) {
		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(metaData.getRegistrationId());
		biometricExceptionPKEntity.setLangCode("ENG");
		biometricExceptionPKEntity.setMissingBio(exceptionIris.getMissingIris());

		bioMetricExceptionEntity.setId(biometricExceptionPKEntity);
		bioMetricExceptionEntity.setPreregId(metaData.getPreRegistrationId());
		bioMetricExceptionEntity.setBioTyp(exceptionIris.getBiometricType());
		bioMetricExceptionEntity.setExcpDescr(exceptionIris.getExceptionDescription());
		bioMetricExceptionEntity.setExcpTyp(exceptionIris.getExceptionType());
		bioMetricExceptionEntity.setIsDeleted(false);
		bioMetricExceptionEntity.setStatusCode("");

		bioMetricExceptionEntity.setUpdBy("Test-user");
		bioMetricExceptionEntity.setCrBy("Test-user");
		return bioMetricExceptionEntity;
	}

	/**
	 * Convert iris to iris entity.
	 *
	 * @param iris the iris
	 * @return the applicant iris entity
	 */
	private ApplicantIrisEntity convertIrisToIrisEntity(Iris iris) {

		ApplicantIrisEntity applicantIrisEntity = new ApplicantIrisEntity();
		ApplicantIrisPKEntity applicantIrisPKEntity = new ApplicantIrisPKEntity();

		applicantIrisPKEntity.setRegId(metaData.getRegistrationId());
		applicantIrisPKEntity.setTyp(iris.getIrisType());
		applicantIrisPKEntity.setLangCode("ENG");

		applicantIrisEntity.setId(applicantIrisPKEntity);
		applicantIrisEntity.setImageName(iris.getIrisImageName());
		applicantIrisEntity.setPreregId(metaData.getPreRegistrationId());
		applicantIrisEntity.setNoOfRetry(0);
		applicantIrisEntity.setQualityScore(new BigDecimal(iris.getQualityScore()));
		applicantIrisEntity.setStatusCode("Test-Status");
		applicantIrisEntity.setCrBy("Test-user");
		applicantIrisEntity.setUpdBy("Test-user");

		return applicantIrisEntity;
	}

	/**
	 * Convert fingerprint to fingerprint entity.
	 *
	 * @param fingerprint the fingerprint
	 * @return the applicant fingerprint entity
	 */
	private ApplicantFingerprintEntity convertFingerprintToFingerprintEntity(Fingerprint fingerprint) {

		ApplicantFingerprintEntity applicantFingerprintEntity = new ApplicantFingerprintEntity();
		ApplicantFingerprintPKEntity applicantFingerprintPKEntity = new ApplicantFingerprintPKEntity();
		
		applicantFingerprintPKEntity.setRegId(metaData.getRegistrationId());
		applicantFingerprintPKEntity.setTyp(fingerprint.getFingerType());
		applicantFingerprintPKEntity.setLangCode("ENG");

		applicantFingerprintEntity.setId(applicantFingerprintPKEntity);
		applicantFingerprintEntity.setImageName(fingerprint.getFingerprintImageName());
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setPreregId(metaData.getPreRegistrationId());
		applicantFingerprintEntity.setQualityScore(new BigDecimal(fingerprint.getQualityScore()));
		applicantFingerprintEntity.setStatusCode("Test-Status");
		applicantFingerprintEntity.setCrBy("Test-user");
		applicantFingerprintEntity.setUpdBy("Test-user");

		return applicantFingerprintEntity;

	}

	/**
	 * Convert biometric exc to biometric exc entity.
	 *
	 * @param exceptionFingerprint the exception fingerprint
	 * @return the biometric exception entity
	 */
	private BiometricExceptionEntity convertBiometricExcToBiometricExcEntity(ExceptionFingerprint exceptionFingerprint) {

		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(metaData.getRegistrationId());
		biometricExceptionPKEntity.setLangCode("ENG");
		biometricExceptionPKEntity.setMissingBio(exceptionFingerprint.getMissingFinger());

		bioMetricExceptionEntity.setId(biometricExceptionPKEntity);
		bioMetricExceptionEntity.setPreregId(metaData.getPreRegistrationId());
		bioMetricExceptionEntity.setBioTyp(exceptionFingerprint.getBiometricType());
		bioMetricExceptionEntity.setExcpDescr(exceptionFingerprint.getExceptionDescription());
		bioMetricExceptionEntity.setExcpTyp(exceptionFingerprint.getExceptionType());
		bioMetricExceptionEntity.setIsDeleted(false);

		bioMetricExceptionEntity.setStatusCode("Test-Status");
		bioMetricExceptionEntity.setUpdBy("Test-user");
		bioMetricExceptionEntity.setCrBy("Test-user");
		
		return bioMetricExceptionEntity;
	}

	/**
	 * Convert photo graph data to photo graph entity.
	 *
	 * @param photoGraphData the photo graph data
	 * @return the applicant photograph entity
	 */
	private ApplicantPhotographEntity convertPhotoGraphDataToPhotoGraphEntity(Photograph photoGraphData) {
		
		ApplicantPhotographEntity applicantPhotographEntity= new ApplicantPhotographEntity();
		
		ApplicantPhotographPKEntity applicantPhotographPKEntity= new ApplicantPhotographPKEntity();
		applicantPhotographPKEntity.setRegId(metaData.getRegistrationId());
		applicantPhotographPKEntity.setLangCode("ENG");
		
		applicantPhotographEntity.setId(applicantPhotographPKEntity);
		applicantPhotographEntity.setPreregId(metaData.getPreRegistrationId());
		applicantPhotographEntity.setExcpPhotoName(photoGraphData.getExceptionPhotoName());
		applicantPhotographEntity.setImageName(photoGraphData.getPhotographName());
		applicantPhotographEntity.setHasExcpPhotograph(true);
		applicantPhotographEntity.setNoOfRetry(1);
		applicantPhotographEntity.setQualityScore(new BigDecimal(photoGraphData.getQualityScore()));
		
		applicantPhotographEntity.setStatusCode("");
		applicantPhotographEntity.setCrBy("Test-user");
		applicantPhotographEntity.setUpdBy("Test-user");
		
		return applicantPhotographEntity;
	}
	
	/**
	 * Convert osi data to osi entity.
	 *
	 * @param osiData the osi data
	 * @return the reg osi entity
	 */
	private RegOsiEntity convertOsiDataToOsiEntity(OsiData osiData) {

		RegOsiEntity regOsiEntity = new RegOsiEntity();
		regOsiEntity.setIntroducerFingerpImageName(osiData.getIntroducerFingerprintImage());
		regOsiEntity.setIntroducerId(osiData.getIntroducerRID().toString());
		regOsiEntity.setIntroducerIrisImageName(osiData.getIntroducerIrisIrish());
		regOsiEntity.setIntroducerRegId(osiData.getIntroducerUIN());
		regOsiEntity.setIntroducerTyp(osiData.getIntroducerType());
		regOsiEntity.setIntroducerUin(osiData.getIntroducerUIN());
		regOsiEntity.setOfficerFingerpImageName(osiData.getOperatorFingerprintImage());
		regOsiEntity.setOfficerId(osiData.getOperatorID());
		regOsiEntity.setOfficerIrisImageName(osiData.getOperatorIrisImage());
		regOsiEntity.setRegId(metaData.getRegistrationId());
		regOsiEntity.setPreregId(metaData.getPreRegistrationId());
		regOsiEntity.setSupervisorId(osiData.getSupervisorID());
		regOsiEntity.setSupervisorFingerpImageName(osiData.getSupervisorFingerprintImage());
		regOsiEntity.setSupervisorIrisImageName(osiData.getSupervisorIrisName());
		regOsiEntity.setIsActive(true);
		
		regOsiEntity.setCrBy("Test-user");
		regOsiEntity.setUpdBy("Test-user");
		return regOsiEntity;
	}
	
	/**
	 * Gets the photo graph data in byte array.
	 *
	 * @param registrationId the registration id
	 * @param photographName the photograph name
	 * @return the photo graph data in byte array
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] getPhotoGraphDataInByteArray(String registrationId, String photographName) throws IOException {
		InputStream in = (InputStream) fileSystemAdapter.getFile(metaData.getRegistrationId(),photoGraphData.getPhotographName());
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		while ((len = in.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
		return os.toByteArray();
	}
}
