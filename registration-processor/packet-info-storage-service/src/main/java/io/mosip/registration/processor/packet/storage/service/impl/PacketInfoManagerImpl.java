package io.mosip.registration.processor.packet.storage.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.DocumentDetail;
import io.mosip.registration.processor.core.packet.dto.ExceptionFingerprint;
import io.mosip.registration.processor.core.packet.dto.ExceptionIris;
import io.mosip.registration.processor.core.packet.dto.Fingerprint;
import io.mosip.registration.processor.core.packet.dto.FingerprintData;
import io.mosip.registration.processor.core.packet.dto.Iris;
import io.mosip.registration.processor.core.packet.dto.IrisData;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.OsiData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
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
import io.mosip.registration.processor.status.code.AuditLogTempConstant;
import lombok.Cleanup;

/**
 * 
 * @author Horteppa M1048399
 * @author Girish Yarru
 *
 */
@Service
public class PacketInfoManagerImpl implements PacketInfoManager<PacketInfo, Demographic, MetaData> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoManagerImpl.class);

	public static final String FILE_SEPARATOR = "\\";
	
	public static final String LOG_FORMATTER = "{} - {}";
	
	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;
	@Autowired
	private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentRepository;

	@Autowired
	private BasePacketRepository<BiometricExceptionEntity, String> biometricExceptionRepository;

	@Autowired
	private BasePacketRepository<ApplicantFingerprintEntity, String> applicantFingerprintRepository;

	@Autowired
	private BasePacketRepository<ApplicantIrisEntity, String> applicantIrisRepository;

	@Autowired
	private BasePacketRepository<ApplicantPhotographEntity, String> applicantPhotographRepository;

	@Autowired
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	@Autowired
	private BasePacketRepository<ApplicantDemographicEntity, String> applicantDemographicRepository;

	@Autowired
	private BasePacketRepository<RegCenterMachineEntity, String> regCenterMachineRepository;

	@Autowired
	private AuditRequestBuilder auditRequestBuilder;

	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	private FileSystemAdapter<InputStream, Boolean> fileSystemAdapter = new FilesystemCephAdapterImpl();

	private MetaData metaData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager
	 * #savePacketData(java.lang.Object)
	 */
	@Override
	public void savePacketData(PacketInfo packetInfo) {

		boolean isTransactionSuccessful = false;

		BiometericData biometricData = packetInfo.getBiometericData();
		Document documentDto = packetInfo.getDocument();
		OsiData osiData = packetInfo.getOsiData();

		Photograph photoGraphData = packetInfo.getPhotograph();
		metaData = packetInfo.getMetaData();

		try {
			saveDocuments(documentDto);
			saveBioMetricData(biometricData);
			savePhotoGraph(photoGraphData);
			saveOsiData(osiData);
			saveRegCenterData(metaData);

			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {
			String description = isTransactionSuccessful ? "description--packet-meta-data saved Success"
					: "description--packet-metadata Failure";
			createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
					AuditLogTempConstant.APPLICATION_NAME.toString(), description,
					AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
					AuditLogTempConstant.EVENT_TYPE.toString());
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
	public void saveDemographicData(Demographic demographicInfo, MetaData metaData) {

		boolean isTransactionSuccessful = false;
		try {
			List<ApplicantDemographicEntity> applicantDemographicEntities = PacketInfoMapper
					.convertDemographicDtoToEntity(demographicInfo, metaData);
			for (ApplicantDemographicEntity applicantDemographicEntity : applicantDemographicEntities) {
				applicantDemographicRepository.save(applicantDemographicEntity);
				LOGGER.info(LOG_FORMATTER,applicantDemographicEntity.getId().getRegId(), " Demographic  DATA SAVED");
			}
			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException("Table Not Accessible", e);
		} finally {
			String description = isTransactionSuccessful ? "description--Demographic-data saved Success"
					: "description--Demographic Failed to save";
			createAuditRequestBuilder(AuditLogTempConstant.APPLICATION_ID.toString(),
					AuditLogTempConstant.APPLICATION_NAME.toString(), description,
					AuditLogTempConstant.EVENT_ID.toString(), AuditLogTempConstant.EVENT_TYPE.toString(),
					AuditLogTempConstant.EVENT_TYPE.toString());
		}

	}

	/**
	 * Save bio metric data.
	 *
	 * @param bioMetricData
	 *            the bio metric data
	 */
	private void saveBioMetricData(BiometericData bioMetricData) {
		saveFingerPrint(bioMetricData.getFingerprintData());
		saveIris(bioMetricData.getIrisData());
	}

	/**
	 * Save iris.
	 *
	 * @param irisData
	 *            the iris data
	 */
	private void saveIris(IrisData irisData) {
		List<Iris> irisList = irisData.getIris();
		List<ExceptionIris> exceptionIrisList = irisData.getExceptionIris();

		irisList.forEach(iris -> {
			ApplicantIrisEntity applicantIrisEntity = PacketInfoMapper.convertIrisDtoToEntity(iris, metaData);
			applicantIrisRepository.save(applicantIrisEntity);
			LOGGER.info(LOG_FORMATTER, applicantIrisEntity.getId().getRegId() , " Applicant Iris DATA SAVED");
		});

		exceptionIrisList.forEach(exceptionIris -> {
			BiometricExceptionEntity biometricIrisExceptionEntity = PacketInfoMapper
					.convertBiometricExcDtoToEntity(exceptionIris, metaData);
			biometricExceptionRepository.save(biometricIrisExceptionEntity);
			LOGGER.info(LOG_FORMATTER, biometricIrisExceptionEntity.getId().getRegId() , " Applicant Iris DATA SAVED");
		});
	}

	/**
	 * Save finger print.
	 *
	 * @param fingerprintData
	 *            the fingerprint data
	 */
	private void saveFingerPrint(FingerprintData fingerprintData) {
		List<Fingerprint> fingerprints = fingerprintData.getFingerprints();
		List<ExceptionFingerprint> exceptionFingerprints = fingerprintData.getExceptionFingerprints();

		fingerprints.forEach(fingerprint -> {
			ApplicantFingerprintEntity fingerprintEntity = PacketInfoMapper.convertFingerprintDtoToEntity(fingerprint,
					metaData);
			applicantFingerprintRepository.save(fingerprintEntity);
			LOGGER.info(LOG_FORMATTER, fingerprintEntity.getId().getRegId() , " Fingerprint DATA SAVED");

		});

		exceptionFingerprints.forEach(exceptionFingerprint -> {
			BiometricExceptionEntity biometricExceptionEntity = PacketInfoMapper
					.convertBiometricExceptioDtoToEntity(exceptionFingerprint, metaData);
			biometricExceptionRepository.save(biometricExceptionEntity);
			LOGGER.info(LOG_FORMATTER, biometricExceptionEntity.getId().getRegId() , "  Biometric Exception DATA SAVED");
		});
	}

	/**
	 * Save documents.
	 *
	 * @param documentDto
	 *            the document dto
	 */
	private void saveDocuments(Document documentDto) {

		List<DocumentDetail> documentDetails = documentDto.getDocumentDetails();
		for (DocumentDetail documentDetail : documentDetails) {
			saveDocument(documentDetail);
		}
	}

	/**
	 * Save document data.
	 *
	 * @param documentDetail
	 *            the document detail
	 */
	public void saveDocument(DocumentDetail documentDetail) {
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

		applicantDocumentEntity.setDocStore(getDocumentAsByteArray(metaData.getRegistrationId(), fileName));
		applicantDocumentRepository.save(applicantDocumentEntity);
		LOGGER.info(LOG_FORMATTER, applicantDocumentEntity.getId().getRegId() , "  Document Demographic DATA SAVED");
	}

	/**
	 * Save osi data.
	 *
	 * @param osiData
	 *            the osi data
	 */
	private void saveOsiData(OsiData osiData) {
		RegOsiEntity regOsiEntity = PacketInfoMapper.convertOsiDataToEntity(osiData, metaData);
		regOsiRepository.save(regOsiEntity);
		LOGGER.info(LOG_FORMATTER, regOsiEntity.getId() , "  Applicant OSI DATA SAVED");
	}

	/**
	 * Save photo graph.
	 *
	 * @param photoGraphData
	 *            the photo graph data
	 */
	private void savePhotoGraph(Photograph photoGraphData) {
		ApplicantPhotographEntity applicantPhotographEntity = PacketInfoMapper
				.convertPhotoGraphDtoToEntity(photoGraphData, metaData);
		applicantPhotographRepository.save(applicantPhotographEntity);
		LOGGER.info(LOG_FORMATTER,applicantPhotographEntity.getId().getRegId(), " Applicant Photograph DATA SAVED");
	}

	/**
	 * Save reg center data.
	 *
	 * @param metaData
	 *            the meta data
	 */
	private void saveRegCenterData(MetaData metaData) {
		RegCenterMachineEntity regCenterMachineEntity = PacketInfoMapper.convertRegCenterMachineToEntity(metaData);
		regCenterMachineRepository.save(regCenterMachineEntity);
		LOGGER.info(regCenterMachineEntity.getId() + " --> Registration Center Machine DATA SAVED");

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
			@Cleanup InputStream in = fileSystemAdapter.getFile(registrationId, documentName);
			byte[] buffer = new byte[1024];
			int len;
			@Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			return os.toByteArray();
		} catch (IOException e) {
			LOGGER.error(LOG_FORMATTER,"Error While reading  inputstream file", e);
			return new byte[1];
		}
		
		
	}

	private void createAuditRequestBuilder(String applicationId, String applicationName, String description,
			String eventId, String eventName, String eventType) {
		auditRequestBuilder.setActionTimeStamp(OffsetDateTime.now()).setApplicationId(applicationId)
				.setApplicationName(applicationName).setCreatedBy(AuditLogTempConstant.CREATED_BY.toString())
				.setDescription(description).setEventId(eventId).setEventName(eventName).setEventType(eventType)
				.setHostIp(AuditLogTempConstant.HOST_IP.toString())
				.setHostName(AuditLogTempConstant.HOST_NAME.toString()).setId(AuditLogTempConstant.ID.toString())
				.setIdType(AuditLogTempConstant.ID_TYPE.toString())
				.setModuleId(AuditLogTempConstant.MODULE_ID.toString())
				.setModuleName(AuditLogTempConstant.MODULE_NAME.toString())
				.setSessionUserId(AuditLogTempConstant.SESSION_USER_ID.toString())
				.setSessionUserName(AuditLogTempConstant.SESSION_USER_NAME.toString());

		AuditRequestDto auditRequestDto = auditRequestBuilder.build();
		auditHandler.writeAudit(auditRequestDto);
	}
}
