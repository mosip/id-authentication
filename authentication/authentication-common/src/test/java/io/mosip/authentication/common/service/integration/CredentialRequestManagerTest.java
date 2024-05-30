package io.mosip.authentication.common.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.common.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.http.ResponseWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class CredentialRequestManagerTest {

    /** The Credential Request Manager. */
    @InjectMocks
    private CredentialRequestManager credentialRequestManager;

    @Mock
    private RestRequestFactory restRequestFactory;

    @Mock
    private RestHelper restHelper;

    /** The object mapper. */
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RestRequestDTO restRequestDTO;

    private OtpGeneratorRequestDto otpGeneratorRequestDto;

    /**
     * Before.
     */
    @Before
    public void Before(){
        ReflectionTestUtils.setField(credentialRequestManager, "restRequestFactory", restRequestFactory);
        ReflectionTestUtils.setField(credentialRequestManager, "restHelper", restHelper);
        ReflectionTestUtils.setField(credentialRequestManager, "objectMapper", objectMapper);
    }

    /**
     * This class tests the getMissingCredentialsPageItems method
     * @throws RestServiceException
     * @throws IDDataValidationException
     */
    @Test
    public void getMissingCredentialsPageItemsTest() throws RestServiceException, IDDataValidationException {
        int currentPageIndex =1;
        String effectivedtimes="effectivedtimes";
        //request
        RestRequestDTO restRequestDTO = getRestRequestDTO();
        //Response
        ResponseWrapper<Map> response = new ResponseWrapper<>();
        Map<String, Object> res = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        res.put("data", data);
        response.setResponse(res);

        Mockito.when(restRequestFactory.buildRequest(
                RestServicesConstants.CRED_REQUEST_GET_REQUEST_IDS, null, ResponseWrapper.class)).thenReturn(restRequestDTO);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        credentialRequestManager.getMissingCredentialsPageItems(currentPageIndex, effectivedtimes);

        //if data!=null
        res.put("data", null);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        credentialRequestManager.getMissingCredentialsPageItems(currentPageIndex, effectivedtimes);
    }

    /**
     * This class tests the getMissingCredentialsPageItems method
     * @throws RestServiceException
     * @throws IDDataValidationException
     */
    @Test(expected = IDDataValidationException.class)
    public void getMissingCredentialsPageItemsException1Test() throws RestServiceException, IDDataValidationException {
        int currentPageIndex =1;
        String effectivedtimes="effectivedtimes";
        Mockito.doThrow(IDDataValidationException.class).when(restRequestFactory).buildRequest(RestServicesConstants.CRED_REQUEST_GET_REQUEST_IDS, null, ResponseWrapper.class);
        credentialRequestManager.getMissingCredentialsPageItems(currentPageIndex, effectivedtimes);
    }

    /**
     * This class tests the getMissingCredentialsPageItems method
     * @throws RestServiceException
     * @throws IDDataValidationException
     */
    @Test(expected = RestServiceException.class)
    public void getMissingCredentialsPageItemsException2Test() throws RestServiceException, IDDataValidationException {
        int currentPageIndex =1;
        String effectivedtimes="effectivedtimes";
        //request
        RestRequestDTO restRequestDTO = getRestRequestDTO();
        //Response
        ResponseWrapper<RestRequestDTO> response = new ResponseWrapper<>();

        Mockito.when(restRequestFactory.buildRequest(
                RestServicesConstants.CRED_REQUEST_GET_REQUEST_IDS, null, ResponseWrapper.class)).thenReturn(restRequestDTO);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
                IdRepoErrorConstants.CLIENT_ERROR, response.toString(), response));
        credentialRequestManager.getMissingCredentialsPageItems(currentPageIndex, effectivedtimes);
    }

    /**
     * This class tests the retriggerCredentialIssuance method
     * @throws RestServiceException
     * @throws IDDataValidationException
     */
    @Test
    public void retriggerCredentialIssuanceTest() throws RestServiceException, IDDataValidationException {
        String requestId = "requestId";
        //request
        RestRequestDTO restRequestDTO = getRestRequestDTO();
        //Response
        ResponseWrapper<RestRequestDTO> response = new ResponseWrapper<>();

        Mockito.when(restRequestFactory.buildRequest(
                RestServicesConstants.CRED_REQUEST_RETRIGGER_CRED_ISSUANCE, null, ResponseWrapper.class)).thenReturn(restRequestDTO);
        Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(response);
        credentialRequestManager.retriggerCredentialIssuance(requestId);
    }

    /**
     * This class tests the retriggerCredentialIssuance method
     * @throws RestServiceException
     * @throws IDDataValidationException
     */
    @Test(expected = RestServiceException.class)
    public void retriggerCredentialIssuanceException1Test() throws RestServiceException, IDDataValidationException {
        String requestId = "requestId";
        RestRequestDTO request = new RestRequestDTO();
        Mockito.when(restRequestFactory.buildRequest(
                RestServicesConstants.CRED_REQUEST_RETRIGGER_CRED_ISSUANCE, null, ResponseWrapper.class)).thenReturn(request);
        Map<String, String> pathVariables = Map.of("requestId", requestId);
        request.setPathVariables(pathVariables);
        Mockito.doThrow(RestServiceException.class).when(restHelper).requestSync(request);
        credentialRequestManager.retriggerCredentialIssuance(requestId);
    }

    /**
     * This class tests the retriggerCredentialIssuance method
     * @throws RestServiceException
     * @throws IDDataValidationException
     */
    @Test(expected = IDDataValidationException.class)
    public void retriggerCredentialIssuanceException2Test() throws RestServiceException, IDDataValidationException {
        String requestId = "requestId";
        Mockito.doThrow(IDDataValidationException.class).when(restRequestFactory).buildRequest(RestServicesConstants.CRED_REQUEST_RETRIGGER_CRED_ISSUANCE, null, ResponseWrapper.class);
        credentialRequestManager.retriggerCredentialIssuance(requestId);
    }

    private RestRequestDTO getRestRequestDTO() {
        RestRequestDTO restRequestDTO = new RestRequestDTO();
        restRequestDTO.setHttpMethod(HttpMethod.POST);
        restRequestDTO.setUri("http://localhost:8083/cred_request_manager/otps");
        restRequestDTO.setRequestBody(otpGeneratorRequestDto);
        restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
        restRequestDTO.setTimeout(23);
        Map<String, String> pathVariables = Map.of("pageNumber", String.valueOf(1),
                "effectivedtimes", "effectivedtimes");
        restRequestDTO.setPathVariables(pathVariables);
        return restRequestDTO;
    }
}
