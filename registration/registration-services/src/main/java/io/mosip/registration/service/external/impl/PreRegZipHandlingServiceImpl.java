package io.mosip.registration.service.external.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_STORAGE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.ZIP_FILE_EXTENSION;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_IO_EXCEPTION;
import static java.io.File.separator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.StringUtils;
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
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.external.PreRegZipHandlingService;

/**
 * This class is used to handle the pre-registration packet zip files
 * 
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 *
 */
@Service
public class PreRegZipHandlingServiceImpl implements PreRegZipHandlingService {

	@Autowired
	@Qualifier("schema")
	private IdObjectValidator idObjectValidator;

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
	 * @see io.mosip.registration.service.external.impl.PreRegZipHandlingService#
	 * extractPreRegZipFile(byte[])
	 */
	@Override
	public RegistrationDTO extractPreRegZipFile(byte[] preRegZipFile) throws RegBaseCheckedException {

		RegistrationDTO registrationDTO = getRegistrationDtoContent();
		DocumentDetailsDTO documentDetailsDTO;
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {

			ZipEntry zipEntry;
			BufferedReader bufferedReader = null;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String fileName = zipEntry.getName();
				if (fileName.endsWith(".json")) {
					bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
					parseDemographicJson(bufferedReader, zipEntry);
				} else if (fileName.contains("_")) {
					documentDetailsDTO = new DocumentDetailsDTO();
					String docCategoryCode = fileName.substring(0, fileName.indexOf("_"));
					getRegistrationDtoContent().getDemographicDTO().getApplicantDocumentDTO().getDocuments()
							.put(docCategoryCode, documentDetailsDTO);
					attachDocument(documentDetailsDTO, zipInputStream, fileName, docCategoryCode);

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
		documentDetailsDTO.setType(docCatgory);
		documentDetailsDTO.setFormat(fileName.substring(fileName.lastIndexOf(RegistrationConstants.DOT) + 1));

		MoroccoIdentity moroccoIdentity = (MoroccoIdentity) getRegistrationDtoContent().getDemographicDTO()
				.getDemographicInfoDTO().getIdentity();

		String docTypeName = null;
		if (moroccoIdentity != null) {
			docTypeName = getDocTypeName(fileName, docCatgory, moroccoIdentity);
		}

		/*
		 * checking and setting the doc type name based on the reg client primary
		 * language irrespective of pre reg language
		 */
		docTypeName = getDocTypeForPrimaryLanguage(docTypeName);
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

	private String getDocTypeName(String fileName, String docCatgory, MoroccoIdentity moroccoIdentity) {
		String docTypeName;
		if (RegistrationConstants.POA_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != moroccoIdentity.getProofOfAddress()) {

			docTypeName = moroccoIdentity.getProofOfAddress().getType();

		} else if (RegistrationConstants.POI_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != moroccoIdentity.getProofOfIdentity()) {
			docTypeName = moroccoIdentity.getProofOfIdentity().getType();

		} else if (RegistrationConstants.POR_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != moroccoIdentity.getProofOfRelationship()) {
			docTypeName = moroccoIdentity.getProofOfRelationship().getType();

		} else if (RegistrationConstants.DOB_DOCUMENT.equalsIgnoreCase(docCatgory)
				&& null != moroccoIdentity.getProofOfDateOfBirth()) {
			docTypeName = moroccoIdentity.getProofOfDateOfBirth().getType();

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
			/*BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("//demo.txt")));

			// write contents of StringBuffer to a file
			bwr.write(jsonString.toString());

			// flush the stream
			bwr.flush();

			// close the stream
			bwr.close();*/
			if (!StringUtils.isEmpty(jsonString)) {
				/* validate id json schema */
				// getDemographicDTO().getDemographicInfoDTO().setIdentity(identity)

				getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO()
						.setIdentity(validateJSONAndConvertToIdentity(jsonString));
				idObjectValidator
						.validateIdObject(getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO());
			}
		} catch (IOException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (JSONException | IdObjectValidationProcessingException | IdObjectIOException | IdObjectSchemaIOException
				| FileIOException | ClassNotFoundException jsonValidationException) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorMessage()
							+ jsonValidationException.getMessage()
							+ ExceptionUtils.getStackTrace(jsonValidationException));
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorMessage(),
					jsonValidationException);
		}

	}

	@SuppressWarnings("unchecked")
	private Identity validateJSONAndConvertToIdentity(StringBuilder jsonString)
			throws IOException, JSONException, ClassNotFoundException {
		Class<? extends Identity> identityClass = (Class<? extends Identity>) Class
				.forName(String.valueOf(ApplicationContext.map().get(RegistrationConstants.IDENTITY_CLASS_NAME)));

		return new ObjectMapper().readValue(new JSONObject(jsonString.toString()).get("identity").toString(),
				identityClass);
	}

	/**
	 * This method is used to encrypt the pre registration packet and save it into
	 * the disk
	 * 
	 * @param PreRegistrationId
	 *            - pre registration id
	 * @param preRegPacket
	 *            - pre reg packet in bytes
	 * @return PreRegistrationDTO - pre reg dto holds the pre reg data
	 * @throws RegBaseCheckedException
	 *             - holds the checked exceptions
	 */
	@Override
	public PreRegistrationDTO encryptAndSavePreRegPacket(String PreRegistrationId, byte[] preRegPacket)
			throws RegBaseCheckedException {

		SecretKey symmetricKey = keyGenerator.getSymmetricKey();

		// Encrypt the Pre reg packet data using AES
		final byte[] encryptedData = MosipEncryptor.symmetricEncrypt(symmetricKey.getEncoded(), preRegPacket,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

		LOGGER.info(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Pre Registration packet Encrypted");

		String filePath = storePreRegPacketToDisk(PreRegistrationId, encryptedData);

		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath(filePath);
		preRegistrationDTO.setSymmetricKey(Base64.getEncoder().encodeToString(symmetricKey.getEncoded()));
		preRegistrationDTO.setEncryptedPacket(encryptedData);
		preRegistrationDTO.setPreRegId(PreRegistrationId);
		return preRegistrationDTO;

	}

	/**
	 * This method is used to store the encrypted packet into to the configured disk
	 * location
	 * 
	 * @param PreRegistrationId
	 *            - pre reg id
	 * @param encryptedPacket
	 *            - pre reg encrypted packet in bytes
	 * @return String - pre reg packet file path
	 * @throws RegBaseCheckedException
	 *             - holds the checked exceptions
	 */
	@Override
	public String storePreRegPacketToDisk(String PreRegistrationId, byte[] encryptedPacket)
			throws RegBaseCheckedException {
		try {
			// Generate the file path for storing the Encrypted Packet
			String filePath = String
					.valueOf(ApplicationContext.map().get(RegistrationConstants.PRE_REG_PACKET_LOCATION))
					.concat(separator).concat(PreRegistrationId).concat(ZIP_FILE_EXTENSION);
			// Storing the Encrypted Registration Packet as zip
			FileUtils.copyToFile(new ByteArrayInputStream(encryptedPacket),
					new File(FilenameUtils.getFullPath(filePath) + FilenameUtils.getName(filePath)));

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

	/**
	 * This method is used to decrypt the pre registration packet using the
	 * symmetric key
	 * 
	 * @param symmetricKey
	 *            - key to decrypt the pre reg packet
	 * @param encryptedPacket
	 *            - pre reg encrypted packet in bytes
	 * @return byte[] - decrypted pre reg packet
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
