package io.mosip.registration.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.BaseDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.json.metadata.Applicant;
import io.mosip.registration.dto.json.metadata.Biometric;
import io.mosip.registration.dto.json.metadata.BiometricDetails;
import io.mosip.registration.dto.json.metadata.BiometricException;
import io.mosip.registration.dto.json.metadata.Document;
import io.mosip.registration.dto.json.metadata.ExceptionPhotograph;
import io.mosip.registration.dto.json.metadata.FieldValue;
import io.mosip.registration.dto.json.metadata.Identity;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.dto.json.metadata.Photograph;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.checksum.CheckSumUtil;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * This class extends the {@link CustomConverter} class provided by Orika Mapper
 * library
 * 
 * <p>
 * This class maps the {@link RegistrationDTO} object to {@link PacketMetaInfo}
 * </p>
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

			FaceDetailsDTO applicantFaceDetailsDTO = source.getBiometricDTO().getApplicantBiometricDTO().getFace();

			// Set Photograph
			identity.setApplicantPhotograph(buildPhotograph(applicantFaceDetailsDTO.getNumOfRetries(),
					getBIRUUID(RegistrationConstants.INDIVIDUAL, RegistrationConstants.VALIDATION_TYPE_FACE)));

			// Set Exception Photograph
			setExceptionPhotograph(source, identity);

			// Set Documents
			identity.setDocuments(buildDocuments(source.getDemographicDTO()));

			// Add Biometric Details
			BiometricInfoDTO biometricInfoDTO = source.getBiometricDTO().getApplicantBiometricDTO();

			// Get the captured fingerprints
			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = biometricInfoDTO.getFingerprintDetailsDTO();

			// Put the finger-prints to map
			Map<String, FingerprintDetailsDTO> fingerprintMap = new WeakHashMap<>();
			if (fingerprintDetailsDTOs != null) {
				for (FingerprintDetailsDTO fingerprintDetailsDTO : fingerprintDetailsDTOs) {
					for (FingerprintDetailsDTO segmentedFingerprint : fingerprintDetailsDTO
							.getSegmentedFingerprints()) {
						fingerprintMap.put(segmentedFingerprint.getFingerType().toUpperCase(), segmentedFingerprint);
					}
				}
			}

			// Set Left Index Finger
			applicant.setLeftIndex(getBiometric(fingerprintMap.get("LEFTINDEX"), RegistrationConstants.INDIVIDUAL));

			// Set Left Middle Finger
			applicant.setLeftMiddle(getBiometric(fingerprintMap.get("LEFTMIDDLE"), RegistrationConstants.INDIVIDUAL));

			// Set Left Ring Finger
			applicant.setLeftRing(getBiometric(fingerprintMap.get("LEFTRING"), RegistrationConstants.INDIVIDUAL));

			// Set Left Little Finger
			applicant.setLeftLittle(getBiometric(fingerprintMap.get("LEFTLITTLE"), RegistrationConstants.INDIVIDUAL));

			// Set Left Thumb Finger
			applicant.setLeftThumb(getBiometric(fingerprintMap.get("LEFTTHUMB"), RegistrationConstants.INDIVIDUAL));

			// Set Right Index Finger
			applicant.setRightIndex(getBiometric(fingerprintMap.get("RIGHTINDEX"), RegistrationConstants.INDIVIDUAL));

			// Set Left Middle Finger
			applicant.setRightMiddle(getBiometric(fingerprintMap.get("RIGHTMIDDLE"), RegistrationConstants.INDIVIDUAL));

			// Set Left Ring Finger
			applicant.setRightRing(getBiometric(fingerprintMap.get("RIGHTRING"), RegistrationConstants.INDIVIDUAL));

			// Set Left Little Finger
			applicant.setRightLittle(getBiometric(fingerprintMap.get("RIGHTLITTLE"), RegistrationConstants.INDIVIDUAL));

			// Set Left Thumb Finger
			applicant.setRightThumb(getBiometric(fingerprintMap.get("RIGHTTHUMB"), RegistrationConstants.INDIVIDUAL));

			// Get captured Iris Details
			List<IrisDetailsDTO> irisDetailsDTOs = biometricInfoDTO.getIrisDetailsDTO();

			// Put Iris to map
			Map<String, IrisDetailsDTO> irisMap = new WeakHashMap<>();
			if (irisDetailsDTOs != null) {
				for (IrisDetailsDTO irisDetailsDTO : irisDetailsDTOs) {
					irisMap.put(irisDetailsDTO.getIrisType().toUpperCase(), irisDetailsDTO);
				}
			}

			// Set Left Eye
			applicant.setLeftEye(getBiometric(irisMap.get("LEFTEYE"), RegistrationConstants.INDIVIDUAL));

			// Set Right Eye
			applicant.setRightEye(getBiometric(irisMap.get("RIGHTEYE"), RegistrationConstants.INDIVIDUAL));

			// Add captured biometric exceptions
			identity.getExceptionBiometrics()
					.addAll(getExceptionBiometrics(biometricInfoDTO.getBiometricExceptionDTO()));

			// Set MetaData
			identity.setMetaData(getMetaData(source));

			// Set OSIData
			identity.setOsiData(getOSIData(source));

			// Set Checksum
			List<FieldValue> checkSums = new LinkedList<>();
			
			Map<String, String> checkSumMap = CheckSumUtil.getCheckSumMap();
			checkSumMap.forEach((key, value) -> checkSums.add(buildFieldValue(key, value)));
			identity.setCheckSum(checkSums);

			if (source.isNameNotUpdated()) {
				identity.setPrintingName(source.getRegistrationMetaDataDTO().getFullName());
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_META_CONVERTOR,
					runtimeException.getMessage(), runtimeException);
		}
		return packetMetaInfo;
	}

	private void setExceptionPhotograph(RegistrationDTO source, Identity identity) {
		boolean isIntroducerFace = (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)
				|| source.isUpdateUINNonBiometric();
		identity.setExceptionPhotograph(buildExceptionPhotograph(
				isIntroducerFace || (source.isUpdateUINNonBiometric()
						&& !source.isUpdateUINChild())
										? source.getBiometricDTO().getIntroducerBiometricDTO().getExceptionFace()
												.getNumOfRetries()
										: source.getBiometricDTO().getApplicantBiometricDTO().getExceptionFace()
												.getNumOfRetries(),
				isIntroducerFace || (source.isUpdateUINNonBiometric()
						&& !source.isUpdateUINChild())
										? source.getBiometricDTO().getIntroducerBiometricDTO().getExceptionFace()
												.getFace()
										: source.getBiometricDTO().getApplicantBiometricDTO().getExceptionFace()
												.getFace(),
				source));
	}

	private Photograph buildPhotograph(int numRetry, String photographName) {
		Photograph photograph = null;
		if (photographName != null) {
			photograph = new Photograph();
			photograph.setNumRetry(numRetry);
			photograph.setBirIndex(removeFileExt(photographName));
		}

		return photograph;
	}

	private ExceptionPhotograph buildExceptionPhotograph(int numRetry, byte[] face, RegistrationDTO source) {
		ExceptionPhotograph exceptionPhotograph = null;
		if (face != null) {
			exceptionPhotograph = new ExceptionPhotograph();
			exceptionPhotograph.setNumRetry(numRetry);
			exceptionPhotograph.setIndividualType((boolean) SessionContext.map().get(RegistrationConstants.IS_Child)
					|| (source.isUpdateUINChild() && source.isUpdateUINNonBiometric())
									? RegistrationConstants.PARENT
									: RegistrationConstants.INDIVIDUAL);
			exceptionPhotograph.setPhotoName(((boolean) SessionContext.map().get(RegistrationConstants.IS_Child)
					|| (source.isUpdateUINChild() && source.isUpdateUINNonBiometric())
									? RegistrationConstants.PARENT.toLowerCase()
									: RegistrationConstants.INDIVIDUAL.toLowerCase())
											.concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO));
		}

		return exceptionPhotograph;
	}

	private List<Document> buildDocuments(DemographicDTO demographicDTO) {
		List<Document> documents = new ArrayList<>();

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

	private BiometricDetails getBiometric(BaseDTO biometricDTO, String personType) {
		BiometricDetails biometricDetails = null;
		if (biometricDTO != null) {
			if (biometricDTO instanceof FingerprintDetailsDTO) {
				FingerprintDetailsDTO fingerprint = (FingerprintDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric(getBIRUUID(personType, fingerprint.getFingerType()),
						fingerprint.getNumRetry(), fingerprint.isForceCaptured());
			} else if (biometricDTO instanceof IrisDetailsDTO) {
				IrisDetailsDTO iris = (IrisDetailsDTO) biometricDTO;
				biometricDetails = buildBiometric(getBIRUUID(personType, iris.getIrisType()), iris.getNumOfIrisRetry(),
						iris.isForceCaptured());
			}
		}
		return biometricDetails;
	}

	private BiometricDetails buildBiometric(String birIndex, int numRetry, boolean forceCaptured) {
		BiometricDetails biometricDetails = new BiometricDetails();
		biometricDetails.setBirIndex(birIndex);
		biometricDetails.setNumRetry(numRetry);
		biometricDetails.setForceCaptured(forceCaptured);

		return biometricDetails;
	}

	private List<BiometricException> getExceptionBiometrics(List<BiometricExceptionDTO> biometricExceptionDTOs) {
		List<BiometricException> exceptionBiometrics = new LinkedList<>();

		// Add finger-print biometric exceptions
		if (biometricExceptionDTOs != null) {
			for (BiometricExceptionDTO biometricExceptionDTO : biometricExceptionDTOs) {
				exceptionBiometrics.add(buildExceptionBiometric(biometricExceptionDTO.getBiometricType(),
						biometricExceptionDTO.getMissingBiometric(), biometricExceptionDTO.getExceptionType(),
						biometricExceptionDTO.getReason(), biometricExceptionDTO.getIndividualType()));
			}
		}

		return exceptionBiometrics;
	}

	private BiometricException buildExceptionBiometric(String type, String missingBiometric, String exceptionType,
			String reason, String individualType) {
		BiometricException exceptionBiometric = new BiometricException();
		exceptionBiometric.setType(type);
		exceptionBiometric.setMissingBiometric(missingBiometric);
		exceptionBiometric.setExceptionType(exceptionType);
		exceptionBiometric.setReason(reason);
		exceptionBiometric.setIndividualType(individualType);
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
		// Add Pre-Registration ID
		metaData.add(buildFieldValue("preRegistrationId", registrationDTO.getPreRegistrationId()));
		// Add Registration ID
		metaData.add(buildFieldValue("registrationId", registrationDTO.getRegistrationId()));
		// Add Machine ID
		metaData.add(buildFieldValue("machineId", metaDataDTO.getMachineId()));
		// Add Dongle ID
		metaData.add(buildFieldValue("dongleId", metaDataDTO.getDeviceId()));
		// Add MAC ID
		metaData.add(buildFieldValue("macId", RegistrationSystemPropertiesChecker.getMachineId()));
		// Add Center ID
		metaData.add(buildFieldValue("centerId", metaDataDTO.getCenterId()));
		// Add Previous Registration ID
		metaData.add(buildFieldValue("previousRID", metaDataDTO.getPreviousRID()));
		// Add Introducer Type
		metaData.add(buildFieldValue("introducerType", registrationDTO.getOsiDataDTO().getIntroducerType()));
		// Add consentOfApplicant
		metaData.add(buildFieldValue("consentOfApplicant",
				registrationDTO.getRegistrationMetaDataDTO().getConsentOfApplicant()));
		// Add Registration Creation Date
		metaData.add(buildFieldValue("creationDate", DateUtils.formatToISOString(LocalDateTime.now())));

		metaData.add(buildFieldValue("authenticationBiometricFileName",
				registrationDTO.isUpdateUINNonBiometric()
						? removeFileExt(RegistrationConstants.AUTHENTICATION_BIO_CBEFF_FILE_NAME)
						: null));

		return metaData;
	}

	@SuppressWarnings("unchecked")
	private List<FieldValue> getOSIData(RegistrationDTO registrationDTO) {
		List<FieldValue> osiData = new LinkedList<>();
		// Add Operator ID
		osiData.add(buildFieldValue("officerId", registrationDTO.getOsiDataDTO().getOperatorID()));
		// Add Officer CBEFF File
		if (((Map<String, String>) SessionContext.map().get(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME)).keySet()
				.stream().anyMatch(key -> key.startsWith(RegistrationConstants.OFFICER.toLowerCase()))) {
			osiData.add(buildFieldValue("officerBiometricFileName",
					removeFileExt(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME)));
		} else {
			osiData.add(buildFieldValue("officerBiometricFileName", null));
		}

		// Add Supervisor ID
		osiData.add(buildFieldValue("supervisorId", registrationDTO.getOsiDataDTO().getSupervisorID()));
		// Add Officer CBEFF File
		if (((Map<String, String>) SessionContext.map().get(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME)).keySet()
				.stream().anyMatch(key -> key.startsWith(RegistrationConstants.SUPERVISOR.toLowerCase()))) {
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

		// Add Supervisor OTP Authentication Image
		osiData.add(buildFieldValue("supervisorOTPAuthentication",
				String.valueOf(registrationDTO.getOsiDataDTO().isSuperviorAuthenticatedByPIN())));
		// Add Officer Face Image
		osiData.add(buildFieldValue("officerOTPAuthentication",
				String.valueOf(registrationDTO.getOsiDataDTO().isOperatorAuthenticatedByPIN())));

		return osiData;
	}

	private FieldValue buildFieldValue(String label, String value) {
		FieldValue fieldValue = new FieldValue();
		fieldValue.setLabel(label);
		fieldValue.setValue(value == null || value.isEmpty() ? null : value);
		return fieldValue;
	}

	private String removeFileExt(String fileName) {
		if (fileName.contains(RegistrationConstants.DOT)) {
			fileName = fileName.substring(0, fileName.lastIndexOf(RegistrationConstants.DOT));
		}
		return fileName;
	}

	@SuppressWarnings("unchecked")
	private String getBIRUUID(String personType, String biometricType) {
		return ((Map<String, String>) SessionContext.map().get(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME))
				.get(personType.concat(biometricType).toLowerCase());
	}

}
