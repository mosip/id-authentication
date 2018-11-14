package io.mosip.kernel.keymanager.service.impl;

import org.springframework.stereotype.Service;

import io.mosip.kernel.keymanager.dto.KeymanagerResponseDto;
import io.mosip.kernel.keymanager.service.KeymanagerService;

/**
 * This class provides the implementation for the methods of KeymanagerService
 * interface.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class KeymanagerServiceImpl implements KeymanagerService {
	/**
	 * The reference that autowires KeymanagerRepository class.
	 */
//	@Autowired
//	private KeymanagerRepository keymanagerRepository;


	public KeymanagerResponseDto getPublicKey() {
		return null;
	}
}
