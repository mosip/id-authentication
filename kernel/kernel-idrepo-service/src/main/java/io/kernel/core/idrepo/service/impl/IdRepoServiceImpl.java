package io.kernel.core.idrepo.service.impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import io.kernel.core.idrepo.controller.IdRepoController;
import io.kernel.core.idrepo.dao.IdRepoDao;
import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.dto.ResponseDTO;
import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.service.IdRepoService;

/**
 * @author Manoj SP
 *
 */
@Service
public class IdRepoServiceImpl implements IdRepoService {

	@Autowired
	private IdRepoDao idRepo;

	@Override
	public IdResponseDTO addIdentity(IdRequestDTO request) {
		return constructIdResponse("mosip.id.create",
				idRepo.addIdentity(request.getUin(), request.getRegistrationId(), request.getRequest()));
	}

	@Override
	public IdResponseDTO retrieveIdentity(String uin) {
		return constructIdResponse("mosip.id.read", idRepo.retrieveIdentity(uin));
	}

	@Override
	public IdResponseDTO updateIdentity(IdRequestDTO request) {
		Uin uinObject = null;

		IdResponseDTO dbUinData = retrieveIdentity(request.getUin());

		if (!request.getStatus().equals(dbUinData.getStatus())) {
			uinObject = idRepo.updateUinStatus(request.getUin(), request.getStatus());
		}

		if (!request.getRequest().equals(dbUinData.getResponse().getIdentity())) {
			uinObject = idRepo.updateIdenityInfo(request.getUin(), request.getRequest());
		}

		return constructIdResponse("mosip.id.update", uinObject);
	}

	@Override
	public String generateUIN() {
		return null;
	}

	@Override
	public Boolean checkUIN() {
		return false;
	}

	// FIXME add boolean for response obj
	@Override
	public IdResponseDTO constructIdResponse(String id, Uin uin) {
		IdResponseDTO idResponse = new IdResponseDTO();

		idResponse.setId(id);
		idResponse.setVer("1.0");

		// TODO set timestamp
		idResponse.setTimestamp(Instant.now().toString());

		idResponse.setRegistrationId(uin.getUinRefId());

		idResponse.setStatus("status");

		ResponseDTO response = new ResponseDTO();

		response.setEntity(linkTo(methodOn(IdRepoController.class).retrieveEntity(uin.getUin())).toUri().toString());

		// FIXME set proper format
		response.setIdentity(new String(uin.getUinDetail().getUinData()));

		idResponse.setResponse(response);
		return idResponse;
	}
}
