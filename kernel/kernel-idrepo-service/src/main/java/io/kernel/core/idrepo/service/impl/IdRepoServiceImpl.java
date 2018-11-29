package io.kernel.core.idrepo.service.impl;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Maps;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.kernel.core.idrepo.controller.IdRepoController;
import io.kernel.core.idrepo.dao.IdRepoDao;
import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.dto.ResponseDTO;
import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.exception.IdRepoAppException;
import io.kernel.core.idrepo.service.IdRepoService;

/**
 * The Class IdRepoServiceImpl.
 *
 * @author Manoj SP
 */
@Service
public class IdRepoServiceImpl implements IdRepoService {

	/** The Constant MOSIP_ID_UPDATE. */
	private static final String MOSIP_ID_UPDATE = "mosip.id.update";

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	private Environment env;

	/** The rest template. */
	@Autowired
	private RestTemplate restTemplate;

	/** The id. */
	@Resource
	private Map<String, String> id;

	/** The id repo. */
	@Autowired
	private IdRepoDao idRepo;

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.service.IdRepoService#addIdentity(io.kernel.core.idrepo.dto.IdRequestDTO)
	 */
	@Override
	public IdResponseDTO addIdentity(IdRequestDTO request) throws IdRepoAppException {
		try {
			return constructIdResponse("mosip.id.create", idRepo.addIdentity(generateUIN(), request.getRegistrationId(),
					convertToBytes(request.getRequest())));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e, "mosip.id.create");
		}
	}

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.service.IdRepoService#retrieveIdentity(java.lang.String)
	 */
	@Override
	public IdResponseDTO retrieveIdentity(String uin) throws IdRepoAppException {
		try {
			if (checkUIN(uin)) {
				return constructIdResponse("mosip.id.read", idRepo.retrieveIdentity(uin));
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN);
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e, "mosip.id.read");
		}
	}

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.service.IdRepoService#updateIdentity(io.kernel.core.idrepo.dto.IdRequestDTO)
	 */
	@Override
	public IdResponseDTO updateIdentity(IdRequestDTO request) throws IdRepoAppException {
		try {
			if (checkUIN(request.getUin())) {
				Uin uinObject = null;

				IdResponseDTO dbUinData = retrieveIdentity(request.getUin());

				if (!request.getStatus().equals(dbUinData.getStatus())) {
					uinObject = idRepo.updateUinStatus(request.getUin(), request.getStatus());
				}

				if (!request.getRequest().toString().equals(dbUinData.getResponse().getIdentity())) {
					Map<String, Object> requestData = convertToMap(request.getRequest());
					Map<String, Object> dbData = convertToMap(dbUinData.getResponse().getIdentity());
					Maps.difference(requestData, dbData).entriesDiffering()
							.forEach((key, value) -> dbData.put(key, value.leftValue()));
					uinObject = idRepo.updateIdenityInfo(request.getUin(), convertToBytes(dbData));
				}

				return constructIdResponse(MOSIP_ID_UPDATE, uinObject);
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, MOSIP_ID_UPDATE);
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e, MOSIP_ID_UPDATE);
		}
	}

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.service.IdRepoService#generateUIN()
	 */
	@Override
	public String generateUIN() throws IdRepoAppException {
		try {
			ObjectNode body = restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, ObjectNode.class)
					.getBody();
			if (body.has("uin")) {
				return body.get("uin").textValue();
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.UIN_GENERATION_FAILED);
			}
		} catch (IdRepoAppException | RestClientException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UIN_GENERATION_FAILED, e);
		}
	}

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.service.IdRepoService#checkUIN(java.lang.String)
	 */
	@Override
	public Boolean checkUIN(String uin) throws IdRepoAppException {
		try {
			Uin uinObject = idRepo.retrieveIdentity(uin);
			if (uinObject == null) {
				throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN);
			} else if (uinObject.getStatusCode().equals(env.getProperty("mosip.idrepo.status.registered"))) {
				return true;
			} else {
				throw new IdRepoAppException(IdRepoErrorConstants.NON_REGISTERED_UIN.getErrorCode(), String
						.format(IdRepoErrorConstants.NON_REGISTERED_UIN.getErrorMessage(), uinObject.getStatusCode()));
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}
	}

	/* (non-Javadoc)
	 * @see io.kernel.core.idrepo.service.IdRepoService#constructIdResponse(java.lang.String, io.kernel.core.idrepo.entity.Uin)
	 */
	@Override
	public IdResponseDTO constructIdResponse(String id, Uin uin) throws IdRepoAppException {
		IdResponseDTO idResponse = new IdResponseDTO();

		idResponse.setId(id);

		idResponse.setVer(env.getProperty("mosip.idrepo.version"));

		idResponse.setTimestamp(mapper.convertValue(new Date(), String.class));

		idResponse.setRegistrationId(uin.getUinRefId());

		idResponse.setStatus(uin.getStatusCode());

		ResponseDTO response = new ResponseDTO();

		try {
			if (id.equals(this.id.get("create")) || id.equals(this.id.get("update"))) {
				response.setEntity(
						linkTo(methodOn(IdRepoController.class).retrieveIdentity(uin.getUin())).toUri().toString());
				mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
						SimpleBeanPropertyFilter.serializeAllExcept("identity")));
			} else {
				response.setIdentity(convertToObject(uin.getUinDetail().getUinData()));
				mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
						SimpleBeanPropertyFilter.serializeAllExcept("entity")));
			}
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}

		idResponse.setResponse(response);

		return idResponse;
	}

	/**
	 * Convert to map.
	 *
	 * @param identity the identity
	 * @return the map
	 * @throws IdRepoAppException the id repo app exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> convertToMap(Object identity) throws IdRepoAppException {
		try {
			return mapper.readValue(mapper.writeValueAsBytes(identity), Map.class);
		} catch (IOException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * Convert to object.
	 *
	 * @param identity the identity
	 * @return the object
	 * @throws IdRepoAppException the id repo app exception
	 */
	private Object convertToObject(byte[] identity) throws IdRepoAppException {
		try {
			return mapper.readValue(identity, Object.class);

		} catch (IOException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * Convert to bytes.
	 *
	 * @param identity the identity
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	private byte[] convertToBytes(Map<String, Object> identity) throws IdRepoAppException {
		try {
			ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
			return objectWriter.writeValueAsString(identity).getBytes();
		} catch (IOException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * Convert to bytes.
	 *
	 * @param identity the identity
	 * @return the byte[]
	 * @throws IdRepoAppException the id repo app exception
	 */
	private byte[] convertToBytes(Object identity) throws IdRepoAppException {
		try {
			return mapper.writeValueAsBytes(identity);
		} catch (JsonProcessingException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}
	}
}
