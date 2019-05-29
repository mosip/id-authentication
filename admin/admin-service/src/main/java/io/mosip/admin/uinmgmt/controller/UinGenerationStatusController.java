package io.mosip.admin.uinmgmt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.uinmgmt.dto.UinGenerationStatusDto;
import io.mosip.admin.uinmgmt.service.UinGenerationStatusService;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RestController
public class UinGenerationStatusController {

	@Autowired
	private UinGenerationStatusService uinGenerationStatusService;

	@PreAuthorize("hasRole('REGISTRATION_ADMIN')")
	@GetMapping(value = "/packetstatus/{rid}")
	public ResponseWrapper<List<UinGenerationStatusDto>> getPacketStatus(@PathVariable("rid") String rid)
			throws JsonParseException, JsonMappingException, IOException {
		return uinGenerationStatusService.getPacketStatus(rid);
	}

}
