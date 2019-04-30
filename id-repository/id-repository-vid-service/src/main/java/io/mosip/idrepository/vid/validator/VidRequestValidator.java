package io.mosip.idrepository.vid.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.idrepository.vid.repository.VidRepo;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;

/**
 * 
 * @author Prem Kumar
 *
 */
@Component
public class VidRequestValidator implements Validator {

	@Autowired
	private VidRepo vidRepo;

	@Autowired
	Environment env;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(VidRequestDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub

	}

	public void validateId(String vid, Errors errors) throws IdRepoAppException {
		if (vidRepo.existsById(vid)) {
			String vidTypeCode = vidRepo.retrieveVidTypeCode(vid);
			String vidPolicy = env.getProperty("" + vidTypeCode);

		} else {
			// TODO Throw an proper error.
			throw new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
					IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage());
		}

	}

}
