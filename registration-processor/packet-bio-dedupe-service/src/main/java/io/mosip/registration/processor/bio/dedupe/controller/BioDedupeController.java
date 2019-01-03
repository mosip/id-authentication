/**
 * 
 */
package io.mosip.registration.processor.bio.dedupe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author M1022006
 *
 */
@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/bio-dedupe")
@Api(tags = "Biodedupe")
public class BioDedupeController {

	@Autowired
	private BioDedupeService bioDedupeService;

	@GetMapping(path = "/biodedupe", consumes = MediaType.ALL_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = "Get the multipart file of packet", response = MultipartFile.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Multipart file is successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the Registration Entity") })
	public MultipartFile getFile(@RequestParam(value = "refId", required = true) String refId) {
		return null;

	}
}
