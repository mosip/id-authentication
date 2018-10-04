package io.mosip.kernel.keysupplier.service;

import org.springframework.stereotype.Service;

import io.mosip.kernel.keysupplier.dto.AsymmetricResponceDto;

/**
 * @author Urvil Joshi
 *
 * @Since 1.0.0
 */
@Service
public interface AsymmetricKeySupplierService {

	AsymmetricResponceDto getAsymmetricKeys(int applicationId, int keyType, String token);

}
