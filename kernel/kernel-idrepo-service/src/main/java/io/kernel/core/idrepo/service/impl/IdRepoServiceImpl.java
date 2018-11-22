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
 * @author Manoj SP
 *
 */
@Service
public class IdRepoServiceImpl implements IdRepoService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private Map<String, String> id;

    @Autowired
    private IdRepoDao idRepo;

    @Override
    public IdResponseDTO addIdentity(IdRequestDTO request) throws IdRepoAppException {
	try {
	    return constructIdResponse("mosip.id.create", idRepo.addIdentity(generateUIN(), request.getRegistrationId(),
		    convertToBytes(request.getRequest())));
	} catch (IdRepoAppException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e, "mosip.id.create");
	}
    }

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

		return constructIdResponse("mosip.id.update", uinObject);
	    } else {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, "mosip.id.update");
	    }
	} catch (IdRepoAppException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e, "mosip.id.update");
	}
    }

    @Override
    public String generateUIN() throws IdRepoAppException {
	try {
	    return restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, String.class)
		    .getBody();
	} catch (RestClientException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
	}
    }

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
			linkTo(methodOn(IdRepoController.class).retrieveEntity(uin.getUin())).toUri().toString());
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object identity) throws IdRepoAppException {
	try {
	    return mapper.readValue(mapper.writeValueAsBytes(identity), Map.class);
	} catch (IOException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
	}
    }

    private Object convertToObject(byte[] identity) throws IdRepoAppException {
	try {
	    return mapper.readValue(identity, Object.class);

	} catch (IOException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
	}
    }

    private byte[] convertToBytes(Map<String, Object> identity) throws IdRepoAppException {
	try {
	    ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
	    return objectWriter.writeValueAsString(identity).getBytes();
	} catch (IOException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
	}
    }

    private byte[] convertToBytes(Object identity) throws IdRepoAppException {
	try {
	    return mapper.writeValueAsBytes(identity);
	} catch (JsonProcessingException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
	}
    }
}
