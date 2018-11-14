package io.mosip.kernel.keymanager.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.keymanager.dto.KeymanagerResponseDto;
import io.mosip.kernel.keymanager.service.KeymanagerService;

/**
 * This class provides controller methods for Key manager.
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
public class KeymanagerController {

	@Autowired
	KeymanagerService keymanagerService;

	@GetMapping(value = "/keymanager/publickey/{appId}")
	public ResponseEntity<KeymanagerResponseDto> getPublicKey(@PathVariable("appId") String appId,
			@RequestParam("timeStamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timeStamp,
			@RequestParam("machineId") String machineId) {

		System.out.println(appId + "," + timeStamp + "," + machineId);

		return new ResponseEntity<>(keymanagerService.getPublicKey(), HttpStatus.CREATED);
	}
}
