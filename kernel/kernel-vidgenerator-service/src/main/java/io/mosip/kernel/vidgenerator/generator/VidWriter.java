package io.mosip.kernel.vidgenerator.generator;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.vidgenerator.entity.VidEntity;
import io.mosip.kernel.vidgenerator.repository.VidRepository;

/**
 * This class have functionality to persists the list of vids in database
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Component
public class VidWriter {


	@Autowired
	private VidRepository vidRepository;
	

	public void persistVids(VidEntity vid) {
		this.vidRepository.saveAndFlush(vid);
	}
}