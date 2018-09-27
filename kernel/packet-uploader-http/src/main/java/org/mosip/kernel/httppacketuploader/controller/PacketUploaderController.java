package org.mosip.kernel.httppacketuploader.controller;

import org.mosip.kernel.httppacketuploader.dto.PacketUploaderResponceDTO;
import org.mosip.kernel.httppacketuploader.service.PacketUploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	 */
	@PostMapping("/uploads")
	public ResponseEntity<PacketUploaderResponceDTO> upload(MultipartFile packet) {
		return new ResponseEntity<>(service.storePacket(packet), HttpStatus.CREATED);
	}

}
