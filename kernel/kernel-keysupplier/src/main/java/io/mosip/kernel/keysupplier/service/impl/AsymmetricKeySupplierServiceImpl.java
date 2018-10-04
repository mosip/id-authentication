package io.mosip.kernel.keysupplier.service.impl;

import org.springframework.stereotype.Service;

import io.mosip.kernel.keysupplier.dto.AsymmetricResponceDto;
import io.mosip.kernel.keysupplier.service.AsymmetricKeySupplierService;

/**
 * @author Urvil Joshi
 *
 * @Since 1.0.0
 */
@Service
public class AsymmetricKeySupplierServiceImpl implements AsymmetricKeySupplierService {

	@Override
	public AsymmetricResponceDto getAsymmetricKeys(int applicationId, int keyType, String token) {
		
		return null;
	}

}
