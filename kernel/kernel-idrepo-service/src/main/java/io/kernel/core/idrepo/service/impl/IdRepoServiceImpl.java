package io.kernel.core.idrepo.service.impl;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import io.kernel.core.idrepo.controller.IdRepoController;
import io.kernel.core.idrepo.dao.IdRepoDao;
import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.dto.ResponseDTO;
import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.service.IdRepoService;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

/**
 * @author Manoj SP
 *
 */
@Service
public class IdRepoServiceImpl implements IdRepoService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UinValidatorImpl uinValidator;

    @Autowired
    private Environment env;

    @Autowired
    private IdRepoDao idRepo;

    @Override
    public IdResponseDTO addIdentity(IdRequestDTO request) {
	if (checkUIN(request.getUin())) {
	    return constructIdResponse("mosip.id.create",
		    idRepo.addIdentity(request.getUin(), request.getRegistrationId(), request.getRequest()));
	} else {
	    return null;
	}
    }

    @Override
    public IdResponseDTO retrieveIdentity(String uin) {
	if (checkUIN(uin)) {
	    return constructIdResponse("mosip.id.read", idRepo.retrieveIdentity(uin));
	} else {
	    return null;
	}
    }

    @Override
    public IdResponseDTO updateIdentity(IdRequestDTO request) {
	if (checkUIN(request.getUin())) {
	    Uin uinObject = null;

	    IdResponseDTO dbUinData = retrieveIdentity(request.getUin());

	    if (!request.getStatus().equals(dbUinData.getStatus())) {
		uinObject = idRepo.updateUinStatus(request.getUin(), request.getStatus());
	    }

	    if (!request.getRequest().toString().equals(dbUinData.getResponse().getIdentity())) {
		uinObject = idRepo.updateIdenityInfo(request.getUin(), request.getRequest());
	    }

	    return constructIdResponse("mosip.id.update", uinObject);
	} else {
	    return null;
	}
    }

    @Override
    public Boolean checkUIN(String uin) {
	try {
	    return uinValidator.validateId(uin);
	} catch (InvalidIDException e) {
	    System.err.println(e.getMessage());
	    return false;
	}
    }

    @Override
    public IdResponseDTO constructIdResponse(String id, Uin uin) {
	IdResponseDTO idResponse = new IdResponseDTO();

	idResponse.setId(id);

	idResponse.setVer(env.getProperty("mosip.idrepo.version"));

	idResponse.setTimestamp(new Date());

	idResponse.setRegistrationId(uin.getUinRefId());

	idResponse.setStatus(uin.getStatusCode());

	ResponseDTO response = new ResponseDTO();

	if (id.equals("mosip.id.create") || id.equals("mosip.id.update")) {
	    response.setEntity(
		    linkTo(methodOn(IdRepoController.class).retrieveEntity(uin.getUin())).toUri().toString());
	    mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
		    SimpleBeanPropertyFilter.serializeAllExcept("identity")));
	} else {
	    // FIXME set proper format
	    response.setIdentity(new String(uin.getUinDetail().getUinData()));
	    mapper.setFilterProvider(new SimpleFilterProvider().addFilter("responseFilter",
		    SimpleBeanPropertyFilter.serializeAllExcept("entity")));
	}

	idResponse.setResponse(response);

	return idResponse;
    }
}
