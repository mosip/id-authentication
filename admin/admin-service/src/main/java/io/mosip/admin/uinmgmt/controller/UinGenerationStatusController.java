package io.mosip.admin.uinmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.uinmgmt.dto.UinGenerationStatusDto;
import io.mosip.admin.uinmgmt.dto.UinGenerationStatusResponseDto;
import io.mosip.admin.uinmgmt.service.UinGenerationStatusService;
import io.mosip.kernel.core.http.ResponseWrapper;

@RestController
public class UinGenerationStatusController {
	
	@Autowired
	private UinGenerationStatusService uinGenerationStatusService;
	
	@PreAuthorize("hasAnyRole('REGISTRATION_ADMIN')")
	@GetMapping(value = "/packetstatus/{rid}")
	public UinGenerationStatusResponseDto getPacketStatus (@PathVariable("rid") String rid)
	{
		return uinGenerationStatusService.getPacketStatus(rid);
	}

}
