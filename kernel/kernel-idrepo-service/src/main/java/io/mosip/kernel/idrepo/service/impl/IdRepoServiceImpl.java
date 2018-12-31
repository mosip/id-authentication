package io.mosip.kernel.idrepo.service.impl;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
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
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.dto.IdResponseDTO;
import io.mosip.kernel.idrepo.dto.ResponseDTO;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.entity.UinDetail;
import io.mosip.kernel.idrepo.entity.UinDetailHistory;
import io.mosip.kernel.idrepo.entity.UinHistory;
import io.mosip.kernel.idrepo.repository.UinDetailHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinDetailRepo;
import io.mosip.kernel.idrepo.repository.UinHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinRepo;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/**
 * The Class IdRepoServiceImpl.
 *
 * @author Manoj SP
 */
@Service
public class IdRepoServiceImpl implements IdRepoService<IdRequestDTO, IdResponseDTO, Uin> {

	private static final String CREATE = "create";

	private static final String READ = "read";

	private static final String MOSIP_KERNEL_UINGEN_URL = "mosip.kernel.uingen.url";

	private static final String UIN = "uin";

	private static final String REQUEST = "request";

	private static final String UPDATE = "update";

	/** The mosip logger. */
	Logger mosipLogger = IdRepoLogger.getLogger(IdRepoServiceImpl.class);

	/** The Constant LANGUAGE. */
	private static final String LANGUAGE = "language";

	/** The Constant DECRYPT_ENTITY. */
	private static final String DECRYPT_ENTITY = "decryptEntity";

	/** The Constant ENCRYPT_IDENTITY. */
	private static final String ENCRYPT_IDENTITY = "encryptIdentity";

	/** The Constant ID_REPO_SERVICE_IMPL. */
	private static final String ID_REPO_SERVICE_IMPL = "IdRepoServiceImpl";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

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

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/** The rest template. */
	@Autowired
	private RestTemplate restTemplate;

	/** The shard resolver. */
	@Autowired
	private ShardResolver shardResolver;

	/** The uin repo. */
	@Autowired
	private UinRepo uinRepo;

	/** The uin detail repo. */
	@Autowired
	private UinDetailRepo uinDetailRepo;

	/** The uin history repo. */
	@Autowired
	private UinHistoryRepo uinHistoryRepo;

