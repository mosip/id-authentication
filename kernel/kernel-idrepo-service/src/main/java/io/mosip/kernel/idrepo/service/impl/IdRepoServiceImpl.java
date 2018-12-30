package io.mosip.kernel.idrepo.service.impl;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.idrepo.spi.IdRepoService;
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
import io.mosip.kernel.idrepo.repository.UinBiometricRepo;
import io.mosip.kernel.idrepo.repository.UinDocumentHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinDocumentRepo;
import io.mosip.kernel.idrepo.repository.UinHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinRepo;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.ConnectionUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class IdRepoServiceImpl.
 *
 * @author Manoj SP
 */
@Service
public class IdRepoServiceImpl implements IdRepoService<IdRequestDTO, IdResponseDTO, Uin> {

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

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant UPDATE. */
	private static final String UPDATE = "update";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoServiceImpl.class);

	/** The Constant LANGUAGE. */
	private static final String LANGUAGE = "language";

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

	/** The doc attributes. */
	@Resource
	private List<String> docAttributes;

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/** The shard resolver. */
	@Autowired
	private ShardResolver shardResolver;

	/** The uin repo. */
	@Autowired
	private UinRepo uinRepo;

	/** The uin detail repo. */
	@Autowired
	private UinDocumentRepo uinDocRepo;

	/** The uin bio repo. */
	@Autowired
	private UinBiometricRepo uinBioRepo;

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
	private ConnectionUtil connection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idrepo.spi.IdRepoService#addIdentity(java.lang.Object)
	 */
	@Override
	public IdResponseDTO addIdentity(IdRequestDTO request) throws IdRepoAppException {
		try {
			ShardDataSourceResolver.setCurrentShard(shardResolver.getShard(request.getUin()));
			return constructIdResponse(this.id.get(CREATE), addIdentity(request.getUin(), request.getRegistrationId(),
					convertToBytes(request.getRequest()), request.getDocuments()));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e, this.id.get(CREATE));
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
		try {
			// FIXME uinrefId needs to be fixed
			String uinRefId = UUID.randomUUID().toString().replace("-", "").substring(0, 28);

			if (!uinRepo.existsByRegId(regId) && !uinRepo.existsByUin(uin)) {
				if (Objects.nonNull(documents) && !documents.isEmpty()) {
					if (storeDocuments(uin, uinRefId, identityInfo, documents).entrySet().stream()
							.anyMatch(entry -> !(boolean) entry.getValue())) {
						throw new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_FAILED);
					}
				}

				uinHistoryRepo.save(new UinHistory(uinRefId, now(), uin, identityInfo, hash(identityInfo), regId,
						env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED), LANG_CODE, CREATED_BY, now(), UPDATED_BY,
						now(), false, now()));

				return uinRepo.save(new Uin(uinRefId, uin, identityInfo, hash(identityInfo), regId,
						env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED), LANG_CODE, CREATED_BY, now(), UPDATED_BY,
						now(), false, now()));
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.RECORD_EXISTS);
			}
		} catch (DataAccessException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		}
	}

	/**
	 * Store documents.
	 *
	 * @param uin
	 *            the uin
	 * @param uinRefId
	 *            the uin ref id
	 * @param identity
	 *            the identity
	 * @param docList
	 *            the doc list
	 * @return the map
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private Map<String, Boolean> storeDocuments(String uin, String uinRefId, byte[] identity, List<Documents> docList)
			throws IdRepoAppException {
		try {
			ObjectNode identityObject = (ObjectNode) convertToObject(identity, ObjectNode.class);
			return docList.stream().filter(doc -> identityObject.get(IDENTITY).has(doc.getDocType())).map(doc -> {
				JsonNode docType = identityObject.get(IDENTITY).get(doc.getDocType());
				String fileName = docType.get(VALUE).asText() + "." + docType.get(FORMAT).asText();
				try {
					if (StringUtils.equalsIgnoreCase(docType.get(FORMAT).asText(), CBEFF)) {
						StringBuilder bioId = new StringBuilder();
						bioId.append("Biometrics/");
						bioId.append(doc.getDocType());
						bioId.append("/");

						storeFile(uin, bioId + fileName, CryptoUtil.decodeBase64(doc.getDocValue()));

						uinBioRepo.save(new UinBiometric(uinRefId, bioId.toString(), docType.get(VALUE).asText(),
								hash(CryptoUtil.decodeBase64(doc.getDocValue())), LANG_CODE, CREATED_BY, now(),
								UPDATED_BY, now(), false, now()));

						uinBioHRepo.save(new UinBiometricHistory(uinRefId, now(), bioId.toString(),
								docType.get(VALUE).asText(), hash(CryptoUtil.decodeBase64(doc.getDocValue())),
								LANG_CODE, CREATED_BY, now(), UPDATED_BY, now(), false, now()));
					} else {
						StringBuilder docId = new StringBuilder();
						docId.append("Documents/");
						docId.append(doc.getDocType());
						docId.append("/");

						storeFile(uin, docId + fileName, CryptoUtil.decodeBase64(doc.getDocValue()));

						uinDocRepo.save(new UinDocument(uinRefId, docType.get("category").asText(), doc.getDocType(),
								docId.toString(), docType.get(VALUE).asText(), docType.get(FORMAT).asText(),
								hash(CryptoUtil.decodeBase64(doc.getDocValue())), LANG_CODE, CREATED_BY, now(),
								UPDATED_BY, now(), false, now()));

						uinDocHRepo.save(new UinDocumentHistory(uinRefId, now(), docType.get("category").asText(),
								doc.getDocType(), docId.toString(), docType.get(VALUE).asText(),
								docType.get(FORMAT).asText(), hash(CryptoUtil.decodeBase64(doc.getDocValue())),
								LANG_CODE, CREATED_BY, now(), UPDATED_BY, now(), false, now()));
					}

					return Collections.singletonMap(fileName, true);
				} catch (IdRepoAppException e) {
					return Collections.singletonMap(fileName, false);
				}
			}).collect(Collectors.toMap((Map<String, Boolean> map) -> map.keySet().iterator().next(),
					(Map<String, Boolean> value) -> value.values().iterator().next()));
		} catch (IdRepoAppUncheckedException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		}
	}

	/**
	 * Store file.
	 *
	 * @param uin the uin
	 * @param filePathAndName the file path and name
	 * @param fileData the file data
	 * @return true, if successful
	 * @throws IdRepoAppException the id repo app exception
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
			mosipLogger.error("IdRepoService", ID_REPO_SERVICE_IMPL, "storeFile",
					"\n" + ExceptionUtils.getStackTrace(e));
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
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
	public IdResponseDTO retrieveIdentity(String uin) throws IdRepoAppException {
		try {
			validateUIN(uin);
			return constructIdResponse(this.id.get(READ), retrieveIdentityByUin(uin));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, e, this.id.get(READ));
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idrepo.spi.IdRepoService#updateIdentity(java.lang.
	 * Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IdResponseDTO updateIdentity(IdRequestDTO request) throws IdRepoAppException {
		try {
			validateUIN(request.getUin());
			Uin dbUinData = retrieveIdentityByUin(request.getUin());
			Uin uinObject = null;
			if (!request.getStatus().equals(dbUinData.getStatusCode())) {
				uinObject = updateUinStatus(dbUinData, request.getStatus());
			}

			if (!Objects.equals(mapper.writeValueAsString(request.getRequest()), new String(dbUinData.getUinData()))) {
				Map<String, Map<String, List<Map<String, String>>>> requestData = convertToMap(request.getRequest());
				Map<String, Map<String, List<Map<String, String>>>> dbData = (Map<String, Map<String, List<Map<String, String>>>>) convertToObject(
						dbUinData.getUinData(), Map.class);
				MapDifference<String, List<Map<String, String>>> mapDifference = Maps
						.difference(requestData.get(IDENTITY), dbData.get(IDENTITY));
				mapDifference.entriesOnlyOnLeft().forEach((key, value) -> dbData.get(IDENTITY).put(key, value));
				mapDifference.entriesDiffering()
						.forEach((String key, ValueDifference<List<Map<String, String>>> value) -> dbData.get(IDENTITY)
								.put(key, findDifference(value.leftValue(), value.rightValue())));

				uinObject = updateIdentityInfo(dbUinData, convertToBytes(dbData));
			}

			if (Objects.isNull(uinObject)) {
				return constructIdResponse(this.id.get(UPDATE), dbUinData);
			} else {
				return constructIdResponse(this.id.get(UPDATE), uinObject);
			}
		} catch (JsonProcessingException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * Find difference.
	 *
	 * @param leftValue
	 *            the left value
	 * @param rightValue
	 *            the right value
	 * @return the list
	 */
	private List<Map<String, String>> findDifference(List<Map<String, String>> leftValue,
			List<Map<String, String>> rightValue) {

		TreeSet<Map<String, String>> leftValueSet = Sets.newTreeSet((Map<String, String> map1,
				Map<String, String> map2) -> StringUtils.compareIgnoreCase(map1.get(LANGUAGE), map2.get(LANGUAGE)));
		leftValueSet.addAll(leftValue);
		leftValue.clear();

		TreeSet<Map<String, String>> rightValueSet = Sets.newTreeSet((Map<String, String> map1,
				Map<String, String> map2) -> StringUtils.compareIgnoreCase(map1.get(LANGUAGE), map2.get(LANGUAGE)));
		rightValueSet.addAll(rightValue);
		rightValue.clear();

		leftValue.addAll(Sets.difference(rightValueSet, leftValueSet).copyInto(leftValueSet));
		rightValue.addAll(Sets.difference(leftValueSet, rightValueSet).copyInto(rightValueSet));

		IntStream.range(0, leftValue.size())
				.filter(i -> leftValue.get(i).get(LANGUAGE).equalsIgnoreCase(rightValue.get(i).get(LANGUAGE)))
				.forEach(i -> Maps.difference(leftValue.get(i), rightValue.get(i)).entriesDiffering().entrySet()
						.forEach(entry -> rightValue.get(i).put(entry.getKey(), entry.getValue().leftValue())));

		return rightValue;
	}

	/**
	 * Update uin status in DB.
	 *
	 * @param uin
	 *            the uin
	 * @param statusCode
	 *            the status code
	 * @return the uin
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Transactional
	public Uin updateUinStatus(Uin uin, String statusCode) throws IdRepoAppException {
		try {
			uinHistoryRepo.save(new UinHistory(uin.getUinRefId(), now(), uin.getUin(), uin.getUinData(),
					uin.getUinDataHash(), uin.getUinRefId(), LANG_CODE, statusCode, CREATED_BY, now(), UPDATED_BY,
					now(), false, now()));
			uin.setStatusCode(statusCode);
			return uinRepo.save(uin);
		} catch (DataAccessException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		}
	}

	/**
	 * Update idenity info in DB.
	 *
	 * @param uin
	 *            the uin
	 * @param identityInfo
	 *            the identity info
	 * @return the uin
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Transactional
	public Uin updateIdentityInfo(Uin uin, byte[] identityInfo) throws IdRepoAppException {
		try {
			uin.setUinData(identityInfo);
			return uinRepo.save(uin);
		} catch (DataAccessException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		}
	}

	/**
	 * Validate UIN.
	 *
	 * @param uin
	 *            the uin
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private void validateUIN(String uin) throws IdRepoAppException {
		ShardDataSourceResolver.setCurrentShard(shardResolver.getShard(uin));
		if (uinRepo.existsByUin(uin)) {
			String status = uinRepo.getStatusByUin(uin);
			if (!status.equals(env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED))) {
				throw new IdRepoAppException(IdRepoErrorConstants.NON_REGISTERED_UIN.getErrorCode(),
						String.format(IdRepoErrorConstants.NON_REGISTERED_UIN.getErrorMessage(), status));
			}
		} else {
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND);
		}
	}

	/**
	 * Construct id response.
	 *
	 * @param id
	 *            the id
	 * @param uin
	 *            the uin
	 * @return the id response DTO
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private IdResponseDTO constructIdResponse(String id, Uin uin) throws IdRepoAppException {
		IdResponseDTO idResponse = new IdResponseDTO();

		idResponse.setId(id);

		idResponse.setTimestamp(DateUtils.getDefaultUTCCurrentDateTimeString());

		idResponse.setUin(uin.getUin());

		idResponse.setStatus(uin.getStatusCode());

		ResponseDTO response = new ResponseDTO();

		try {
			if (id.equals(this.id.get(CREATE)) || id.equals(this.id.get(UPDATE))) {
				response.setEntity(linkTo(methodOn(IdRepoController.class).retrieveIdentity(uin.getUin().trim(), null))
						.toUri().toString());
				mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
						SimpleBeanPropertyFilter.serializeAllExcept(IDENTITY, "err")));
			} else {
				response.setIdentity(convertToObject(uin.getUinData(), Object.class));
				mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
						SimpleBeanPropertyFilter.serializeAllExcept("entity", "err")));
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.RESPONSE_CONSTRUCTION_ERROR, e);
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
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
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
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * Convert to map.
	 *
	 * @param identity
	 *            the identity
	 * @return the map
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private Map<String, Map<String, List<Map<String, String>>>> convertToMap(Object identity)
			throws IdRepoAppException {
		try {
			return mapper.readValue(mapper.writeValueAsBytes(identity),
					new TypeReference<Map<String, Map<String, List<Map<String, String>>>>>() {
					});
		} catch (IOException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), REQUEST), e);
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
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
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
		return CryptoUtil.encodeBase64(HMACUtils.generateHash(CryptoUtil.encodeBase64(identityInfo).getBytes()));
	}

}
