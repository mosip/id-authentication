package io.mosip.kernel.idrepo.service.impl;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.SdkBaseException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.idrepo.spi.IdRepoService;
import io.mosip.kernel.core.idrepo.spi.MosipFingerprintProvider;
import io.mosip.kernel.core.idrepo.spi.ShardDataSourceResolver;
import io.mosip.kernel.core.idrepo.spi.ShardResolver;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idrepo.config.IdRepoLogger;
import io.mosip.kernel.idrepo.controller.IdRepoController;
import io.mosip.kernel.idrepo.dto.Documents;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.dto.IdResponseDTO;
import io.mosip.kernel.idrepo.dto.ResponseDTO;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.entity.UinBiometric;
import io.mosip.kernel.idrepo.entity.UinBiometricHistory;
import io.mosip.kernel.idrepo.entity.UinDocument;
import io.mosip.kernel.idrepo.entity.UinDocumentHistory;
import io.mosip.kernel.idrepo.entity.UinHistory;
import io.mosip.kernel.idrepo.repository.UinBiometricHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinDocumentHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinRepo;
import io.mosip.kernel.idrepo.util.DFSConnectionUtil;

/**
 * The Class IdRepoServiceImpl.
 *
 * @author Manoj SP
 */
@Service
public class IdRepoServiceImpl implements IdRepoService<IdRequestDTO, IdResponseDTO, Uin> {

