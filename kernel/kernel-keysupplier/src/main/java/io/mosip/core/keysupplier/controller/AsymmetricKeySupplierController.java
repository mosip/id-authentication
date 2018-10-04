package io.mosip.core.keysupplier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.core.keysupplier.dto.AsymmetricResponceDto;
import io.mosip.core.keysupplier.service.AsymmetricKeySupplierService;

/**
 * @author Urvil Joshi
 *
 * @Since 1.0.0
 */
@RestController(value = "/asymmetric")
public class AsymmetricKeySupplierController {
	@Autowired
	AsymmetricKeySupplierService service;

	@GetMapping("/keys/{applicationid}/{keytype}")
	ResponseEntity<AsymmetricResponceDto> getAsymmetricKeys(@PathVariable("applicationid") int applicationId,
			@PathVariable("keytype") int keyType, @RequestHeader(value = "token") String token) {
		return new ResponseEntity<>(service.getAsymmetricKeys(applicationId, keyType, token), HttpStatus.FOUND);
	}

}
