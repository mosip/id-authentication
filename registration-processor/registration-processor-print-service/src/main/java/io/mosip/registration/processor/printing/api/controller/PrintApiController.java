package io.mosip.registration.processor.printing.api.controller;

import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.printing.api.dto.PrintRequest;
import io.mosip.registration.processor.printing.api.dto.PrintResponse;
import io.mosip.registration.processor.printing.api.dto.RequestDTO;
import io.mosip.registration.processor.printing.api.dto.ResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class PrintApiController.
 * 
 * @author M1048358 Alok
 */
@RestController
@Api(tags = "Print PDF")
@RequestMapping("/registration-processor")
public class PrintApiController {

	/** The printservice. */
	@Autowired
	private PrintService<Map<String, byte[]>> printservice;

	/** The env. */
	@Autowired
	private Environment env;

	/** The Constant REG_PACKET_GENERATOR_SERVICE_ID. */
	private static final String REG_PRINT_SERVICE_ID = "mosip.registration.processor.registration.packetgenerator.id";

	/** The Constant REG_PACKET_GENERATOR_APPLICATION_VERSION. */
	private static final String REG_PRINT_SERVICE_VERSION = "mosip.registration.processor.application.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/**
	 * Gets the file.
	 *
	 * @param printRequest
	 *            the print request DTO
	 * @return the file
	 */
	@PostMapping(path = "/print/v0.1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Service to get Pdf of UIN Card", response = PrintResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "UIN card is successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the uin card"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<PrintResponse> getFile(@Valid @RequestBody PrintRequest printRequest) {
		RequestDTO request = printRequest.getRequest();
		byte[] pdfbytes = printservice.getDocuments(request.getIdtype(), request.getIdValue()).get("uinPdf");
		return ResponseEntity.status(HttpStatus.OK).body(buildPrintResponse(pdfbytes));
	}

	/**
	 * Builds the print response.
	 *
	 * @param pdfbytes the pdfbytes
	 * @return the prints the response
	 */
	private PrintResponse buildPrintResponse(byte[] pdfbytes) {
		PrintResponse printresponse = new PrintResponse();
		ResponseDTO response = new ResponseDTO();
		response.setFile(pdfbytes);
		if (Objects.isNull(printresponse.getId())) {
			printresponse.setId(env.getProperty(REG_PRINT_SERVICE_ID));
		}
		printresponse.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		printresponse.setVersion(env.getProperty(REG_PRINT_SERVICE_VERSION));
		printresponse.setResponse(response);
		printresponse.setErrors(null);

		return printresponse;
	}
}