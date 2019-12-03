package io.mosip.kernel.pridgenerator.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.pridgenerator.entity.PridEntity;
import io.mosip.kernel.pridgenerator.service.PridService;

/**
 * This class have functionality to persists the list of vids in database
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Component
public class PridWriter {

	@Autowired
	private PridService pridService;

	public boolean persistPrids(PridEntity vid) {
			return this.pridService.savePRID(vid);
	}
}