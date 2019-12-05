package io.mosip.kernel.pridgenerator.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.pridgenerator.entity.PridEntity;
import io.mosip.kernel.pridgenerator.service.PridService;

/**
 * This class have functionality to persists the list of prids in database
 * 
 * @author Ajay J
 * @since 1.0.0
 *
 */
@Component
public class PridWriter {

	@Autowired
	private PridService pridService;

	public boolean persistPrids(PridEntity prid) {
			return this.pridService.savePRID(prid);
	}
}