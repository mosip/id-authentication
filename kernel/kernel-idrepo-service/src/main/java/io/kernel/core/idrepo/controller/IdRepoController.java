package io.kernel.core.idrepo.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.exception.IdRepoAppException;
import io.kernel.core.idrepo.exception.IdRepoDataValidationException;
import io.kernel.core.idrepo.service.IdRepoService;
import io.kernel.core.idrepo.util.DataValidationUtil;
import io.kernel.core.idrepo.validator.IdRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import springfox.documentation.annotations.ApiIgnore;

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

    @Autowired
    private UinValidatorImpl uinValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
	binder.addValidators(validator);
    }

    @PostMapping(path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdResponseDTO> addIdentity(@Validated @RequestBody IdRequestDTO request, @ApiIgnore Errors errors)
	    throws IdRepoAppException {
	try {
	    DataValidationUtil.validate(errors);
	    return new ResponseEntity<>(idRepoService.addIdentity(request), HttpStatus.CREATED);
	} catch (IdRepoDataValidationException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.DATA_VALIDATION_FAILED, e);
	}
    }

    @GetMapping(path = "/identity/{uin}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdResponseDTO> retrieveEntity(@PathVariable String uin) throws IdRepoAppException {
	try {
	    if (!Objects.isNull(uin) && uinValidator.validateId(uin)) {
		return new ResponseEntity<>(idRepoService.retrieveIdentity(uin), HttpStatus.OK);
	    } else {
		throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN);
	    }
	} catch (InvalidIDException | IdRepoAppException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, e, "mosip.id.read");
	}
    }

    @PatchMapping(path = "/identity/{uin}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdResponseDTO> updateEntity(@Validated @RequestBody IdRequestDTO request, @ApiIgnore Errors errors) throws IdRepoAppException {
	try {
	    DataValidationUtil.validate(errors);
	    return new ResponseEntity<>(idRepoService.updateIdentity(request), HttpStatus.OK);
	} catch (IdRepoDataValidationException e) {
	    throw new IdRepoAppException(IdRepoErrorConstants.DATA_VALIDATION_FAILED, e, "mosip.id.update");
	}
    }
}
