package io.mosip.registration.mapper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.BaseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
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
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

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
			String language = "eng";

			ApplicantDocumentDTO documentDTO = source.getDemographicDTO().getApplicantDocumentDTO();

			// Set Photograph
			identity.setApplicantPhotograph(buildPhotograph("label", language, documentDTO.getNumRetry(),
					documentDTO.getPhotographName(), documentDTO.getQualityScore()));

			// Set Exception Photograph
			identity.setExceptionPhotograph(
					buildPhotograph("label", language, 0, documentDTO.getExceptionPhotoName(), 0));

			// Set Documents
			identity.setDocuments(buildDocuments(source.getDemographicDTO()));

			// Add Biometric Details
			BiometricInfoDTO biometricInfoDTO = source.getBiometricDTO().getApplicantBiometricDTO();

			// Get the captured fingerprints
			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = biometricInfoDTO.getFingerprintDetailsDTO();

			// Put the finger-prints to map
			Map<String, FingerprintDetailsDTO> fingerprintMap = new HashMap<>();
			if (fingerprintDetailsDTOs != null) {
				for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
					for (FingerprintDetailsDTO segmentedFingerprint : fingerprintDetailsDTO
							.getSegmentedFingerprints()) {
						fingerprintMap.put(segmentedFingerprint.getFingerType().toUpperCase(), segmentedFingerprint);
					}
				}
			}

			// Set Left Index Finger
			String biometricType = RegistrationConstants.FINGERPRINT.toLowerCase();
			applicant.setLeftIndex(getBiometric(fingerprintMap.get("LEFTINDEX"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Middle Finger
			applicant.setLeftMiddle(getBiometric(fingerprintMap.get("LEFTMIDDLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Ring Finger
			applicant.setLeftRing(getBiometric(fingerprintMap.get("LEFTRING"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Little Finger
			applicant.setLeftLittle(getBiometric(fingerprintMap.get("LEFTLITTLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Thumb Finger
			applicant.setLeftThumb(getBiometric(fingerprintMap.get("LEFTTHUMB"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Right Index Finger
			applicant.setRightIndex(getBiometric(fingerprintMap.get("RIGHTINDEX"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Middle Finger
			applicant.setRightMiddle(getBiometric(fingerprintMap.get("RIGHTMIDDLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Ring Finger
			applicant.setRightRing(getBiometric(fingerprintMap.get("RIGHTRING"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Little Finger
			applicant.setRightLittle(getBiometric(fingerprintMap.get("RIGHTLITTLE"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

			// Set Left Thumb Finger
			applicant.setRightThumb(getBiometric(fingerprintMap.get("RIGHTTHUMB"), language, biometricType,
					RegistrationConstants.INDIVIDUAL));

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
			biometricType = RegistrationConstants.IRIS.toLowerCase();
			applicant.setLeftEye(
					getBiometric(irisMap.get("LEFTEYE"), language, biometricType, RegistrationConstants.INDIVIDUAL));

			// Set Right Eye
			applicant.setRightEye(
					getBiometric(irisMap.get("RIGHTEYE"), language, biometricType, RegistrationConstants.INDIVIDUAL));

			// Add captured biometric exceptions
			identity.getExceptionBiometrics()
					.addAll(getExceptionBiometrics(biometricInfoDTO.getBiometricExceptionDTO(), language));

			// Set Parent Finger-print Image
			getIntroducerBiometrics(source, introducer, language);

			// Set MetaData
			identity.setMetaData(getMetaData(source));

			// Set OSIData
			identity.setOsiData(getOSIData(source));

			// Set Registered Device
			identity.setCapturedRegisteredDevices(getRegisteredDevices());

			// Set Registered Device
			identity.setCapturedNonRegisteredDevices(getNonRegisteredDevices());

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

	private void getIntroducerBiometrics(RegistrationDTO source, Introducer introducer, String language) {
		BiometricInfoDTO biometricInfoDTO;
		String biometricType;
		biometricInfoDTO = source.getBiometricDTO().getIntroducerBiometricDTO();
		if (biometricInfoDTO != null) {
			List<FingerprintDetailsDTO> fingerprints = biometricInfoDTO.getFingerprintDetailsDTO();
			if (fingerprints != null && !fingerprints.isEmpty()) {
				biometricType = RegistrationConstants.FINGERPRINT.toLowerCase();
				introducer.setIntroducerFingerprint(
						getBiometric(fingerprints.get(0), language, biometricType, RegistrationConstants.INTRODUCER));
			}

			List<IrisDetailsDTO> parentIris = biometricInfoDTO.getIrisDetailsDTO();
			if (parentIris != null && !parentIris.isEmpty()) {
				biometricType = RegistrationConstants.IRIS.toLowerCase();
				introducer.setIntroducerIris(
						getBiometric(parentIris.get(0), language, biometricType, RegistrationConstants.INTRODUCER));
			}
		}
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

	private List<Document> buildDocuments(DemographicDTO demographicDTO) {
		List<Document> documents = new ArrayList<>();

		DocumentDetailsDTO documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity()
				.getProofOfIdentity();

		if (documentDetailsDTO != null) {
			documents.add(getDocument(removeFileExt(documentDetailsDTO.getValue()), "PoI",
					documentDetailsDTO.getType(), documentDetailsDTO.getOwner()));
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfAddress();

		if (documentDetailsDTO != null) {
			documents.add(getDocument(removeFileExt(documentDetailsDTO.getValue()), "PoA",
					documentDetailsDTO.getType(), documentDetailsDTO.getOwner()));
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfRelationship();

		if (documentDetailsDTO != null) {
			documents.add(getDocument(removeFileExt(documentDetailsDTO.getValue()), "PoR",
					documentDetailsDTO.getType(), documentDetailsDTO.getOwner()));
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfDateOfBirth();

		if (documentDetailsDTO != null) {
			documents.add(getDocument(removeFileExt(documentDetailsDTO.getValue()), "PoB",
					documentDetailsDTO.getType(), documentDetailsDTO.getOwner()));
		}

		if (demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt() != null) {
			// Add the Acknowledgement Receipt
			documents.add(
					getDocument(removeFileExt(demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceiptName()),
							RegistrationConstants.ACK_RECEIPT, RegistrationConstants.ACK_RECEIPT, "Self"));
		}

		return documents;
	}

	private Document getDocument(String documentName, String documentType, String documentCategory,
			String documentOwner) {
		Document document = new Document();
		document.setDocumentName(documentName);
		document.setDocumentType(documentType);
		document.setDocumentCategory(documentCategory);
		document.setDocumentOwner(documentOwner);

		return document;
	}

	private BiometricDetails getBiometric(BaseDTO biometricDTO, String language, String biometricType,
			String personType) {
		BiometricDetails biometricDetails = null;
		if (biometricDTO != null) {
			if (biometricDTO instanceof FingerprintDetailsDTO) {
				FingerprintDetailsDTO fingerprint = (FingerprintDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric("label", language, biometricType,
						getBIRUUID(personType, fingerprint.getFingerType()), fingerprint.getQualityScore(),
						fingerprint.getNumRetry(), fingerprint.isForceCaptured());
			} else if (biometricDTO instanceof IrisDetailsDTO) {
				IrisDetailsDTO iris = (IrisDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric("label", language, biometricType,
						getBIRUUID(personType, iris.getIrisType()), iris.getQualityScore(), iris.getNumOfIrisRetry(),
						iris.isForceCaptured());
			}
		}
		return biometricDetails;
	}

	private BiometricDetails buildBiometric(String label, String language, String type, String birIndex,
			double qualityScore, int numRetry, boolean forceCaptured) {
		BiometricDetails biometricDetails = new BiometricDetails();
		biometricDetails.setLabel(label);
		biometricDetails.setLanguage(language);
		biometricDetails.setType(type);
		biometricDetails.setImageName(birIndex);
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
		metaData.add(buildFieldValue("machineId", "12334"));
		// Add Dongle ID
		metaData.add(buildFieldValue("dongleId", "67890"));
		// Add MAC ID
		metaData.add(buildFieldValue("macId", RegistrationSystemPropertiesChecker.getMachineId()));
		// Add Center ID
		metaData.add(buildFieldValue("centerId", "54321"));
		// Add UIN
		metaData.add(buildFieldValue("uin", metaDataDTO.getUin()));
		// Add Previous Registration ID
		metaData.add(buildFieldValue("previousRID", metaDataDTO.getPreviousRID()));
		// Add Introducer Type
		metaData.add(buildFieldValue("introducerType", registrationDTO.getOsiDataDTO().getIntroducerType()));

		// Validate whether Introducer has provided UIN or RID
		String introducerRID = null;
		String introducerUIN = null;
		BigInteger introducerRIDorUIN = registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity()
				.getParentOrGuardianRIDOrUIN();
		if (introducerRIDorUIN != null) {
			if (introducerRIDorUIN.toString().length() == Integer
					.parseInt(AppConfig.getApplicationProperty("uin_length"))) {
				introducerUIN = introducerRIDorUIN.toString();
			} else {
				introducerRID = introducerRIDorUIN.toString();
			}
		}

		// Add Introducer RID
		metaData.add(buildFieldValue("introducerRID", introducerRID));
		// Add Hash of Introducer RID
		metaData.add(buildFieldValue("introducerRIDHash", getHash(introducerRID)));
		// Add Introducer UIN
		metaData.add(buildFieldValue("introducerUIN", introducerUIN));
		// Add Hash of Introducer UIN
		metaData.add(buildFieldValue("introducerUINHash", getHash(introducerUIN)));
		// Add Officer Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(),
				RegistrationConstants.OFFICER.toLowerCase(), RegistrationConstants.BIOMETRIC_TYPE));
		// Add Supervisor Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(),
				RegistrationConstants.SUPERVISOR.toLowerCase(), RegistrationConstants.BIOMETRIC_TYPE));
		// Add Introducer Biometrics
		metaData.addAll(getOfficerBiometric(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO(),
				RegistrationConstants.INTRODUCER.toLowerCase(), RegistrationConstants.BIOMETRIC_TYPE));
		// Add Registration Creation Date
		metaData.add(buildFieldValue("creationDate", DateUtils.formatToISOString(LocalDateTime.now())));

		return metaData;
	}

	@SuppressWarnings("unchecked")
	private List<FieldValue> getOSIData(RegistrationDTO registrationDTO) {
		List<FieldValue> osiData = new LinkedList<>();
		// Add Operator ID
		osiData.add(buildFieldValue("officerId", registrationDTO.getOsiDataDTO().getOperatorID()));
		// Add Officer CBEFF File
		if (((Map<String, String>) SessionContext.getSessionContext().getMapObject()
				.get(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME)).keySet().stream()
						.anyMatch(key -> key.startsWith(RegistrationConstants.OFFICER.toLowerCase()))) {
			osiData.add(buildFieldValue("officerBiometricFileName",
					removeFileExt(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME)));
		} else {
			osiData.add(buildFieldValue("officerBiometricFileName", null));
		}

		// Add Supervisor ID
		osiData.add(buildFieldValue("supervisorId", registrationDTO.getOsiDataDTO().getSupervisorID()));
		// Add Officer CBEFF File
		if (((Map<String, String>) SessionContext.getSessionContext().getMapObject()
				.get(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME)).keySet().stream()
						.anyMatch(key -> key.startsWith(RegistrationConstants.SUPERVISOR.toLowerCase()))) {
			osiData.add(buildFieldValue("supervisorBiometricFileName",
					removeFileExt(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME)));
		} else {
			osiData.add(buildFieldValue("supervisorBiometricFileName", null));
		}

		// Add Supervisor Password
		osiData.add(buildFieldValue("supervisorPassword",
				String.valueOf(registrationDTO.getOsiDataDTO().isSuperviorAuthenticatedByPassword())));
		// Add Officer Password
		osiData.add(buildFieldValue("officerPassword",
				String.valueOf(registrationDTO.getOsiDataDTO().isOperatorAuthenticatedByPassword())));

		// Add Supervisor PIN
		osiData.add(buildFieldValue("supervisorPIN", null));
		// Add Officer PIN
		osiData.add(buildFieldValue("officerPIN", null));

		// Add Supervisor Face Image
		osiData.add(buildFieldValue("supervisorFaceImage", null));
		// Add Officer Face Image
		osiData.add(buildFieldValue("officerFaceImage", null));

		// Add Supervisor OTP Authentication Image
		osiData.add(buildFieldValue("supervisorOTPAuthentication",
				String.valueOf(registrationDTO.getOsiDataDTO().isSuperviorAuthenticatedByPIN())));
		// Add Officer Face Image
		osiData.add(buildFieldValue("officerOTPAuthentication",
				String.valueOf(registrationDTO.getOsiDataDTO().isOperatorAuthenticatedByPIN())));

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
				fingerprintImageName = fingerprint.getFingerType();
				if (field.equals(RegistrationConstants.BIOMETRIC_IMAGE)) {
					fingerprintImageName = getBIRUUID(officerType, fingerprintImageName);
				}
			}

			IrisDetailsDTO iris = (IrisDetailsDTO) getObjectAt(officerBiometric.getIrisDetailsDTO(), 0);
			if (iris != null) {
				irisImageName = iris.getIrisType();
				if (field.equals(RegistrationConstants.BIOMETRIC_IMAGE)) {
					irisImageName = getBIRUUID(officerType, irisImageName);
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

	private List<FieldValue> getRegisteredDevices() {
		List<FieldValue> registratedDevices = new LinkedList<>();

		// Add fingerprint device
		registratedDevices.add(buildFieldValue("fingerprint", "123455YRHTIFHKJI8U90U2334"));
		// Add Iris Device
		registratedDevices.add(buildFieldValue("iris", "123455YRHTIFHKJI8U9r2rrr3r3"));
		// Add GPS Device
		registratedDevices.add(buildFieldValue("gps", "123455YRHTIFHKJI8U90r3ttt4ttf"));
		// Add Photo Camera Device
		registratedDevices.add(buildFieldValue("photo", "12345ttt4tggrgrrwrgwgwgrggrggw"));

		return registratedDevices;
	}

	private List<FieldValue> getNonRegisteredDevices() {
		List<FieldValue> registratedDevices = new LinkedList<>();

		// Add fingerprint device
		registratedDevices.add(buildFieldValue("fingerprint", "trergrfrfrfrfrw21313113"));
		// Add Iris Device
		registratedDevices.add(buildFieldValue("iris", "313rq342s4s3gg5g54bth5j4j64j64"));
		// Add GPS Device
		registratedDevices.add(buildFieldValue("gps", "8yjh98q78njh7t62hbkjkjqhkjqbqhhkh"));
		// Add Photo Camera Device
		registratedDevices.add(buildFieldValue("photo", "11331oiu31y8bahagyaftfafahfakhfagk"));

		return registratedDevices;
	}

	@SuppressWarnings("unchecked")
	private String getBIRUUID(String personType, String biometricType) {
		return ((Map<String, String>) SessionContext.getSessionContext().getMapObject()
				.get(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME))
						.get(personType.concat(biometricType).toLowerCase());
	}

}
