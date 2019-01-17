package io.mosip.kernel.idrepo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoDataValidationException;
import io.mosip.kernel.core.idrepo.spi.IdRepoService;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.dto.IdResponseDTO;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.util.DataValidationUtil;
import io.mosip.kernel.idrepo.validator.IdRequestValidator;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class IdRepoController.
 *
 * @author Manoj SP
 */
@RestController
public class IdRepoController {

	private static final String ALL = "all";

	private static final String UPDATE = "update";

	private static final String READ = "read";

	private static final String TYPE = "type";

	private static final String CREATE = "create";

	/** The id. */
	@Resource
	private Map<String, String> id;

	@Resource
	private List<String> allowedTypes;

	/** The id repo service. */
	@Autowired
	private IdRepoService<IdRequestDTO, IdResponseDTO, Uin> idRepoService;

	/** The validator. */
	@Autowired
	private IdRequestValidator validator;

	/** The uin validator. */
	@Autowired
	private IdValidator<String> uinValidatorImpl;

	/**
	 * Inits the binder.
	 *
	 * @param binder
	 *            the binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}

	/**
	 * Adds the identity.
	 *
	 * @param request
	 *            the request
	 * @param errors
	 *            the errors
	 * @return the response entity
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@PostMapping(path = "/v1.0/identity/{uin}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IdResponseDTO> addIdentity(@PathVariable String uin,
			@Validated @RequestBody IdRequestDTO request, @ApiIgnore Errors errors) throws IdRepoAppException {
		try {
			uinValidatorImpl.validateId(uin);
			DataValidationUtil.validate(errors);
			return new ResponseEntity<>(idRepoService.addIdentity(request, uin), HttpStatus.CREATED);
		} catch (InvalidIDException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, e, id.get(CREATE));
		} catch (IdRepoDataValidationException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATA_VALIDATION_FAILED, e, id.get(CREATE));
		}
	}

	/**
	 * Retrieve identity.
	 *
	 * @param uin
	 *            the uin
	 * @param filter
	 *            the filter
	 * @return the response entity
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@GetMapping(path = "/v1.0/identity/{uin}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IdResponseDTO> retrieveIdentity(@PathVariable String uin,
			@RequestParam(name = TYPE, required = false) String type, HttpServletRequest request)
			throws IdRepoAppException {
		if (request.getParameterMap().size() > 1
				|| (request.getParameterMap().size() == 1 && !request.getParameterMap().containsKey(TYPE))) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_REQUEST, id.get(READ));
		}

		try {
			if (Objects.nonNull(type)) {
				List<String> typeList = Arrays.asList(StringUtils.split(type, ','));
				if (typeList.size() == 1 && !allowedTypes.containsAll(typeList)) {
					throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), TYPE));
				} else {
					if (typeList.parallelStream().filter(typeValue -> !typeValue.equals(ALL))
							.allMatch(allowedTypes::contains)) {
						type = ALL;
					} else {
						throw new IdRepoAppException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
								String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), TYPE));
					}
				}
			}

			uinValidatorImpl.validateId(uin);
			return new ResponseEntity<>(idRepoService.retrieveIdentity(uin, type), HttpStatus.OK);
		} catch (InvalidIDException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, e, id.get(READ));
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(e.getErrorCode(), e.getErrorText(), e, id.get(READ));
		}
	}

	/**
	 * Update identity.
	 *
	 * @param uin
	 *            the uin
	 * @param request
	 *            the request
	 * @param errors
	 *            the errors
	 * @return the response entity
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@PatchMapping(path = "/v1.0/identity/{uin}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<IdResponseDTO> updateIdentity(@PathVariable String uin, @RequestBody IdRequestDTO request,
			@ApiIgnore Errors errors) throws IdRepoAppException {
		try {
			validator.validate(request, errors);
			DataValidationUtil.validate(errors);
			return new ResponseEntity<>(idRepoService.updateIdentity(request, uin), HttpStatus.OK);
		} catch (IdRepoDataValidationException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.DATA_VALIDATION_FAILED, e, id.get(UPDATE));
		}
	}
}
