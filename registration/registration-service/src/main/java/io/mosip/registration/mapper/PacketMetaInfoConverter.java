package io.mosip.registration.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.BaseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.json.metadata.Applicant;
import io.mosip.registration.dto.json.metadata.Biometric;
import io.mosip.registration.dto.json.metadata.BiometricDetails;
import io.mosip.registration.dto.json.metadata.BiometricException;
import io.mosip.registration.dto.json.metadata.Document;
import io.mosip.registration.dto.json.metadata.FieldValue;
import io.mosip.registration.dto.json.metadata.Identity;
import io.mosip.registration.dto.json.metadata.Introducer;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.dto.json.metadata.Photograph;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.checksum.CheckSumUtil;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * The custom Orika Mapper converter class for converting the
 * {@link RegistrationDTO} object to {@link PacketMetaInfo}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class PacketMetaInfoConverter extends CustomConverter<RegistrationDTO, PacketMetaInfo> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object,
	 * ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public PacketMetaInfo convert(RegistrationDTO source, Type<? extends PacketMetaInfo> destinationType) {
		// Instantiate PacketMetaInfo object
		PacketMetaInfo packetMetaInfo = new PacketMetaInfo();
		try {
			// Initialize PacketMetaInfo object
			Identity identity = new Identity();
			packetMetaInfo.setIdentity(identity);
			List<BiometricException> exceptionBiometrics = new LinkedList<>();
			identity.setExceptionBiometrics(exceptionBiometrics);
			Biometric biometric = new Biometric();
			identity.setBiometric(biometric);
			Applicant applicant = new Applicant();
			biometric.setApplicant(applicant);
			Introducer introducer = new Introducer();
			biometric.setIntroducer(introducer);

			// Load from ApplicationContext
			String language = "en";

			ApplicantDocumentDTO documentDTO = source.getDemographicDTO().getApplicantDocumentDTO();

			// Set Photograph
			identity.setApplicantPhotograph(buildPhotograph("label", language, documentDTO.getNumRetry(),
					documentDTO.getPhotographName(), documentDTO.getQualityScore()));

			// Set Exception Photograph
			identity.setExceptionPhotograph(
					buildPhotograph("label", language, 0, documentDTO.getExceptionPhotoName(), 0));

			// Set Documents
			identity.setDocuments(buildDocuments(documentDTO));
			
			// Add Biometric Details
			BiometricInfoDTO biometricInfoDTO = source.getBiometricDTO().getApplicantBiometricDTO();

			// Get the captured fingerprints
			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = biometricInfoDTO.getFingerprintDetailsDTO();

			// Put the finger-prints to map
			Map<String, FingerprintDetailsDTO> fingerprintMap = new HashMap<>();
			if (fingerprintDetailsDTOs != null) {
				for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
					fingerprintMap.put(fingerprintDetailsDTO.getFingerType().toUpperCase(), fingerprintDetailsDTO);
				}
			}

			// Set Left Slap
			String biometricType = "fingerprint";
			applicant.setLeftSlap(getBiometric(fingerprintMap.get("LEFTSLAP"), language, biometricType));

			// Set Right Slap
			applicant.setRightSlap(getBiometric(fingerprintMap.get("RIGHTSLAP"), language, biometricType));

			// Set Thumbs
			applicant.setThumbs(getBiometric(fingerprintMap.get("THUMBS"), language, biometricType));

			// Get captured Iris Details
			List<IrisDetailsDTO> irisDetailsDTOs = biometricInfoDTO.getIrisDetailsDTO();

			// Put Iris to map
			Map<String, IrisDetailsDTO> irisMap = new HashMap<>();
			if (irisDetailsDTOs != null) {
				for (IrisDetailsDTO irisDetailsDTO : irisDetailsDTOs) {
					irisMap.put(irisDetailsDTO.getIrisType().toUpperCase(), irisDetailsDTO);
				}
			}

			// Set Left Eye
			biometricType = "iris";
			applicant.setLeftEye(getBiometric(irisMap.get("LEFTEYE"), language, biometricType));

			// Set Right Eye
			applicant.setRightEye(getBiometric(irisMap.get("RIGHTKEY"), language, biometricType));

			// Add captured Finger-print biometric exceptions
			identity.getExceptionBiometrics()
					.addAll(getExceptionBiometrics(biometricInfoDTO.getFingerPrintBiometricExceptionDTO(), language));

			// Add captured iris biometric exceptions
			identity.getExceptionBiometrics()
					.addAll(getExceptionBiometrics(biometricInfoDTO.getIrisBiometricExceptionDTO(), language));

			// Set Parent Finger-print Image
			biometricInfoDTO = source.getBiometricDTO().getIntroducerBiometricDTO();
			if (biometricInfoDTO != null) {
				List<FingerprintDetailsDTO> fingerprints = biometricInfoDTO.getFingerprintDetailsDTO();
				if (!fingerprints.isEmpty()) {
					biometricType = "fingerprint";
					introducer.setIntroducerFingerprint(getBiometric(fingerprints.get(0), language, biometricType));
				}

				List<IrisDetailsDTO> parentIris = biometricInfoDTO.getIrisDetailsDTO();
				if (!parentIris.isEmpty() ) {
					biometricType = "iris";
					introducer.setIntroducerIris(getBiometric(parentIris.get(0), language, biometricType));
				}
			}

			// Set MetaData
			identity.setMetaData(getMetaData(source));

			// Set OSIData
			identity.setOsiData(getOSIData(source));
			
			// Set Checksum
			List<FieldValue> checkSums = new LinkedList<>();
			Map<String, String> checkSumMap = CheckSumUtil.getCheckSumMap();
			checkSumMap.forEach((key, value) -> checkSums.add(buildFieldValue(key, value)));
			identity.setCheckSum(checkSums);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_META_CONVERTOR,
					runtimeException.toString());
		}
		return packetMetaInfo;
	}

	private Photograph buildPhotograph(String label, String language, int numRetry, String photographName,
			double qualityScore) {
		Photograph photograph = null;
		if (photographName != null) {
			photograph = new Photograph();
			photograph.setLabel(label);
			photograph.setLanguage(language);
			photograph.setNumRetry(numRetry);
			photograph.setPhotographName(removeFileExt(photographName));
			photograph.setQualityScore(qualityScore);
		}

		return photograph;
	}
	
	private List<Document> buildDocuments(ApplicantDocumentDTO documentDTO) {
		List<Document> documents = new ArrayList<>();
		if (documentDTO.getDocumentDetailsDTO() != null) {
			for (DocumentDetailsDTO documentDetailsDTO : documentDTO.getDocumentDetailsDTO()) {
				Document document = new Document();
				document.setDocumentCategory(documentDetailsDTO.getDocumentCategory());
				document.setDocumentName(removeFileExt(documentDetailsDTO.getDocumentName()));
				document.setDocumentOwner(documentDetailsDTO.getDocumentOwner());
				document.setDocumentType(documentDetailsDTO.getDocumentType());
				
				documents.add(document);
			}
		}
		
		// Create Document object for Applicant Acknowledgement Receipt
		Document document = new Document();
		document.setDocumentCategory(RegistrationConstants.ACK_RECEIPT);
		document.setDocumentName(removeFileExt(documentDTO.getAcknowledgeReceiptName()));
		document.setDocumentOwner("Self");
		document.setDocumentType(RegistrationConstants.ACK_RECEIPT);
		
		// Add the Acknowledgement Receipt
		documents.add(document);
		
		return documents;
	}

	private BiometricDetails getBiometric(BaseDTO biometricDTO, String language, String biometricType) {
		BiometricDetails biometricDetails = null;
		if (biometricDTO != null) {
			if (biometricDTO instanceof FingerprintDetailsDTO) {
				FingerprintDetailsDTO fingerprint = (FingerprintDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric("label", language, biometricType, fingerprint.getFingerprintImageName(),
						fingerprint.getQualityScore(), fingerprint.getNumRetry(), fingerprint.isForceCaptured());
			} else if (biometricDTO instanceof IrisDetailsDTO) {
				IrisDetailsDTO iris = (IrisDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric("label", language, biometricType, iris.getIrisImageName(),
						iris.getQualityScore(), iris.getNumOfIrisRetry(), iris.isForceCaptured());
			}
		}
		return biometricDetails;
	}

	private BiometricDetails buildBiometric(String label, String language, String type, String imageName, double qualityScore,
			int numRetry, boolean forceCaptured) {
		BiometricDetails biometricDetails = new BiometricDetails();
		biometricDetails.setLabel(label);
		biometricDetails.setLanguage(language);
		biometricDetails.setType(type);
		biometricDetails.setImageName(removeFileExt(imageName));
		biometricDetails.setQualityScore(qualityScore);
		biometricDetails.setNumRetry(numRetry);
		biometricDetails.setForceCaptured(forceCaptured);

		return biometricDetails;
	}

	private List<BiometricException> getExceptionBiometrics(List<BiometricExceptionDTO> biometricExceptionDTOs,
			String language) {
		List<BiometricException> exceptionBiometrics = new LinkedList<>();

		// Add finger-print biometric exceptions
		if (biometricExceptionDTOs != null) {
			for (BiometricExceptionDTO biometricExceptionDTO : biometricExceptionDTOs) {
				exceptionBiometrics.add(buildExceptionBiometric(language, biometricExceptionDTO.getBiometricType(),
						biometricExceptionDTO.getMissingBiometric(), biometricExceptionDTO.getExceptionType(),
						biometricExceptionDTO.getExceptionDescription()));
			}
		}

		return exceptionBiometrics;
	}

	private BiometricException buildExceptionBiometric(String language, String type, String missingBiometric,
			String exceptionType, String exceptionDescription) {
		BiometricException exceptionBiometric = new BiometricException();
		exceptionBiometric.setLanguage(language);
		exceptionBiometric.setType(type);
		exceptionBiometric.setMissingBiometric(missingBiometric);
		exceptionBiometric.setExceptionType(exceptionType);
		exceptionBiometric.setExceptionDescription(exceptionDescription);

		return exceptionBiometric;
	}

	private List<FieldValue> getMetaData(RegistrationDTO registrationDTO) {
		List<FieldValue> metaData = new LinkedList<>();

		// Get RegistrationMetaDataDTO
		RegistrationMetaDataDTO metaDataDTO = registrationDTO.getRegistrationMetaDataDTO();

		// Add Geo-location Latitude
		metaData.add(buildFieldValue("geoLocLatitude", String.valueOf(metaDataDTO.getGeoLatitudeLoc())));
		// Add Geo-location Longitude
		metaData.add(buildFieldValue("geoLoclongitude", String.valueOf(metaDataDTO.getGeoLongitudeLoc())));
		// Add Registration Type
		metaData.add(buildFieldValue("registrationType", metaDataDTO.getRegistrationCategory()));
		// Add Applicant Type
		metaData.add(buildFieldValue("applicantType", metaDataDTO.getApplicationType()));
		// Add Pre-Registration ID
		metaData.add(buildFieldValue("preRegistrationId", registrationDTO.getPreRegistrationId()));
		// Add Registration ID
		metaData.add(buildFieldValue("registrationId", registrationDTO.getRegistrationId()));
		// Add Hash of Registration ID
		metaData.add(buildFieldValue("registrationIdHash",
				HMACUtils.digestAsPlainText(HMACUtils.generateHash(registrationDTO.getRegistrationId().getBytes()))));
		// Add Machine ID
		metaData.add(buildFieldValue("machineId", metaDataDTO.getMachineId()));
		// Add Center ID
		metaData.add(buildFieldValue("centerId", metaDataDTO.getCenterId()));
		// Add UIN
		metaData.add(buildFieldValue("uin", metaDataDTO.getUin()));
		// Add Previous Registration ID
		metaData.add(buildFieldValue("previousRID", metaDataDTO.getPreviousRID()));
		// Add Introducer Type
		metaData.add(buildFieldValue("introducerType", registrationDTO.getOsiDataDTO().getIntroducerType()));
		// Add Introducer RID
		metaData.add(buildFieldValue("introducerRID", registrationDTO.getDemographicDTO().getIntroducerRID()));
		// Add Hash of Introducer RID
		metaData.add(buildFieldValue("introducerRIDHash", getHash(registrationDTO.getDemographicDTO().getIntroducerRID())));
		// Add Introducer UIN
		metaData.add(buildFieldValue("introducerUIN", registrationDTO.getDemographicDTO().getIntroducerUIN()));
		// Add Hash of Introducer UIN
		metaData.add(buildFieldValue("introducerUINHash", getHash(registrationDTO.getDemographicDTO().getIntroducerUIN())));
		// Add Officer Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(),
				"officer", RegistrationConstants.BIOMETRIC_TYPE));
		// Add Supervisor Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(), "supervisor",
				RegistrationConstants.BIOMETRIC_TYPE));
		// Add Introducer Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO(), "introducer",
				RegistrationConstants.BIOMETRIC_TYPE));
		// Add Registration Creation Date
		metaData.add(buildFieldValue("creationDate", String.valueOf(LocalDateTime.now())));
		// Add DOB Verified
		metaData.add(buildFieldValue("isVerified", String.valueOf(false)));

		return metaData;
	}

	private List<FieldValue> getOSIData(RegistrationDTO registrationDTO) {
		List<FieldValue> osiData = new LinkedList<>();
		// Add Operator ID
		osiData.add(buildFieldValue("officerId", registrationDTO.getOsiDataDTO().getOperatorID()));
		// Add Name of Operator Fingerprint and Iris Images
		osiData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(), "officer",
				RegistrationConstants.BIOMETRIC_IMAGE));

		// Add Supervisor ID
		osiData.add(buildFieldValue("supervisorId", registrationDTO.getOsiDataDTO().getSupervisorID()));
		// Add Name of Supervisor Fingerprint and Iris Images
		osiData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getSupervisorBiometricDTO(), "supervisor",
				RegistrationConstants.BIOMETRIC_IMAGE));
		
		// Add Supervisor Password
		osiData.add(buildFieldValue("supervisorPassword", null));
		// Add Officer Password
		osiData.add(buildFieldValue("officerPassword", null));
		
		// Add Supervisor PIN
		osiData.add(buildFieldValue("supervisorPIN", null));
		// Add Officer PIN
		osiData.add(buildFieldValue("officerPIN", null));
		
		// Add Supervisor Authentication Image
		osiData.add(buildFieldValue("supervisorAuthenticationImage", null));
		// Add Officer Authentication Image
		osiData.add(buildFieldValue("officerAuthenticationImage", null));

		return osiData;
	}

	private List<FieldValue> getOfficerBiometric(BiometricInfoDTO officerBiometric, String officerType, String field) {
		List<FieldValue> officer = new LinkedList<>();
		String fingerprintImageName = null;
		String irisImageName = null;

		if (officerBiometric != null) {
			FingerprintDetailsDTO fingerprint = (FingerprintDetailsDTO) getObjectAt(
					officerBiometric.getFingerprintDetailsDTO(), 0);
			if (fingerprint != null) {
				if (field.equals(RegistrationConstants.BIOMETRIC_IMAGE)) {
					fingerprintImageName = removeFileExt(fingerprint.getFingerprintImageName());
				} else {
					fingerprintImageName = fingerprint.getFingerType();
				}
			}

			IrisDetailsDTO iris = (IrisDetailsDTO) getObjectAt(officerBiometric.getIrisDetailsDTO(), 0);
			if (iris != null) {
				if (field.equals(RegistrationConstants.BIOMETRIC_IMAGE)) {
					irisImageName = removeFileExt(iris.getIrisImageName());
				} else {
					irisImageName = iris.getIrisType();
				}
			}
		}

		officer.add(buildFieldValue(officerType + "Fingerprint" + field, fingerprintImageName));
		officer.add(buildFieldValue(officerType + "Iris" + field, irisImageName));

		return officer;
	}

	private FieldValue buildFieldValue(String label, String value) {
		FieldValue fieldValue = new FieldValue();
		fieldValue.setLabel(label);
		fieldValue.setValue(value);
		return fieldValue;
	}

	private boolean checkNull(List<?> list) {
		boolean isNull = false;
		if (list == null || list.isEmpty()) {
			isNull = true;
		}
		return isNull;
	}

	private Object getObjectAt(List<?> list, int index) {
		Object object = null;
		if (!checkNull(list) && (index < list.size())) {
			object = list.get(index);
		}
		return object;
	}

	private String getHash(String value) {
		String hashedString = null;
		if (value != null) {
			hashedString = HMACUtils.digestAsPlainText(HMACUtils.generateHash(value.getBytes()));
		}

		return hashedString;
	}
	
	private String removeFileExt(String fileName) {
		if (fileName.contains(".")) {
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		return fileName;
	}
}
