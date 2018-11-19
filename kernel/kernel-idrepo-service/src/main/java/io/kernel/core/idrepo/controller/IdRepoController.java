package io.kernel.core.idrepo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.service.IdRepoService;
import io.kernel.core.idrepo.validator.IdRequestValidator;

/**
 * @author Manoj SP
 *
 */
@RestController
public class IdRepoController {

    @Autowired
    private IdRepoService idRepoService;

    @Autowired
    private IdRequestValidator validator;

    @PostMapping(path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public IdResponseDTO addIdentity(@Valid @RequestBody IdRequestDTO request) {
	return idRepoService.addIdentity(request);

    }

    @GetMapping(path = "/identity/uin/{uin}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public IdResponseDTO retrieveEntity(@PathVariable String uin) {
	return idRepoService.retrieveIdentity(uin);
    }

    @PatchMapping(path = "/identity/uin/{uin}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public IdResponseDTO updateEntity(@Valid @RequestBody IdRequestDTO request) {
	return idRepoService.updateIdentity(request);
    }
}
