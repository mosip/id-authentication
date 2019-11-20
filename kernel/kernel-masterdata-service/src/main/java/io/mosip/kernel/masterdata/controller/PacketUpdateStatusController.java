package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.packetstatusupdater.dto.PacketStatusUpdateDto;
import io.mosip.kernel.core.packetstatusupdater.spi.PacketStatusUpdateService;

@RestController
@RequestMapping("/packetStatusUpdate")
public class PacketUpdateStatusController {

	@Autowired
	private PacketStatusUpdateService packetUpdateStatusService;

	@GetMapping
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	public ResponseWrapper<PacketStatusUpdateDto> validatePacket(@RequestParam(value = "rid") String rId) {
		ResponseWrapper<PacketStatusUpdateDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(packetUpdateStatusService.getStatus(rId));
		return responseWrapper;
	}
}
