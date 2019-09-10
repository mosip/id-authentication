package io.mosip.kernel.idobjectvalidator.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;

/**
 * The Class IdObjectCompositeValidator.
 *
 * @author Manoj SP
 */
@Component
@Primary
public class IdObjectCompositeValidator implements IdObjectValidator {

	@Autowired
	private IdObjectSchemaValidator schemaValidator;

	@Autowired
	private IdObjectPatternValidator patternValidator;

	@Autowired(required = false)
	@Qualifier("referenceValidator")
	@Lazy
	private IdObjectValidator referenceValidator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator#validateIdObject
	 * (java.lang.Object)
	 */
	@Override
	public boolean validateIdObject(Object identityObject, IdObjectValidatorSupportedOperations operation)
			throws IdObjectValidationFailedException, IdObjectIOException {
		schemaValidator.validateIdObject(identityObject, operation);
		patternValidator.validateIdObject(identityObject, operation);

		if (Objects.nonNull(referenceValidator)) {
			referenceValidator.validateIdObject(identityObject, operation);
		}
		
		return true;
	}

}
