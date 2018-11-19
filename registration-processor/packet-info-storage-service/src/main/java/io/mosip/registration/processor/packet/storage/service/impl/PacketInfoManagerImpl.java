package io.mosip.registration.processor.packet.storage.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.asn1.cms.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.kernel.dataaccess.hibernate.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.packet.dto.Applicant;
import io.mosip.registration.processor.core.packet.dto.Biometric;
import io.mosip.registration.processor.core.packet.dto.BiometricData;
import io.mosip.registration.processor.core.packet.dto.BiometricException;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.packet.storage.mapper.PacketInfoMapper;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import lombok.Cleanup;

/**
 * The Class PacketInfoManagerImpl.
 *
 * @author Horteppa M1048399
 * @author Girish Yarru
 *
 */

@RefreshScope
@Service
public class PacketInfoManagerImpl implements PacketInfoManager<Identity, ApplicantInfoDto> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoManagerImpl.class);

	public static final String FILE_SEPARATOR = "\\";

	public static final String LOG_FORMATTER = "{} - {}";

	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	private static final String TABLE_NOT_ACCESSIBLE = "TABLE IS NOT ACCESSIBLE.";

	/** The applicant document repository. */
	@Autowired
	private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentRepository;

	/** The biometric exception repository. */
	@Autowired
	private BasePacketRepository<BiometricExceptionEntity, String> biometricExceptionRepository;

	/** The applicant fingerprint repository. */
	@Autowired
	private BasePacketRepository<ApplicantFingerprintEntity, String> applicantFingerprintRepository;

	/** The applicant iris repository. */
	@Autowired
	private BasePacketRepository<ApplicantIrisEntity, String> applicantIrisRepository;

	/** The applicant photograph repository. */
	@Autowired
	private BasePacketRepository<ApplicantPhotographEntity, String> applicantPhotographRepository;

	/** The reg osi repository. */
	@Autowired
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	/** The applicant demographic repository. */
	@Autowired
	private BasePacketRepository<ApplicantDemographicEntity, String> applicantDemographicRepository;

	/** The reg center machine repository. */
	@Autowired
	private BasePacketRepository<RegCenterMachineEntity, String> regCenterMachineRepository;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The description. */
	String description = "";

	/** The core audit request builder. */
	@Autowired
	CoreAuditRequestBuilder coreAuditRequestBuilder;

	@Autowired
	private PacketInfoDao packetInfoDao;

	@Autowired
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	/** The meta data. */
	private List<FieldValue> metaData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager
	 * #savePacketData(java.lang.Object)
	 */
	@Override
	public void savePacketData(Identity identity) {

		boolean isTransactionSuccessful = false;

		Biometric biometric = identity.getBiometric();
		List<Document> documentDtos = identity.getDocuments();
		List<FieldValue> osiData = identity.getOsiData();
		List<BiometricException> exceptionBiometrics=identity.getExceptionBiometrics();
		Photograph applicantPhotographData = identity.getApplicantPhotograph();
		Photograph exceptionPhotographData = identity.getExceptionPhotograph();
		metaData = identity.getMetaData();

		try {
			saveDocuments(documentDtos);
			saveApplicantBioMetricDatas(biometric.getApplicant());
			saveExceptionBiometricDatas(exceptionBiometrics);
			savePhotoGraph(applicantPhotographData,exceptionPhotographData);
			
			saveOsiData(osiData,biometric.getIntroducer());
			saveRegCenterData(metaData);
			isTransactionSuccessful = true;

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(TABLE_NOT_ACCESSIBLE, e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Packet meta data saved successfully"
					: "Packet meta data unsuccessful";

			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}

	}

	private void saveExceptionBiometricDatas(List<BiometricException> exceptionBiometrics) {
		for(BiometricException exp: exceptionBiometrics) {
			BiometricExceptionEntity biometricExceptionEntity=
					PacketInfoMapper.convertBiometricExceptioDtoToEntity(exp, metaData);
			biometricExceptionRepository.save(biometricExceptionEntity);
			LOGGER.info(LOG_FORMATTER, biometricExceptionEntity.getId().getRegId(), " Biometric Exception DATA SAVED");
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager
	 * #saveDemographicData(java.lang.Object)
	 */
	@Override
	public void saveDemographicData(Demographic demographicInfo, List<FieldValue> metaData) {

		boolean isTransactionSuccessful = false;
		try {
			List<ApplicantDemographicEntity> applicantDemographicEntities = PacketInfoMapper
					.convertDemographicDtoToEntity(demographicInfo, metaData);
			for (ApplicantDemographicEntity applicantDemographicEntity : applicantDemographicEntities) {
				applicantDemographicRepository.save(applicantDemographicEntity);
				LOGGER.info(LOG_FORMATTER, applicantDemographicEntity.getId().getRegId(), " Demographic  DATA SAVED");
			}
			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Demographic data saved successfully"
					: "Demographic data Failed to save";

			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getPacketsforQCUser(java.lang.String)
	 */
	@Override
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcUserId) {
		boolean isTransactionSuccessful = false;
		List<ApplicantInfoDto> applicantInfoDtoList = null;
		try {
			applicantInfoDtoList = packetInfoDao.getPacketsforQCUser(qcUserId);
			isTransactionSuccessful = true;
			return applicantInfoDtoList;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {
			/*
			 * String description = isTransactionSuccessful ?
			 * "description--QcUser packet Info fetched Success" :
			 * "description--QcUser packet Info fetched Failed";
			 * createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
			 * AuditLogTempConstant.APPLICATION_NAME.toString(), description,
			 * AuditLogTempConstant.EVENT_ID.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString(),
			 * AuditLogTempConstant.EVENT_TYPE.toString());
			 */
		}
	}

	/**
	 * Save bio metric data.
	 *
	 * @param bioMetricData
	 *            the bio metric data
	 */
	private void saveApplicantBioMetricDatas(Applicant applicant) {
		saveIris(applicant.getLeftEye());
		saveIris(applicant.getRightEye());
		saveFingerPrint(applicant.getLeftSlap());
		saveFingerPrint(applicant.getRightSlap());
		saveFingerPrint(applicant.getThumbs());
		
	}

	/**
	 * Save iris.
	 *
	 * @param irisData
	 *            the iris data
	 */
	private void saveIris(BiometricData irisData) {
		

		
			ApplicantIrisEntity applicantIrisEntity = PacketInfoMapper.convertIrisDtoToEntity(irisData, metaData);
			applicantIrisRepository.save(applicantIrisEntity);
			LOGGER.info(LOG_FORMATTER, applicantIrisEntity.getId().getRegId(), " Applicant Iris DATA SAVED");
		

		
	}

	/**
	 * Save finger print.
	 *
	 * @param fingerprintData
	 *            the fingerprint data
	 */
	private void saveFingerPrint(BiometricData fingerprintData) {
		

		
			ApplicantFingerprintEntity fingerprintEntity = PacketInfoMapper.convertFingerprintDtoToEntity(fingerprintData,
					metaData);
			applicantFingerprintRepository.save(fingerprintEntity);
			LOGGER.info(LOG_FORMATTER, fingerprintEntity.getId().getRegId(), " Fingerprint DATA SAVED");

		

		
	}

	/**
	 * Save documents.
	 *
	 * @param documentDtos
	 *            the document dto
	 */
	private void saveDocuments(List<Document> documentDtos) {

		for(Document document:documentDtos) {
			saveDocument(document);
		}
		
		
	}

	/**
	 * Save document data.
	 *
	 * @param documentDetail
	 *            the document detail
	 */
	public void saveDocument(Document documentDetail) {
		ApplicantDocumentEntity applicantDocumentEntity = PacketInfoMapper.convertAppDocDtoToEntity(documentDetail,
				metaData);

		String fileName = "";
		if (PacketFiles.APPLICANTPHOTO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.APPLICANTPHOTO.name();
		} else if (PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name();
		} else if (PacketFiles.DEMOGRAPHICINFO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name();
		} else if (PacketFiles.PROOFOFADDRESS.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFADDRESS.name();
		} else if (PacketFiles.EXCEPTIONPHOTO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.EXCEPTIONPHOTO.name();
		} else if (PacketFiles.PROOFOFIDENTITY.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFIDENTITY.name();
		}
		Optional<FieldValue> regId=metaData.stream().filter(m -> m.getLabel().equals("registrationId")).findFirst();
		String registrationId="";
		if(regId.isPresent())registrationId=regId.get().getValue();
		applicantDocumentEntity.setDocStore(getDocumentAsByteArray(registrationId, fileName));
		applicantDocumentRepository.save(applicantDocumentEntity);
		LOGGER.info(LOG_FORMATTER, applicantDocumentEntity.getId().getRegId(), "  Document Demographic DATA SAVED");
	}

	/**
	 * Save osi data.
	 *
	 * @param osiData
	 *            the osi data
	 * @param introducer 
	 */
	private void saveOsiData(List<FieldValue> osiData, Introducer introducer) {
		RegOsiEntity regOsiEntity = PacketInfoMapper.convertOsiDataToEntity(osiData,introducer,metaData);
		regOsiRepository.save(regOsiEntity);
		LOGGER.info(LOG_FORMATTER, regOsiEntity.getId(), "  Applicant OSI DATA SAVED");
	}

	/**
	 * Save photo graph.
	 *
	 * @param photoGraphData
	 *            the photo graph data
	 * @param exceptionPhotographData 
	 */
	private void savePhotoGraph(Photograph photoGraphData, Photograph exceptionPhotographData) {
		ApplicantPhotographEntity applicantPhotographEntity = PacketInfoMapper
				.convertPhotoGraphDtoToEntity(photoGraphData,exceptionPhotographData, metaData);
		applicantPhotographRepository.save(applicantPhotographEntity);
		LOGGER.info(LOG_FORMATTER, applicantPhotographEntity.getId().getRegId(), " Applicant Photograph DATA SAVED");
	}

	/**
	 * Save reg center data.
	 *
	 * @param metaData
	 *            the meta data
	 */
	private void saveRegCenterData(List<FieldValue> metaData) {
		RegCenterMachineEntity regCenterMachineEntity = PacketInfoMapper.convertRegCenterMachineToEntity(metaData);
		regCenterMachineRepository.save(regCenterMachineEntity);
		LOGGER.info(LOG_FORMATTER,regCenterMachineEntity.getId() + " --> Registration Center Machine DATA SAVED");

	}

	/**
	 * Gets the document as byte array.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param documentName
	 *            the document name
	 * @return the document as byte array
	 */
	private byte[] getDocumentAsByteArray(String registrationId, String documentName) {
		try {
			@Cleanup
			InputStream in = filesystemCephAdapterImpl.getFile(registrationId, documentName);
			byte[] buffer = new byte[1024];
			int len;
			@Cleanup
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			return os.toByteArray();
		} catch (IOException e) {
			LOGGER.error(LOG_FORMATTER, "Error While reading  inputstream file", e);
			return new byte[1];
		}

	}
}
