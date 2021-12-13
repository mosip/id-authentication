package io.mosip.authentication.common.service.helper;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;

@RestController
public class TestController {

	RestHelper restHelper;

	@PostMapping(path = "/requestSync", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object requestSync(@RequestBody RestRequestDTO request) throws RestServiceException {
		return restHelper.requestSync(request);
	}

	@PostMapping(value = "/requestASync")
	public Object requestASync(@RequestBody RestRequestDTO request) throws RestServiceException {
		return restHelper.requestAsync(request);
	}
}
