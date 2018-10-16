package io.mosip.registration.processor.core.spi.packetinfo.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.DemoInLocalLang;
import io.mosip.registration.processor.core.packet.dto.DemoInUserLang;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.DocumentDetail;
import io.mosip.registration.processor.core.packet.dto.ExceptionFingerprint;
import io.mosip.registration.processor.core.packet.dto.ExceptionIris;
import io.mosip.registration.processor.core.packet.dto.Fingerprint;
import io.mosip.registration.processor.core.packet.dto.Iris;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.OsiData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.core.spi.packetinfo.entity.ApplicantDemographicPKEntity;
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
import io.mosip.registration.processor.core.spi.packetinfo.exception.TablenotAccessibleException;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantDemographicRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantDocumentRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantFingerprintRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantIrisRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.ApplicantPhotographRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.BiometricExceptionRepository;
import io.mosip.registration.processor.core.spi.packetinfo.repository.RegOsiRepository;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;

/**
 * 
 * @author Horteppa M1048399
 *
 */
@Service
public class PacketInfoManagerImpl implements PacketInfoManager<PacketInfo,  DemographicInfo> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoManagerImpl.class);
	
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
	private RegOsiRepository regOsiRepository;
	
	@Autowired
	private ApplicantDemographicRepository applicantDemographicRepository;

	//@Autowired
	private FileSystemAdapter fileSystemAdapter;
	
	private BiometericData biometricData;
	private Document documentDto;
	private MetaData metaData;
	private OsiData osiData;
	private Photograph photoGraphData;
	

	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager#savePacketData(java.lang.Object)
	 */
	@Override
	public void savePacketData(PacketInfo packetInfo) {

		biometricData = packetInfo.getBiometericData();
		documentDto = packetInfo.getDocument();
		metaData = packetInfo.getMetaData();
		osiData = packetInfo.getOsiData();
		photoGraphData = packetInfo.getPhotograph();
		try {
			List<DocumentDetail> documentDetails = documentDto.getDocumentDetails();
			for (DocumentDetail documentDetail : documentDetails) {
				saveDocumentData(documentDetail);
			}
			saveBioMetricData(biometricData);
			savePhotoGraphData(photoGraphData);
			saveOsiData(osiData);
		} catch (DataAccessLayerException  e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager#saveDemographicData(java.lang.Object)
	 */
	@Override
	public void saveDemographicData(DemographicInfo demographicInfo) {
		
		try {
			List<ApplicantDemographicEntity> applicantDemographicEntities = convertDemographicInfoToAppDemographicInfoEntity(demographicInfo);
			for(ApplicantDemographicEntity applicantDemographicEntity:applicantDemographicEntities) {
				if (applicantDemographicRepository.save(applicantDemographicEntity) != null) {
					LOGGER.info(applicantDemographicEntity.getId().getRegId() +" --> Demographic  DATA SAVED");
				}
			}
		}catch(DataAccessLayerException  e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		}
		
	}
	
	

	/**
	 * Save bio metric data.
	 *
	 * @param bioMetricData the bio metric data
	 */
	public void saveBioMetricData(BiometericData bioMetricData){
		List<Fingerprint> fingerprints = bioMetricData.getFingerprintData().getFingerprints();
		List<ExceptionFingerprint> exceptionFingerprints = bioMetricData.getFingerprintData().getExceptionFingerprints();
		List<Iris> irisList = bioMetricData.getIrisData().getIris();
		List<ExceptionIris> exceptionIrisList = bioMetricData.getIrisData().getExceptionIris();

		for (Fingerprint fingerprint : fingerprints) {
			ApplicantFingerprintEntity fingerprintEntity = convertFingerprintToFingerprintEntity(fingerprint);
			if (applicantFingerprintRepository.save(fingerprintEntity) != null) {
				LOGGER.info(fingerprintEntity.getId().getRegId() +" --> Fingerprint DATA SAVED");
			}
		}

		for (ExceptionFingerprint exceptionFingerprint : exceptionFingerprints) {
			BiometricExceptionEntity biometricExceptionEntity = convertBiometricExcToBiometricExcEntity(exceptionFingerprint);
			if (biometricExceptionRepository.save(biometricExceptionEntity) != null) {
				LOGGER.info(biometricExceptionEntity.getId().getRegId() +" --> Biometric Exception DATA SAVED");
			}
		}

		for (Iris iris : irisList) {
			ApplicantIrisEntity applicantIrisEntity = convertIrisToIrisEntity(iris);
			if (applicantIrisRepository.save(applicantIrisEntity) != null) {
				LOGGER.info(applicantIrisEntity.getId().getRegId() +" --> Applicant Iris DATA SAVED");
			}
		}

		for (ExceptionIris exceptionIris : exceptionIrisList) {
			BiometricExceptionEntity biometricIrisExceptionEntity = convertIrisToIrisExcEntity(exceptionIris);
			if (biometricExceptionRepository.save(biometricIrisExceptionEntity) != null) {
				LOGGER.info(biometricIrisExceptionEntity.getId().getRegId() +" --> Applicant Iris DATA SAVED");
			}
		}
	}
	
	/**
	 * Save document data.
	 *
	 * @param documentDetail the document detail
	 */
	public void saveDocumentData(DocumentDetail documentDetail) {

		ApplicantDocumentEntity applicantDocumentEntity = convertAppDocDtoToAppDocEntity(documentDetail);
		
	/*	if (photoGraphData.isHasExceptionPhoto()) {
			byte[] docBytes = getPhotoGraphDataInByteArray(metaData.getRegistrationId(),photoGraphData.getExceptionPhotoName());
			applicantDocumentEntity.setDocStore(docBytes);
			if (applicantDocumentRepository.save(applicantDocumentEntity) != null) {
				LOGGER.info(applicantDocumentEntity.getId().getRegId() +" --> Document Exception Demographic DATA SAVED");
			}
		}*/
		//byte[] docBytes =getPhotoGraphDataInByteArray(metaData.getRegistrationId(),photoGraphData.getPhotographName());
		byte[] docBytes = { 10, 20, 40 };
		applicantDocumentEntity.setDocStore(docBytes);
		if (applicantDocumentRepository.save(applicantDocumentEntity) != null) {
			LOGGER.info(applicantDocumentEntity.getId().getRegId() +" --> Document Demographic DATA SAVED");
		}
	
	}
	/**
	 * Save osi data.
	 *
	 * @param osiData the osi data
	 * @throws Exception the exception
	 */
	private void saveOsiData(OsiData osiData) {
		RegOsiEntity regOsiEntity =  convertOsiDataToOsiEntity(osiData);
		if (regOsiRepository.save(regOsiEntity) != null) {
			LOGGER.info(regOsiEntity.getRegId() +" --> Applicant OSI DATA SAVED");
		}
		
	}

	/**
	 * Save photo graph data.
	 *
	 * @param photoGraphData the photo graph data
	 * @throws Exception the exception
	 */
	private void savePhotoGraphData(Photograph photoGraphData){
		ApplicantPhotographEntity applicantPhotographEntity= convertPhotoGraphDataToPhotoGraphEntity(photoGraphData);
		if (applicantPhotographRepository.save(applicantPhotographEntity) != null) {
			LOGGER.info(applicantPhotographEntity.getId().getRegId() +" --> Applicant Photograph DATA SAVED");
		}
	}

	/**
	 * Convert app doc dto to app doc entity.
	 *
	 * @param documentDto the document dto
	 * @return the applicant document entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ApplicantDocumentEntity convertAppDocDtoToAppDocEntity(DocumentDetail documentDto) {

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

		bioMetricExceptionEntity.setUpdBy(" ");
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
		applicantIrisEntity.setQualityScore(BigDecimal.valueOf(iris.getQualityScore()));
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
		applicantFingerprintEntity.setQualityScore(BigDecimal.valueOf(fingerprint.getQualityScore()));
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
		applicantPhotographEntity.setQualityScore(BigDecimal.valueOf(photoGraphData.getQualityScore()));
		
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
	private byte[] getPhotoGraphDataInByteArray(String registrationId, String photographName)  {
		InputStream in = (InputStream) fileSystemAdapter.getFile(metaData.getRegistrationId(),photoGraphData.getPhotographName());
		byte[] buffer = new byte[1024];
		int len;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
		} catch (IOException e) {
			LOGGER.error("Error While reading json inputstream file");
			e.printStackTrace();
		}
		return os.toByteArray();
	}
	
	/**
	 * Convert demographic info to app demographic info entity.
	 *
	 * @param demographicInfo the demographic info
	 * @return the list
	 */
	private List<ApplicantDemographicEntity> convertDemographicInfoToAppDemographicInfoEntity(
			DemographicInfo demographicInfo) {
		DemoInLocalLang demoInLocalLang = demographicInfo.getDemoInLocalLang();
		DemoInUserLang demoInUserLang = demographicInfo.getDemoInUserLang();
		List<ApplicantDemographicEntity> applicantDemographicEntities = new ArrayList<ApplicantDemographicEntity>();
		ApplicantDemographicEntity applicantDemographicEntity = new ApplicantDemographicEntity();
		
		ApplicantDemographicPKEntity applicantDemographicPKEntity  = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity.setLangCode(demoInLocalLang.getLanguageCode());
		applicantDemographicPKEntity.setRegId(metaData.getRegistrationId());
		
		applicantDemographicEntity.setId(applicantDemographicPKEntity);
		applicantDemographicEntity.setPreregId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInLocalLang.getAddress().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInLocalLang.getAddress().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInLocalLang.getAddress().getLine3());
		applicantDemographicEntity.setAge(new Date().getYear()-demoInLocalLang.getDateOfBirth().getYear());
		applicantDemographicEntity.setApplicantType("Local Language");
		applicantDemographicEntity.setDob(demoInLocalLang.getDateOfBirth());
		applicantDemographicEntity.setEmail(demoInLocalLang.getEmailId());
		applicantDemographicEntity.setFamilyname(demoInLocalLang.getFamilyname());
		applicantDemographicEntity.setFirstname(demoInLocalLang.getFirstname());
		applicantDemographicEntity.setForename(demoInLocalLang.getForename());
		applicantDemographicEntity.setFullname(demoInLocalLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInLocalLang.getGender());
		applicantDemographicEntity.setGivenname(demoInLocalLang.getGivenname());
		applicantDemographicEntity.setLastname(demoInLocalLang.getLastname());
		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setMiddlename(demoInLocalLang.getMiddlename());
		applicantDemographicEntity.setMobile(demoInLocalLang.getMobile());
		applicantDemographicEntity.setNationalid("National Id");
		applicantDemographicEntity.setParentFullname("Parent Full Name");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");
		applicantDemographicEntity.setParentRefId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setStatusCode("Status code");
		applicantDemographicEntity.setCrBy("Test User");
		applicantDemographicEntity.setSurname(demoInLocalLang.getSurname());
		
		applicantDemographicEntities.add(applicantDemographicEntity);
		
		
		
		applicantDemographicEntity = new ApplicantDemographicEntity();
		
		ApplicantDemographicPKEntity applicantDemographicPKEntity1  = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity1.setLangCode(demoInUserLang.getLanguageCode());
		applicantDemographicPKEntity1.setRegId(metaData.getRegistrationId());
		
		applicantDemographicEntity.setId(applicantDemographicPKEntity1);
		applicantDemographicEntity.setPreregId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInUserLang.getAddress().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInUserLang.getAddress().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInUserLang.getAddress().getLine3());
		applicantDemographicEntity.setAge(new Date().getYear()-demoInLocalLang.getDateOfBirth().getYear());
		applicantDemographicEntity.setApplicantType("USER Language");
		applicantDemographicEntity.setDob(demoInLocalLang.getDateOfBirth());
		applicantDemographicEntity.setEmail(demoInUserLang.getEmailId());
		applicantDemographicEntity.setFamilyname(demoInUserLang.getFamilyname());
		applicantDemographicEntity.setFirstname(demoInUserLang.getFirstname());
		applicantDemographicEntity.setForename(demoInUserLang.getForename());
		applicantDemographicEntity.setFullname(demoInUserLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInUserLang.getGender());
		applicantDemographicEntity.setGivenname(demoInUserLang.getGivenname());
		applicantDemographicEntity.setLastname(demoInUserLang.getLastname());
		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setMiddlename(demoInUserLang.getMiddlename());
		applicantDemographicEntity.setMobile(demoInUserLang.getMobile());
		applicantDemographicEntity.setNationalid("National Id");
		applicantDemographicEntity.setParentFullname("Parent Full Name");
		applicantDemographicEntity.setParentRefId("ParentRefId");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");
		applicantDemographicEntity.setStatusCode("Status code");
		applicantDemographicEntity.setCrBy("Test User");
		applicantDemographicEntity.setSurname(demoInUserLang.getSurname());
		
		applicantDemographicEntities.add(applicantDemographicEntity);
		
		return applicantDemographicEntities;
	}
}
