package io.kernel.idrepo.service.impl;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Maps;

import io.kernel.idrepo.controller.IdRepoController;
import io.kernel.idrepo.dto.IdRequestDTO;
import io.kernel.idrepo.dto.IdResponseDTO;
import io.kernel.idrepo.dto.ResponseDTO;
import io.kernel.idrepo.entity.Uin;
import io.kernel.idrepo.entity.UinDetail;
import io.kernel.idrepo.entity.UinDetailHistory;
import io.kernel.idrepo.entity.UinHistory;
import io.kernel.idrepo.repository.UinDetailHistoryRepo;
import io.kernel.idrepo.repository.UinDetailRepo;
import io.kernel.idrepo.repository.UinHistoryRepo;
import io.kernel.idrepo.repository.UinRepo;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.spi.IdRepoService;
import io.mosip.kernel.core.idrepo.spi.ShardDataSourceResolver;
import io.mosip.kernel.core.idrepo.spi.ShardResolver;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class IdRepoServiceImpl.
 *
 * @author Manoj SP
 */
@Service
public class IdRepoServiceImpl implements IdRepoService<IdRequestDTO, IdResponseDTO, Uin> {

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant MOSIP_ID_READ. */
	private static final String MOSIP_ID_READ = "mosip.id.read";

	/** The Constant MOSIP_ID_CREATE. */
	private static final String MOSIP_ID_CREATE = "mosip.id.create";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "createdBy";

	/** The Constant MOSIP_IDREPO_STATUS_REGISTERED. */
	private static final String MOSIP_IDREPO_STATUS_REGISTERED = "mosip.idrepo.status.registered";

	/** The Constant UPDATED_BY. */
	private static final String UPDATED_BY = "updatedBy";

	/** The Constant MOSIP_ID_UPDATE. */
	private static final String MOSIP_ID_UPDATE = "mosip.id.update";

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
			ShardDataSourceResolver.setCurrentShard(shardResolver.getShrad(uin));
			return constructIdResponse(MOSIP_ID_CREATE,
					addIdentity(uin, request.getRegistrationId(), convertToBytes(request.getRequest())));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATABASE_ACCESS_ERROR, e, MOSIP_ID_CREATE);
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
			uinHistoryRepo
					.save(new UinHistory(uinRefId, now(), uinRefId, env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED),
							CREATED_BY, now(), UPDATED_BY, now(), false, now()));
			uinDetailHistoryRepo.save(new UinDetailHistory(uinRefId, now(), identityInfo, CREATED_BY, now(), UPDATED_BY,
					now(), false, now()));
			return uinRepo.save(new Uin(uinRefId, uin, env.getProperty(MOSIP_IDREPO_STATUS_REGISTERED), CREATED_BY,
					now(), UPDATED_BY, now(), false, now(), uinDetailRepo.save(new UinDetail(uinRefId, identityInfo,
							CREATED_BY, now(), UPDATED_BY, now(), false, now()))));
		} catch (DataIntegrityViolationException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.RECORD_EXISTS, e);
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
			checkUIN(uin);
			return constructIdResponse(MOSIP_ID_READ, retrieveIdentityByUin(uin));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, e, MOSIP_ID_READ);
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
		checkUIN(request.getUin());
		Uin dbUinData = retrieveIdentityByUin(request.getUin());
		Uin uinObject = null;
		if (!request.getStatus().equals(dbUinData.getStatusCode())) {
			uinObject = updateUinStatus(dbUinData, request.getStatus());
		}

		if (!request.getRequest().toString().equals(new String(dbUinData.getUinDetail().getUinData()))) {
			Map<String, Object> requestData = convertToMap(request.getRequest());
			Map<String, Object> dbData = (Map<String, Object>) convertToObject(dbUinData.getUinDetail().getUinData(),
					Map.class);
			Maps.difference(requestData, dbData).entriesDiffering()
					.forEach((key, value) -> dbData.put(key, value.leftValue()));
			uinObject = updateIdenityInfo(dbUinData, convertToBytes(dbData));
		}

		return constructIdResponse(MOSIP_ID_UPDATE, uinObject);
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
	public Uin updateIdenityInfo(Uin uin, byte[] identityInfo) throws IdRepoAppException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idrepo.spi.IdRepoService#generateUIN()
	 */
	@Override
	public String generateUIN() throws IdRepoAppException {
		try {
			ObjectNode body = restTemplate
					.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, ObjectNode.class).getBody();
			if (body.has("uin")) {
				return body.get("uin").textValue();
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.UIN_GENERATION_FAILED);
			}
		} catch (IdRepoAppException | RestClientException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UIN_GENERATION_FAILED, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idrepo.spi.IdRepoService#checkUIN(java.lang.String)
	 */
	@Override
	public void checkUIN(String uin) throws IdRepoAppException {
		ShardDataSourceResolver.setCurrentShard(shardResolver.getShrad(uin));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idrepo.spi.IdRepoService#constructIdResponse(java.lang.
	 * String, java.lang.Object)
	 */
	@Override
	public IdResponseDTO constructIdResponse(String id, Uin uin) throws IdRepoAppException {
		IdResponseDTO idResponse = new IdResponseDTO();

		idResponse.setId(id);

		idResponse.setVer(env.getProperty("mosip.idrepo.version"));

		idResponse.setTimestamp(
				DateUtils.formatDate(now(), env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone("GMT")));

		idResponse.setRegistrationId(uin.getUinRefId());

		idResponse.setStatus(uin.getStatusCode());

		ResponseDTO response = new ResponseDTO();

		try {
			if (id.equals(this.id.get("create")) || id.equals(this.id.get("update"))) {
				response.setEntity(linkTo(methodOn(IdRepoController.class).retrieveIdentity(uin.getUin().trim()))
						.toUri().toString());
				mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
						SimpleBeanPropertyFilter.serializeAllExcept("identity", "err")));
			} else {
				response.setIdentity(convertToObject(uin.getUinDetail().getUinData(), Object.class));
				mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
						SimpleBeanPropertyFilter.serializeAllExcept("entity", "err")));
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
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
	@SuppressWarnings("unchecked")
	private Map<String, Object> convertToMap(Object identity) throws IdRepoAppException {
		try {
			return mapper.readValue(mapper.writeValueAsBytes(identity), Map.class);
		} catch (IOException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "request"), e);
		}
	}

	/**
	 * Get the current time.
	 *
	 * @return the date
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	private Date now() throws IdRepoAppException {
		try {
			return DateUtils.parseToDate(
					DateUtils.formatDate(new Date(), env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone("GMT")),
					env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone("GMT"));
		} catch (ParseException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INTERNAL_SERVER_ERROR, e);
		}
	}

}