	private static final String ADD_IDENTITY = "addIdentity";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoServiceImpl.class);

	private static final String ID_REPO_SERVICE = "IdRepoService";

	private static final String APPLICATION_VERSION = "application.version";

	private static final String DOCUMENTS = "documents";

	/** The Constant TYPE. */
	private static final String TYPE = "type";

	/** The Constant DOT. */
	private static final String DOT = ".";

	/** The Constant SLASH. */
	private static final String SLASH = "/";

	/** The Constant RETRIEVE_IDENTITY. */
	private static final String RETRIEVE_IDENTITY = "retrieveIdentity";

	/** The Constant ENTITY. */
	private static final String ENTITY = "entity";

	/** The Constant ERR. */
	private static final String ERR = "error";

	/** The Constant RESPONSE_FILTER. */
	private static final String RESPONSE_FILTER = "responseFilter";

	/** The Constant BIOMETRICS. */
	private static final String BIOMETRICS = "Biometrics";

	/** The Constant BIO. */
	private static final String BIO = "bio";

	/** The Constant DEMO. */
	private static final String DEMO = "demo";

	/** The Constant ID_REPO_SERVICE_IMPL. */
	private static final String ID_REPO_SERVICE_IMPL = "IdRepoServiceImpl";

	/** The Constant CBEFF. */
	private static final String CBEFF = "cbeff";

	/** The Constant FORMAT. */
	private static final String FORMAT = "format";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/** The Constant LANG_CODE. */
	private static final String LANG_CODE = "AR";

	/** The Constant CREATE. */
	private static final String CREATE = "create";

	/** The Constant READ. */
	private static final String READ = "read";

	/** The Constant UPDATE. */
	private static final String UPDATE = "update";

	/** The Constant IDENTITY. */
	private static final String IDENTITY = "identity";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "createdBy";

	/** The Constant MOSIP_IDREPO_STATUS_REGISTERED. */
	private static final String MOSIP_IDREPO_STATUS_REGISTERED = "mosip.kernel.idrepo.status.registered";

	/** The Constant UPDATED_BY. */
	private static final String UPDATED_BY = "updatedBy";

	/** The Constant SUCCESS_UPLOAD_MESSAGE. */
	private static final String SUCCESS_UPLOAD_MESSAGE = "Successfully uploaded to DFS";

	/** The Constant ALL. */
	private static final String ALL = "all";

	/** The Constant DEMOGRAPHICS. */
	private static final String DEMOGRAPHICS = "Demographics";

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The id. */
	@Resource
	private Map<String, String> id;

	@Resource
	private List<String> allowedBioTypes;

	/** The shard resolver. */
	@Autowired
	private ShardResolver shardResolver;

	/** The uin repo. */
	@Autowired
	private UinRepo uinRepo;

	/** The uin detail repo. */
	@Autowired
	private UinDocumentHistoryRepo uinDocHRepo;

	/** The uin bio H repo. */
	@Autowired
	private UinBiometricHistoryRepo uinBioHRepo;

	/** The uin history repo. */
	@Autowired
	private UinHistoryRepo uinHistoryRepo;

	/** The connection. */
	@Autowired
	private DFSConnectionUtil connection;

	@Autowired
	private MosipFingerprintProvider<String> fpProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idrepo.spi.IdRepoService#addIdentity(java.lang.Object)
	 */
	@Override
	public IdResponseDTO addIdentity(IdRequestDTO request, String uin) throws IdRepoAppException {
		try {
			ShardDataSourceResolver.setCurrentShard(shardResolver.getShard(uin));
			return constructIdResponse(
					this.id.get(CREATE), addIdentity(uin, request.getRegistrationId(),
							convertToBytes(request.getRequest().getIdentity()), request.getRequest().getDocuments()),
					null);
		} catch (IdRepoAppException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, ADD_IDENTITY,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e, this.id.get(CREATE));
		} catch (DataAccessException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, ADD_IDENTITY,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, ADD_IDENTITY,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR, e);
		}
	}

	/**
	 * Adds the identity to DB.
	 *
	 * @param uin
	 *            the uin
	 * @param regId
	 *            the uin ref id
	 * @param identityInfo
	 *            the identity info
	 * @param documents
	 *            the documents
	 * @return the uin
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Transactional
	public Uin addIdentity(String uin, String regId, byte[] identityInfo, List<Documents> documents)
			throws IdRepoAppException {
		// FIXME uinrefId needs to be fixed
		String uinRefId = UUID.randomUUID().toString().replace("-", "").substring(0, 28);

		if (!uinRepo.existsByRegId(regId) && !uinRepo.existsByUin(uin)) {
			List<UinDocument> docList = new ArrayList<>();
			List<UinBiometric> bioList = new ArrayList<>();
			if (Objects.nonNull(documents) && !documents.isEmpty()) {
				ObjectNode identityObject = (ObjectNode) convertToObject(identityInfo, ObjectNode.class);
				documents.stream().filter(doc -> identityObject.has(doc.getCategory())).forEach(doc -> {
					JsonNode docType = identityObject.get(doc.getCategory());
					try {
						if (StringUtils.equalsIgnoreCase(docType.get(FORMAT).asText(), CBEFF)) {
							String fileRefId = UUID.randomUUID().toString();

							storeFile(uin, BIOMETRICS + SLASH + fileRefId + DOT + docType.get(FORMAT).asText(),
									convertToFMR(doc.getValue()));

							bioList.add(new UinBiometric(uinRefId, fileRefId, doc.getCategory(),
									docType.get(VALUE).asText(), hash(CryptoUtil.decodeBase64(doc.getValue())),
									LANG_CODE, CREATED_BY, now(), UPDATED_BY, now(), false, now()));

							uinBioHRepo.save(new UinBiometricHistory(uinRefId, now(), fileRefId, doc.getCategory(),
									docType.get(VALUE).asText(), hash(CryptoUtil.decodeBase64(doc.getValue())),
									LANG_CODE, CREATED_BY, now(), UPDATED_BY, now(), false, now()));

						} else {
							String fileRefId = UUID.randomUUID().toString();

							storeFile(uin, DEMOGRAPHICS + SLASH + fileRefId + DOT + docType.get(FORMAT).asText(),
									CryptoUtil.decodeBase64(doc.getValue()));

							docList.add(new UinDocument(uinRefId, docType.get(TYPE).asText(), doc.getCategory(),
									fileRefId, docType.get(VALUE).asText(), docType.get(FORMAT).asText(),
									hash(CryptoUtil.decodeBase64(doc.getValue())), LANG_CODE, CREATED_BY, now(),
									UPDATED_BY, now(), false, now()));

							uinDocHRepo.save(new UinDocumentHistory(uinRefId, now(), docType.get(TYPE).asText(),
									doc.getCategory(), fileRefId, docType.get(VALUE).asText(),
									docType.get(FORMAT).asText(), hash(CryptoUtil.decodeBase64(doc.getValue())),
									LANG_CODE, CREATED_BY, now(), UPDATED_BY, now(), false, now()));
						}
					} catch (IdRepoAppException e) {
						mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, ADD_IDENTITY,
								"\n" + ExceptionUtils.getStackTrace(e));
						throw new IdRepoAppUncheckedException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR, e);
					}
				});

				uinRepo.save(new Uin(uinRefId, uin, identityInfo, hash(identityInfo), regId,
						env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED), LANG_CODE, CREATED_BY, now(), UPDATED_BY,
						now(), false, now(), bioList, docList));
			} else {

				uinRepo.save(new Uin(uinRefId, uin, identityInfo, hash(identityInfo), regId,
						env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED), LANG_CODE, CREATED_BY, now(), UPDATED_BY,
						now(), false, now(), null, null));
			}

			uinHistoryRepo.save(new UinHistory(uinRefId, now(), uin, identityInfo, hash(identityInfo), regId,
					env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED), LANG_CODE, CREATED_BY, now(), UPDATED_BY, now(),
					false, now()));

			return retrieveIdentityByUin(uin);
		} else {
			throw new IdRepoAppException(IdRepoErrorConstants.RECORD_EXISTS);
		}
	}

	private byte[] convertToFMR(String encodedCbeffFile) {
		return CryptoUtil.decodeBase64(fpProvider.convertFIRtoFMR(Collections.singletonList(encodedCbeffFile)).get(0));
	}

	/**
	 * Store file.
	 *
	 * @param uin
	 *            the uin
	 * @param filePathAndName
	 *            the file path and name
	 * @param fileData
	 *            the file data
	 * @return true, if successful
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private boolean storeFile(String uin, String filePathAndName, byte[] fileData) throws IdRepoAppException {
		try {
			AmazonS3 conn = connection.getConnection();
			mosipLogger.debug(ID_REPO_SERVICE_IMPL, uin, filePathAndName,
					"bucket exists with uin: " + uin + " -- " + conn.doesBucketExistV2(uin));
			if (!conn.doesBucketExistV2(uin)) {
				conn.createBucket(uin);
				mosipLogger.debug(ID_REPO_SERVICE_IMPL, uin, filePathAndName, "bucket created with uin : " + uin);
			}
			mosipLogger.debug(ID_REPO_SERVICE_IMPL, uin, filePathAndName, "before storing file");
			conn.putObject(uin, filePathAndName, new ByteArrayInputStream(fileData), null);
			mosipLogger.debug(ID_REPO_SERVICE_IMPL, uin, filePathAndName, SUCCESS_UPLOAD_MESSAGE);
		} catch (SdkClientException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, "storeFile",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR, e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idrepo.spi.IdRepoService#retrieveIdentity(java.lang.
	 * String)
	 */
	@Override
	public IdResponseDTO retrieveIdentity(String uin, String type) throws IdRepoAppException {
		try {
			ShardDataSourceResolver.setCurrentShard(shardResolver.getShard(uin));
			if (uinRepo.existsByUin(uin)) {
				List<Documents> documents = new ArrayList<>();
				Uin uinObject = retrieveIdentityByUin(uin);
				if (Objects.isNull(type)) {
					mosipLogger.info(ID_REPO_SERVICE_IMPL, RETRIEVE_IDENTITY, "method - " + RETRIEVE_IDENTITY,
							"filter - null");
					return constructIdResponse(this.id.get(READ), uinObject, null);
				} else if (type.equalsIgnoreCase(BIO)) {
					getFiles(uinObject, documents, BIOMETRICS);
					mosipLogger.info(ID_REPO_SERVICE_IMPL, RETRIEVE_IDENTITY, "filter - bio",
							"bio documents  --> " + documents);
					return constructIdResponse(this.id.get(READ), uinObject, documents);
				} else if (type.equalsIgnoreCase(DEMO)) {
					getFiles(uinObject, documents, DEMOGRAPHICS);
					mosipLogger.info(ID_REPO_SERVICE_IMPL, RETRIEVE_IDENTITY, "filter - demo",
							"docs documents  --> " + documents);
					return constructIdResponse(this.id.get(READ), uinObject, documents);
				} else if (type.equalsIgnoreCase(ALL)) {
					getFiles(uinObject, documents, BIOMETRICS);
					getFiles(uinObject, documents, DEMOGRAPHICS);
					mosipLogger.info(ID_REPO_SERVICE_IMPL, RETRIEVE_IDENTITY, "filter - all",
							"docs documents  --> " + documents);
					return constructIdResponse(this.id.get(READ), uinObject, documents);
				} else {
					throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), TYPE));
				}
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND);
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, RETRIEVE_IDENTITY,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e, this.id.get(READ));
		} catch (IdRepoAppUncheckedException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, RETRIEVE_IDENTITY,
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

	/**
	 * Gets the files.
	 *
	 * @param uinObject
	 *            the uin object
	 * @param documents
	 *            the documents
	 * @param type
	 *            the type
	 * @return the files
	 */
	private void getFiles(Uin uinObject, List<Documents> documents, String type) {
		if (type.equals(BIOMETRICS)) {
			uinObject.getBiometrics().parallelStream().forEach(bio -> {
				if (allowedBioTypes.contains(bio.getBiometricFileType())) {
					try {
						ObjectNode identityMap = (ObjectNode) convertToObject(uinObject.getUinData(), ObjectNode.class);
						String fileName = BIOMETRICS + SLASH + bio.getBioFileId() + DOT
								+ identityMap.get(bio.getBiometricFileType()).get(FORMAT).asText();
						String data = getFile(uinObject.getUin(), fileName);
						if (Objects.nonNull(data)) {
							if (bio.getBiometricFileHash().equals(hash(CryptoUtil.decodeBase64(data)))) {
								documents.add(new Documents(bio.getBiometricFileType(), data));
							} else {
								throw new IdRepoAppException(IdRepoErrorConstants.DOCUMENT_HASH_MISMATCH);
							}
						}
					} catch (IdRepoAppException e) {
						mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, "getFiles",
								"\n" + ExceptionUtils.getStackTrace(e));
						throw new IdRepoAppUncheckedException(e.getErrorCode(), e.getErrorText(), e);
					}
				}
			});
		}

		if (type.equals(DEMOGRAPHICS)) {
			uinObject.getDocuments().parallelStream().forEach(demo -> {
				try {
					ObjectNode identityMap = (ObjectNode) convertToObject(uinObject.getUinData(), ObjectNode.class);
					String fileName = DEMOGRAPHICS + SLASH + demo.getDocId() + DOT
							+ identityMap.get(demo.getDoctypCode()).get(FORMAT).asText();
					String data = getFile(uinObject.getUin(), fileName);
					if (demo.getDocHash().equals(hash(CryptoUtil.decodeBase64(data)))) {
						documents.add(new Documents(demo.getDoctypCode(), data));
					} else {
						throw new IdRepoAppException(IdRepoErrorConstants.DOCUMENT_HASH_MISMATCH);
					}
				} catch (IdRepoAppException e) {
					mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, "getFiles",
							"\n" + ExceptionUtils.getStackTrace(e));
					throw new IdRepoAppUncheckedException(e.getErrorCode(), e.getErrorText(), e);
				}
			});
		}
	}

	/**
	 * Gets the file.
	 *
	 * @param uin
	 *            the uin
	 * @param filePathAndName
	 *            the file path and name
	 * @return the file
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private String getFile(String uin, String filePathAndName) throws IdRepoAppException {
		try {
			if (connection.getConnection().doesBucketExistV2(uin)
					&& connection.getConnection().doesObjectExist(uin, filePathAndName)) {
				return CryptoUtil.encodeBase64(IOUtils.toByteArray((InputStream) connection.getConnection()
						.getObject(new GetObjectRequest(uin, filePathAndName)).getObjectContent()));

			}
		} catch (SdkBaseException | IOException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, "getFile", "\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR, e);
		}
		return null;
	}

	/**
	 * Retrieve identity by uin from DB.
	 *
	 * @param uin
	 *            the uin
	 * @return the uin
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Transactional
	public Uin retrieveIdentityByUin(String uin) throws IdRepoAppException {
		return uinRepo.findByUin(uin);
	}

	/**
	 * Construct id response.
	 *
	 * @param id
	 *            the id
	 * @param uin
	 *            the uin
	 * @param documents
	 *            the documents
	 * @return the id response DTO
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private IdResponseDTO constructIdResponse(String id, Uin uin, List<Documents> documents) throws IdRepoAppException {
		IdResponseDTO idResponse = new IdResponseDTO();
		Set<String> ignoredProperties = new HashSet<>();

		idResponse.setId(id);

		idResponse.setVersion(env.getProperty(APPLICATION_VERSION));

		idResponse.setTimestamp(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));

		idResponse.setStatus(uin.getStatusCode());

		ResponseDTO response = new ResponseDTO();

		if (id.equals(this.id.get(CREATE)) || id.equals(this.id.get(UPDATE))) {
			response.setEntity(
					linkTo(methodOn(IdRepoController.class).retrieveIdentity(uin.getUin().trim(), null, null)).toUri()
							.toString());
			mapper.setFilterProvider(new SimpleFilterProvider().addFilter(RESPONSE_FILTER,
					SimpleBeanPropertyFilter.serializeAllExcept(IDENTITY, ERR, DOCUMENTS)));
		} else {
			ignoredProperties.add(ENTITY);
			ignoredProperties.add(ERR);

			if (Objects.isNull(documents)) {
				ignoredProperties.add(DOCUMENTS);
			} else {
				response.setDocuments(documents);
			}

			response.setIdentity(convertToObject(uin.getUinData(), Object.class));

			mapper.setFilterProvider(new SimpleFilterProvider().addFilter(RESPONSE_FILTER,
					SimpleBeanPropertyFilter.serializeAllExcept(ignoredProperties)));

		}

		idResponse.setResponse(response);

		return idResponse;
	}

	/**
	 * Convert to bytes.
	 *
	 * @param identity
	 *            the identity
	 * @return the byte[]
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private byte[] convertToBytes(Object identity) throws IdRepoAppException {
		try {
			return mapper.writeValueAsBytes(identity);
		} catch (JsonProcessingException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, "convertToBytes",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.JSON_PROCESSING_FAILED, e);
		}
	}

	/**
	 * Convert to object.
	 *
	 * @param identity
	 *            the identity
	 * @param clazz
	 *            the clazz
	 * @return the object
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private Object convertToObject(byte[] identity, Class<?> clazz) throws IdRepoAppException {
		try {
			return mapper.readValue(identity, clazz);

		} catch (IOException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, "convertToObject",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.JSON_PROCESSING_FAILED, e);
		}
	}

	/**
	 * Get the current time.
	 *
	 * @return the date
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private LocalDateTime now() throws IdRepoAppException {
		try {
			return DateUtils.parseUTCToLocalDateTime(
					DateUtils.formatDate(new Date(), env.getProperty(DATETIME_PATTERN)),
					env.getProperty(DATETIME_PATTERN));
		} catch (ParseException e) {
			mosipLogger.error(ID_REPO_SERVICE, ID_REPO_SERVICE_IMPL, "now()", "\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), "DATETIME_PATTERN"), e);
		}
	}

	/**
	 * Hash.
	 *
	 * @param identityInfo
	 *            the identity info
	 * @return the string
	 */
	private String hash(byte[] identityInfo) {
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(identityInfo));
	}

	@Override
	public IdResponseDTO updateIdentity(IdRequestDTO request, String uin) throws IdRepoAppException {
		return null;
	}

}
