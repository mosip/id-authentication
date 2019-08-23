package io.mosip.registration.service.external.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_STORAGE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.ZIP_FILE_EXTENSION;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_IO_EXCEPTION;
import static java.io.File.separator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.SecretKey;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.DocumentTypeDAO;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.external.PreRegZipHandlingService;

/**
 * This implementation class to handle the pre-registration data
 * 
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 *
 */
@Service
public class PreRegZipHandlingServiceImpl implements PreRegZipHandlingService {

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
	private DocumentTypeDAO documentTypeDAO;

	@Autowired
	private MasterSyncDao masterSyncDao;

	private static final Logger LOGGER = AppConfig.getLogger(PreRegZipHandlingServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.external.PreRegZipHandlingService#
	 * extractPreRegZipFile(byte[])
	 */
	@Override
	public RegistrationDTO extractPreRegZipFile(byte[] preRegZipFile) throws RegBaseCheckedException {

		RegistrationDTO registrationDTO = getRegistrationDtoContent();
		DocumentDetailsDTO documentDetailsDTO;
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {
			ZipInputStream inputStream= new ZipInputStream(new ByteArrayInputStream(preRegZipFile));
			ZipEntry zipEntry;
			BufferedReader bufferedReader = null;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String jsoFileName = zipEntry.getName();
				if (jsoFileName.endsWith(".json")) {
					bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));
					parseDemographicJson(bufferedReader, zipEntry);
					break;
				} 
			}
			
