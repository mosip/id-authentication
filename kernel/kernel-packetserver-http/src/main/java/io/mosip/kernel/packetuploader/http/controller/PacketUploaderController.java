package io.mosip.kernel.packetuploader.http.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.packetuploader.http.dto.PacketUploaderResponceDTO;
import io.mosip.kernel.packetuploader.http.service.PacketUploaderService;

/**
 * Controller for uploading packet
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@RestController
@CrossOrigin
public class PacketUploaderController {
	/**
	 * {@link #PacketUploaderController()} instance
	 */
	@Autowired
	PacketUploaderService service;

	/**
	 * Mapped method to upload packet
	 * 
	 * @param packet
	 *            packet to upload
	 * @return {@link ResponseEntity} with File properties
	 * @throws IOException
	 *             signals that an I/O exception of some sort has occurred. This
	 *             class is the general class of exceptions produced by failed
	 *             or interrupted I/O operations.
	 */
	@PostMapping("/uploads")
	public ResponseEntity<PacketUploaderResponceDTO> upload(
			MultipartFile packet) throws IOException {
		return new ResponseEntity<>(service.upload(packet), HttpStatus.CREATED);
	}

}