	/** The uin detail history repo. */
	@Autowired
	private UinDetailHistoryRepo uinDetailHistoryRepo;

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The encryptor. */
	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/** The decryptor. */
	@Autowired
	private Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idrepo.spi.IdRepoService#addIdentity(java.lang.Object)
	 */
	@Override
	public IdResponseDTO addIdentity(IdRequestDTO request) throws IdRepoAppException {
		try {
			String uin = generateUIN();
			ShardDataSourceResolver.setCurrentShard(shardResolver.getShard(uin));
			return constructIdResponse(this.id.get(CREATE), addIdentity(uin, request.getRegistrationId(),
					encryptIdentity(convertToBytes(request.getRequest()))));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e, this.id.get(CREATE));
		}
	}

	/**
	 * Adds the identity to DB.
	 *
	 * @param uin
	 *            the uin
	 * @param uinRefId
	 *            the uin ref id
	 * @param identityInfo
	 *            the identity info
	 * @return the uin
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Transactional
	public Uin addIdentity(String uin, String uinRefId, byte[] identityInfo) throws IdRepoAppException {
		try {
			if (!uinRepo.existsById(uinRefId)) {
				uinHistoryRepo
						.save(new UinHistory(uinRefId, now(), uin, env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED),
								CREATED_BY, now(), UPDATED_BY, now(), false, now()));
				uinDetailHistoryRepo.save(new UinDetailHistory(uinRefId, now(), identityInfo, CREATED_BY, now(),
						UPDATED_BY, now(), false, now()));
				return uinRepo.save(new Uin(uinRefId, uin, env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED), CREATED_BY,
						now(), UPDATED_BY, now(), false, now(), uinDetailRepo.save(new UinDetail(uinRefId, identityInfo,
								CREATED_BY, now(), UPDATED_BY, now(), false, now()))));
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.RECORD_EXISTS);
			}
		} catch (DataAccessException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		}
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

			byte[] decryptedIdentity = decryptIdentity(dbUinData.getUinDetail().getUinData());
			if (!Objects.equals(mapper.writeValueAsString(request.getRequest()), new String(decryptedIdentity))) {
				Map<String, Map<String, List<Map<String, String>>>> requestData = convertToMap(request.getRequest());
				Map<String, Map<String, List<Map<String, String>>>> dbData = (Map<String, Map<String, List<Map<String, String>>>>) convertToObject(
						decryptedIdentity, Map.class);
				MapDifference<String, List<Map<String, String>>> mapDifference = Maps
						.difference(requestData.get(IDENTITY), dbData.get(IDENTITY));
				mapDifference.entriesOnlyOnLeft().forEach((key, value) -> dbData.get(IDENTITY).put(key, value));
				mapDifference.entriesDiffering()
						.forEach((String key, ValueDifference<List<Map<String, String>>> value) -> dbData.get(IDENTITY)
								.put(key, findDifference(value.leftValue(), value.rightValue())));

				uinObject = updateIdentityInfo(dbUinData, encryptIdentity(convertToBytes(dbData)));
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
			uinHistoryRepo.save(new UinHistory(uin.getUinRefId(), now(), uin.getUin(), statusCode, CREATED_BY, now(),
					UPDATED_BY, now(), false, now()));
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
			UinDetail uinDetail = uin.getUinDetail();
			uinDetail.setUinData(identityInfo);
			uinDetailRepo.save(uinDetail);
			uinDetailHistoryRepo.save(new UinDetailHistory(uin.getUinRefId(), now(), identityInfo, CREATED_BY, now(),
					UPDATED_BY, now(), false, now()));
			return uinRepo.getOne(uin.getUinRefId());
		} catch (DataAccessException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e);
		}
	}

	/**
	 * Generate UIN.
	 *
	 * @return the string
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private String generateUIN() throws IdRepoAppException {
		try {
			ObjectNode body = restTemplate
					.exchange(env.getProperty(MOSIP_KERNEL_UINGEN_URL), HttpMethod.GET, null, ObjectNode.class)
					.getBody();
			if (Objects.nonNull(body) && body.has(UIN) && Objects.nonNull(body.get(UIN))) {
				return body.get(UIN).textValue();
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.UIN_GENERATION_FAILED);
			}
		} catch (IdRepoAppException | RestClientException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UIN_GENERATION_FAILED, e);
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

		idResponse.setTimestamp(DateUtils.getUTCCurrentDateTimeString());

		idResponse.setRegistrationId(uin.getUinRefId());

		idResponse.setStatus(uin.getStatusCode());

		ResponseDTO response = new ResponseDTO();

		try {
			if (id.equals(this.id.get(CREATE)) || id.equals(this.id.get(UPDATE))) {
				response.setEntity(linkTo(methodOn(IdRepoController.class).retrieveIdentity(uin.getUin().trim()))
						.toUri().toString());
				mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
						SimpleBeanPropertyFilter.serializeAllExcept(IDENTITY, "err")));
			} else {
				response.setIdentity(convertToObject(decryptIdentity(uin.getUinDetail().getUinData()), Object.class));
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
	 * Encrypt identity.
	 *
	 * @param identity
	 *            the identity
	 * @return the byte[]
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private byte[] encryptIdentity(byte[] identity) throws IdRepoAppException {
		try {
			// Generate SessionKey (AES 256)
			SecretKey sessionKey = keyGenerator.getSymmetricKey();

			// Encrypt data with session key
			byte[] encryptedData = encryptor.symmetricEncrypt(sessionKey, identity);
			mosipLogger.info(SESSION_ID, ID_REPO_SERVICE_IMPL, ENCRYPT_IDENTITY,
					"encryptedData - \n" + Base64.getEncoder().encodeToString(encryptedData) + "\n");

			// hash data using HMAC SHA256
			byte[] hash = HMACUtils.generateHash(Base64.getEncoder().encodeToString(encryptedData).getBytes());

			mosipLogger.info(SESSION_ID, ID_REPO_SERVICE_IMPL, ENCRYPT_IDENTITY,
					"hash - \n" + Base64.getEncoder().encodeToString(hash) + "\n");

			// Encrypt session Key using public Key
			byte[] encryptedsessionKey = encryptor.asymmetricPublicEncrypt(
					KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(getKey("publicKey")))),
					sessionKey.getEncoded());
			mosipLogger.info(SESSION_ID, ID_REPO_SERVICE_IMPL, ENCRYPT_IDENTITY,
					"encryptedsessionKey - \n" + Base64.getEncoder().encodeToString(encryptedsessionKey) + "\n");

			// Append Hash | Encrypted session key | Encrypted Data
			StringBuilder builder = new StringBuilder(Base64.getEncoder().encodeToString(hash));
			builder.append('|');
			builder.append(Base64.getEncoder().encodeToString(encryptedsessionKey));
			builder.append('|');
			builder.append(Base64.getEncoder().encodeToString(encryptedData));
			return builder.toString().getBytes();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | java.security.NoSuchAlgorithmException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Decrypt identity.
	 *
	 * @param identity
	 *            the identity
	 * @return the byte[]
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private byte[] decryptIdentity(byte[] identity) throws IdRepoAppException {

		try {
			// Extract Hash & Encrypted session key & Encrypted Data
			String[] encryptedIdentity = new String(identity).split("\\|");
			String hash = encryptedIdentity[0];
			String encryptedSessionKey = encryptedIdentity[1];
			String encryptedData = encryptedIdentity[2];

			// HMAC Decrypted Data record
			// Compare HMAC
			mosipLogger.info(SESSION_ID, ID_REPO_SERVICE_IMPL, DECRYPT_ENTITY, "HASH - \n" + hash + "\n");
			if (hash.equals(Base64.getEncoder().encodeToString(HMACUtils.generateHash(encryptedData.getBytes())))) {

				// Decrypt session Key with private Key
				byte[] sessionKey = decryptor
						.asymmetricPrivateDecrypt(
								KeyFactory
										.getInstance(
												env.getProperty("mosip.kernel.keygenerator.asymmetric-algorithm-name"))
										.generatePrivate(new PKCS8EncodedKeySpec(CryptoUtil.decodeBase64(getKey("privateKey")))),
								Base64.getDecoder().decode(encryptedSessionKey));
				mosipLogger.info(SESSION_ID, ID_REPO_SERVICE_IMPL, DECRYPT_ENTITY,
						"sessionKey - \n" + Base64.getEncoder().encodeToString(sessionKey) + "\n");

				// Decrypt data with with decrypted session key
				byte[] decryptedData = decryptor.symmetricDecrypt(
						new SecretKeySpec(sessionKey, 0, sessionKey.length, "AES"),
						Base64.getDecoder().decode(encryptedData));
				mosipLogger.info(SESSION_ID, ID_REPO_SERVICE_IMPL, DECRYPT_ENTITY,
						"decryptedData - \n" + new String(decryptedData) + "\n");

				return decryptedData;
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST);
			}
		} catch (IdRepoAppException | InvalidKeySpecException | java.security.NoSuchAlgorithmException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * Gets the key.
	 *
	 * @param keyType
	 *            the key type
	 * @return the key
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	public String getKey(String keyType) throws IdRepoAppException {
        if (keyType == "publicKey") {
               return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArYVA3ldCArKvG945Dbk85XweDiAkZGazPBc0Z9jIiW2PSqbFz-z02KeC1lVNF1Yaf-A-ZdrgAQNpLRX04NBvE9gquHCpOk6GdP1n0UFVTcxD6-TWrF3XqWZ4f2UtaflWWqPKRnrkjUMKXFAyU35qe7zrTzNQaX43m8Kxacf9twxQNkShkXuJaV0kpQcvnOBsNwi8iwYtECB5DVGzuIiC7Gqkm-Q-ceGbFrTV_j8CF7IJZvDAmfTn08tSsnb5UokC1tg194Z1t8Px3TM_eV5_EVAee16bOc8XLhJgKnj0PC1FKUxpPhtol1vOLWr2bnJw3HCv82yKvRG0joh0uOEaWwIDAQAB";
        } else {
               return "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCthUDeV0ICsq8b3jkNuTzlfB4OICRkZrM8FzRn2MiJbY9KpsXP7PTYp4LWVU0XVhp_4D5l2uABA2ktFfTg0G8T2Cq4cKk6ToZ0_WfRQVVNzEPr5NasXdepZnh_ZS1p-VZao8pGeuSNQwpcUDJTfmp7vOtPM1BpfjebwrFpx_23DFA2RKGRe4lpXSSlBy-c4Gw3CLyLBi0QIHkNUbO4iILsaqSb5D5x4ZsWtNX-PwIXsglm8MCZ9OfTy1KydvlSiQLW2DX3hnW3w_HdMz95Xn8RUB57Xps5zxcuEmAqePQ8LUUpTGk-G2iXW84tavZucnDccK_zbIq9EbSOiHS44RpbAgMBAAECggEAAxu9-72oaw_0XxVuBDbrRlkg2LPusvW8BDmOMMYLYZra1EWEiaL_B9qIV3qTtPVP6DaGJ6DyHlKHUc1w444YT0yyBzfNcEPz2KipcXAwLQKZvOIOjmvn7xrs0atoUtuqllkUyIpvCbDHwLuXCzPaiHjr5jFPIXSnXlzwfKqeeubv_Lsf6JjfrKC3Vm8Rzm3ZmgCBO_r3o7uBMZbG5JCDvwmYGjEV0IIt29wzpIvQimnWeN7ospXoalw4XynwzmzpqEHmMrVnS_5f-b60UBtppNNIVkM9I3YZZotYiIN3HhbnpUFXr9s8H_0-Ij_P67l8rjeFnb_P8k9vYkBh7jn94QKBgQDll_NWPkMYGygDaAURCzrFmIPk9NE6giuT6WfSezowUnUp_pkDM-UwqfYXv2TjpPwnQP4I0wGSdb3TdBpUIYVZhJejl6SXHeK4PFctAxPYL9j1qS5EGZ4MW0BHY_TwaXYkuT0ehZzAHd3boiqI8vLyfcLyS6phrBpRmhLufO1K6QKBgQDBelBIVTsABinMYY2fxL9gAC-Hq_f5YTkpzuCdyP4CdCkg0j214Rg_PaGhvCK2mlCC66S_TSRlcfm2VBl9bNLMZDrNjcLndHIFtFYsBBbO85SP1_NqKYQnB90zaMBHhKVNwkUqL8ocNud17PQR_8UvD7KTEyrlAWaprI0I76YoowKBgFTJOnmc4JYkYTBw134l0XxrCDojFqkurP0gctDN5P03VkE93i_KmFbSTSkT6yQOJ3gvjiCgG7KEfKd79sUe4-ndB0rIk2WjCbjTv7XFryxk7xA5e_Z2J2GUyTYT-b5Wk-SX67Q7z1k7LlUPm_mbDpDyVG2595--OLHt3hPoWpERAoGAKKSQDtHQpg2lZCit_nzvErSXR3HJkppozq6For-hFFTgeuOPURCq7kX9GSOTSzaZaRclMYZ7-c96fGGoRR_CHGuFqr53zfU5Kpeabn6hZ_HlKF5d5NQ5zZUqU21SzvM_YllH_Dezxj-GWdBf6RQIkP1ELv8cyKSFs2jq65CkH-8CgYBbL-Oi9Alz6w4jgNCRjuWRr35VAlowRGxjn2U_b2jhmqjnu2LiIU15BnCopETclOxzNgwxca7A4gSIzJTGIOJGqxWyUDtHcHIuusNd4T21b6lzHDoOS0adVTRlX-K-_2df_C8jx7pem2k9qLy0FgUJ4NjDraZIt4WIS_eXjItVCw";
        }
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

}