			while ((zipEntry = inputStream.getNextEntry()) != null) {
				String docFileName = zipEntry.getName();
				if (docFileName.contains("_")) {
					documentDetailsDTO = new DocumentDetailsDTO();
					String docCategoryCode = docFileName.substring(0, docFileName.indexOf("_"));
					getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocuments()
							.put(docCategoryCode, documentDetailsDTO);
					attachDocument(documentDetailsDTO, inputStream, docFileName, docCategoryCode);

				}
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (IOException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_ZIP_CREATION, exception.getMessage());
		}
		return registrationDTO;
	}

	private void attachDocument(DocumentDetailsDTO documentDetailsDTO, ZipInputStream zipInputStream, String fileName,
			String docCatgory) throws IOException {
		documentDetailsDTO.setDocument(IOUtils.toByteArray(zipInputStream));
		documentDetailsDTO.setFormat(fileName.substring(fileName.lastIndexOf(RegistrationConstants.DOT) + 1));

		IndividualIdentity individualIdentity = (IndividualIdentity) getRegistrationDtoContent().getDemographicDTO()
				.getDemographicInfoDTO().getIdentity();

		String docTypeName = null;
		if (individualIdentity != null) {
			docTypeName = getDocTypeName(fileName, docCatgory, individualIdentity);
		}

		/*
		 * checking and setting the doc type name based on the reg client primary
		 * language irrespective of pre reg language
		 */
		docTypeName = getDocTypeForPrimaryLanguage(docTypeName);
		documentDetailsDTO.setType(docTypeName);
		documentDetailsDTO.setValue(docCatgory.concat("_").concat(docTypeName));
	}

	private String getDocTypeForPrimaryLanguage(String docTypeName) {
		if (StringUtils.isNotEmpty(docTypeName)) {
			List<DocumentType> documentTypes = documentTypeDAO.getDocTypeByName(docTypeName);
			if (isListNotEmpty(documentTypes)
					&& !ApplicationContext.applicationLanguage().equalsIgnoreCase(documentTypes.get(0).getLangCode())) {
				List<DocumentType> docTypesForPrimaryLanguage = masterSyncDao.getDocumentTypes(
						Arrays.asList(documentTypes.get(0).getCode()), ApplicationContext.applicationLanguage());
				if (isListNotEmpty(docTypesForPrimaryLanguage)) {
					docTypeName = docTypesForPrimaryLanguage.get(0).getName();
				}
			}
		}
		return docTypeName;
	}

	private String getDocTypeName(String fileName, String docCatgory, IndividualIdentity individualIdentity) {
		String docTypeName;
		if (RegistrationConstants.POA_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != individualIdentity.getProofOfAddress()) {

			docTypeName = individualIdentity.getProofOfAddress().getType();

		} else if (RegistrationConstants.POI_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != individualIdentity.getProofOfIdentity()) {
			docTypeName = individualIdentity.getProofOfIdentity().getType();

		} else if (RegistrationConstants.POR_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != individualIdentity.getProofOfRelationship()) {
			docTypeName = individualIdentity.getProofOfRelationship().getType();

		} else if (RegistrationConstants.DOB_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != individualIdentity.getProofOfDateOfBirth()) {
			docTypeName = individualIdentity.getProofOfDateOfBirth().getType();

		} else {
			docTypeName = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
		}
		return docTypeName;
	}

	/**
	 * This method is used to parse the demographic json and converts it into
	 * RegistrationDto
	 * 
	 * @param bufferedReader
	 *            - reader for text file
	 * @param zipEntry
	 *            - a file entry in zip
	 * @throws RegBaseCheckedException
	 *             - holds the cheked exceptions
	 */
	private void parseDemographicJson(BufferedReader bufferedReader, ZipEntry zipEntry) throws RegBaseCheckedException {

		try {
			String value;
			StringBuilder jsonString = new StringBuilder();
			while ((value = bufferedReader.readLine()) != null) {
				jsonString.append(value);
			}

			if (!StringUtils.isEmpty(jsonString) 
					&& validateDemographicInfoObject()) {
				/* validate id json schema */
				IndividualIdentity individualIdentity = (IndividualIdentity) JsonUtils.jsonStringToJavaObject(
						IndividualIdentity.class, new JSONObject(jsonString.toString()).get("identity").toString());
				getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().setIdentity(individualIdentity);
				/*idObjectValidator.validateIdObject(
						getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO(),
						RegistrationConstants.PACKET_TYPE_NEW);*/

			}
		} catch (IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (JsonParseException | JsonMappingException | JSONException
				| io.mosip.kernel.core.exception.IOException jsonValidationException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorMessage(),
					jsonValidationException);
		} catch (BaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(baseCheckedException.getErrorCode(), baseCheckedException.getErrorText());
		}

	}

	private boolean validateDemographicInfoObject() {
		return null != getRegistrationDtoContent() && getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.external.PreRegZipHandlingService#
	 * encryptAndSavePreRegPacket(java.lang.String, byte[])
	 */
	@Override
	public PreRegistrationDTO encryptAndSavePreRegPacket(String preRegistrationId, byte[] preRegPacket)
			throws RegBaseCheckedException {

		SecretKey symmetricKey = keyGenerator.getSymmetricKey();

		// Encrypt the Pre reg packet data using AES
		final byte[] encryptedData = MosipEncryptor.symmetricEncrypt(symmetricKey.getEncoded(), preRegPacket,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

		LOGGER.info(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Pre Registration packet Encrypted");

		String filePath = storePreRegPacketToDisk(preRegistrationId, encryptedData);

		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath(filePath);
		preRegistrationDTO.setSymmetricKey(Base64.getEncoder().encodeToString(symmetricKey.getEncoded()));
		preRegistrationDTO.setEncryptedPacket(encryptedData);
		preRegistrationDTO.setPreRegId(preRegistrationId);
		return preRegistrationDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.external.PreRegZipHandlingService#
	 * storePreRegPacketToDisk(java.lang.String, byte[])
	 */
	@Override
	public String storePreRegPacketToDisk(String preRegistrationId, byte[] encryptedPacket)
			throws RegBaseCheckedException {
		try {
			// Generate the file path for storing the Encrypted Packet
			String filePath = String
					.valueOf(ApplicationContext.map().get(RegistrationConstants.PRE_REG_PACKET_LOCATION))
					.concat(separator).concat(preRegistrationId).concat(ZIP_FILE_EXTENSION);
			// Storing the Encrypted Registration Packet as zip
			FileUtils.copyToFile(new ByteArrayInputStream(encryptedPacket),
					FileUtils.getFile(FilenameUtils.getFullPath(filePath) + FilenameUtils.getName(filePath)));

			LOGGER.info(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Pre Registration Encrypted packet saved");

			return filePath;
		} catch (io.mosip.kernel.core.exception.IOException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(),
					REG_IO_EXCEPTION.getErrorMessage() + ExceptionUtils.getStackTrace(exception));
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseUncheckedException(RegistrationConstants.ENCRYPTED_PACKET_STORAGE,
					runtimeException.toString(), runtimeException);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.external.PreRegZipHandlingService#
	 * decryptPreRegPacket(java.lang.String, byte[])
	 */
	@Override
	public byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket) {

		return MosipDecryptor.symmetricDecrypt(Base64.getDecoder().decode(symmetricKey), encryptedPacket,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	private RegistrationDTO getRegistrationDtoContent() {
		return (RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA);
	}

	private boolean isListNotEmpty(List<?> values) {
		return values != null && !values.isEmpty();
	}
}
