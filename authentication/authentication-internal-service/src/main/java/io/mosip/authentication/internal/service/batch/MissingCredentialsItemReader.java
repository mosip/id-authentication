package io.mosip.authentication.internal.service.batch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.idrepository.core.dto.PageDto;
import io.mosip.kernel.core.util.DateUtils;
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MissingCredentialsItemReader implements ItemReader<CredentialRequestIdsDto> {
	
	@Autowired
	@Qualifier("external")
	private RestHelper restHelper;
	
	@Autowired
	private RestRequestFactory restRequestFactory;

	
	private AtomicInteger currentPageIndex = new AtomicInteger(0);
	
	private String effectivedtimes = getEffectiveDTimes();

	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;

	@Autowired

	private ObjectMapper objectMapper;

	@Override
	public CredentialRequestIdsDto read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		List<CredentialRequestIdsDto> requestIds = getNextPageItems();
		return Stream.builder().;
	}

	private List<CredentialRequestIdsDto> getNextPageItems() throws IDDataValidationException, RestServiceException {
		RestRequestDTO request = restRequestFactory.buildRequest(RestServicesConstants.CRED_REQUEST_GET_REQUEST_IDS, null, PageDto.class);
		Map<String, String> pathVariables = Map.of("pageNumber", String.valueOf(currentPageIndex.getAndIncrement()),
													"effectivedtimes", effectivedtimes);
		request.setPathVariables(pathVariables);
		PageDto<CredentialRequestIdsDto> response;
		try {
			response = restHelper.<PageDto>requestSync(request);
		} catch (RestServiceException e) {
			RestHelperImpl.getErrorList(e.getResponseBodyAsString(), objectMapper);
		}
		List<CredentialRequestIdsDto> requestIds = response.getData();
		return requestIds;
	}

	private String getEffectiveDTimes() {
		return DateUtils
				.formatToISOString(credentialEventRepo.getMaxCrDTimes()
						.orElseGet(DateUtils::getUTCCurrentDateTime));
	}

}
