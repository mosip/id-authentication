package io.mosip.authentication.common.manager;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.core.dto.IdAuthFraudAnalysisEventDTO;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class IdAuthFraudAnalysisEventManagerTest {

    @InjectMocks
    private IdAuthFraudAnalysisEventManager idAuthFraudAnalysisEventManager;

    @Mock
    private IdAuthFraudAnalysisEventDTO eventData;

    @Mock
    private AutnTxnRepository authTxnRepo;

    @Mock
    private AutnTxn txn;

    @Mock
    private IdAuthFraudAnalysisEventPublisher publisher;

    @Autowired
    private ObjectMapper mapper;
    
    @Before
    public void Before(){
    	EnvUtil.setIsFraudAnalysisEnabled(true);
        ReflectionTestUtils.setField(idAuthFraudAnalysisEventManager, "requestFloodingTimeDiff", 1);
        ReflectionTestUtils.setField(idAuthFraudAnalysisEventManager, "mapper", mapper);
    }

    /**
     * This class tests the analyseEvent method
     *                      analyseRequestFlooding method
     *                      requestFloodingBasedOnIdvId method
     *                      requestFloodingBasedOnPartnerId method
     *                      createEventData method
     */
    @Test
    public void analyseEventTest(){
        AutnTxn autnTxn = getAutnTxn();
        List<AutnTxn> requests = new ArrayList<>();
        requests.add(autnTxn);
        LocalDateTime t= LocalDateTime.of(2021, 11, 2, 12,24, 37, 3);
        Mockito.when(eventData.getRequestTime()).thenReturn(t);
        //Based on IdvId
        Mockito.when(eventData.getIndividualIdHash()).thenReturn("IndividualIdHash");
        Mockito.when(authTxnRepo.countByRefIdAndRequestDTtimesAfter("IndividualIdHash", t.minusSeconds(1))).thenReturn(1l);
        //Based on Partner Id
        Mockito.when(eventData.getPartnerId()).thenReturn("PartnerId");
        Mockito.when(authTxnRepo.countByRefIdAndRequestDTtimesAfter("PartnerId", t.minusSeconds(1))).thenReturn(1l);
        ReflectionTestUtils.invokeMethod(idAuthFraudAnalysisEventManager, "analyseEvent", autnTxn);
    }

    /**
     * This class tests the analyseDigitalSignatureFailure method
     * @throws IOException
     */
    @Test
    public void analyseDigitalSignatureFailureTest() throws IOException {
        String uri="uri/abed/eight/Film/nor";
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("individualId", "dc1551db-614b-53ff-b189-65bfb8125399");
        request.put("transactionID", "1234567890");
        request.put("requestTime","2021-11-02T12:24:37.003Z");
        String errorMessage = "errorMessage";
        idAuthFraudAnalysisEventManager.analyseDigitalSignatureFailure(uri, request, errorMessage);
    }

    /**
     * This class tests the getAuthType method
     */
    @Test
    public void getAuthTypeTest(){
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("individualId", "dc1551db-614b-53ff-b189-65bfb8125399");
        request.put("transactionID", "1234567890");
        request.put("requestTime","2021-11-02T12:24:37.003Z");
        List<String> pathSegments = new ArrayList<>();
        String authType = null;
//        when contextSuffix.contentEquals(OTP)
        pathSegments.add(null); pathSegments.add(null); pathSegments.add(null); pathSegments.add("otp");
        ReflectionTestUtils.invokeMethod(idAuthFraudAnalysisEventManager, "getAuthType", pathSegments, authType, request);
//        when contextSuffix.contentEquals(KYC)
        pathSegments.add(3, "kyc");
        ReflectionTestUtils.invokeMethod(idAuthFraudAnalysisEventManager, "getAuthType", pathSegments, authType, request);
//        when contextSuffix.contentEquals("auth)
        pathSegments.add(3, "auth");
        ReflectionTestUtils.invokeMethod(idAuthFraudAnalysisEventManager, "getAuthType", pathSegments, authType, request);
    }

    private AutnTxn getAutnTxn(){
        AutnTxn autnTxn = new AutnTxn();
        autnTxn.setId("dc1551db-614b-53ff-b189-65bfb8125399");
        autnTxn.setRequestDTtimes(
                LocalDateTime.of(2021, 11, 2, 12,24, 37, 3));
        autnTxn.setResponseDTimes(
                LocalDateTime.of(2021, 11, 2, 12, 24, 38, 756169));
        autnTxn.setRequestTrnId("1234567890");
        autnTxn.setAuthTypeCode("OTP-REQUEST");
        autnTxn.setStatusCode("Y");
        autnTxn.setStatusComment("OTP Request Success");
        autnTxn.setLangCode("NA");
        autnTxn.setRefIdType("UIN");
        autnTxn.setRefId("E463923C59E189F048633E7FB9378DC794CCDDCFB9F6AE841BE6DDD60CF13AD0");
        autnTxn.setToken("362737013453447806883457690320262449");
        autnTxn.setAuthTknId("283909196998392124732323266977762343");
        autnTxn.setEntitytype("PARTNER");
        autnTxn.setEntityId("1635855298474");
        autnTxn.setEntityName("1635855298474");
        autnTxn.setResponseSignature("eyJ4NWMiOlsiTUlJRHNqQ0NBcHFnQXdJQkFnSUlwREVLWDdkZ2FuWXdEUVlKS29aSWh2Y05BUUVMQlFBd2NERUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeEdqQVlCZ05WQkFzTUVVMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU01SVXdFd1lEVlFRRERBeDNkM2N1Ylc5emFYQXVhVzh3SGhjTk1qRXhNREkzTURVME1qTXdXaGNOTWpReE1ESTJNRFUwTWpNd1dqQjdNUXN3Q1FZRFZRUUdFd0pKVGpFTE1Ba0dBMVVFQ0F3Q1MwRXhFakFRQmdOVkJBY01DVUpCVGtkQlRFOVNSVEVOTUFzR0ExVUVDZ3dFU1VsVVFqRWdNQjRHQTFVRUN3d1hUVTlUU1ZBdFZFVkRTQzFEUlU1VVJWSWdLRWxFUVNreEdqQVlCZ05WQkFNTUVYZDNkeTV0YjNOcGNDNXBieTFUU1VkT01JSUJJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBazMybHh3L3Y4cVlKcDB4MklzdkZDLzBxanArVDkzL1hWa3VQblFYbkQ1Q3RSaHZiWjJPYXZLM0FnUDZwSkZIVWdUT1J5WFhObUV6T2VWb2lYK1NBSks3dDE5Q1d0Nmg2VlZuWXJvQTI0Mzltc2FPTUQwK3I2eFNhdi9qZVNxZWlYWDVZNmtjVzJhQm1Hd2cwSVI2ZnpHRnNwajVpWjQzbjJ4MWZNakhBa2EvS3d0Qlh0S1M4Z2x0TVp4dDRWVVNIVjNLZklXdkxJb2pIVitoSVdsMUg5bEczZDQ4UCtLdGtMdnhsVU0wRHJIdFhaWXVtM2lLL0ZOOEYxOFFEaHliQnJZN0d5UWJiVVMrakVzbitWRlFieUI4enYvZExONU44dDRXOWFqdjNsV09aMFkxNEJFb291bHB3VXB1dE0vQnMrSVh5M0VEeGthUnRFK2VQMnM5azhRSURBUUFCbzBVd1F6QVNCZ05WSFJNQkFmOEVDREFHQVFIL0FnRUJNQjBHQTFVZERnUVdCQlFFWFNsTTh0RmVnTTZvcWF0QmdZTGJvVzc1RlRBT0JnTlZIUThCQWY4RUJBTUNBb1F3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUJpTm9YRThkVWw2NmFHRWxDWGQ0UjVxRjdqVmNMMUg0bGhpUW82MUF0ZzM1MjZvVWM1ZC9ZeUpWUU80dnN6bVVXVGhTMW9NcXU4aXlLZGZNNU9VdElrWi9ibytqMVZtRjlzS1lkbmNzTVFPUmtQckU3V0x0aVAzSW96c25jcGxvWGFYL3NycGxTTjlSaUZWUnV1anBRczJhOEJtNzRONElVTXhwT1ZOcFRqbWZqWTNxVjQzRnhZNFErZHlQWWdLamdyRG5oYnFyWFRxM0w1VjRXdEZLTXJBc0ppVSt6cHhNcTBTN0laSEp2cTlNTFN3YTZMeHlWVk1LeDhwWS9YN0Rya3Y2bVVlS1llOC93ZHptSGI5ei9JdkdZMzBZeElXR09PVlZjTkNOYmRkQVZ1WUxRRVNDK01ydzVVUktGVzV5Y3ZrYTlnZ2s0eDBJa2dzNkVTNEd6Yz0iXSwieDV0I1MyNTYiOiJFTW5HcTVZa0M1c3BxZmpremJ0dkxBMGNYcGhHUlhwcDV2SWpHNThyOXNJIiwiYWxnIjoiUlMyNTYifQ..IJn3bAU9pXpc5kkp1w3arm6cFg88n1asxGFnRoVqApua-B-s4HAhloqZEQvG2_AQRckqnVRJmbgK6-Vb2R2uq-rp6D6y9V4bcKze6SKTnPiTi116aV5vG4j9dtn7UOph7Q1nwqcjnnltvkpdK8qkixgx3HTxKYYG9elEC94Z4sqohlE82zLOLQlZCUrc85RnRG9g1YX_4LWGQe-E0lzzVfvromx0kenkv3MJCEziZ7p6GrLC3YExDmg0cssZn-veHcxUyBBy6zdCKChuJorFOUY3HSw86SXlaA3iyaxO1ygsisLOclqdoLtYDtiNStIY0XX4rjWf37lDG3FKQ4DRGA");
        autnTxn.setRequestSignature("eyJ4NWMiOlsiTUlJRG5UQ0NBb1dnQXdJQkFnSUkwQVdsaWQzWEQ4a3dEUVlKS29aSWh2Y05BUUVMQlFBd2RqRUxNQWtHQTFVRUJoTUNTVTR4Q3pBSkJnTlZCQWdNQWt0Qk1SSXdFQVlEVlFRSERBbENRVTVIUVV4UFVrVXhEVEFMQmdOVkJBb01CRWxKVkVJeElEQWVCZ05WQkFzTUYwMVBVMGxRTFZSRlEwZ3RRMFZPVkVWU0lDaFFUVk1wTVJVd0V3WURWUVFEREF4M2QzY3ViVzl6YVhBdWFXOHdIaGNOTWpFeE1UQXlNVEl4TkRVMVdoY05Nakl4TVRBeU1USXhORFUxV2pCak1Rc3dDUVlEVlFRR0V3SkpUakVMTUFrR0ExVUVDQXdDUzBFeEZqQVVCZ05WQkFvTURURTJNelU0TlRVeU9USXlOREl4R2pBWUJnTlZCQXNNRVVsRVFTMVVSVk5VTFU5U1J5MVZUa2xVTVJNd0VRWURWUVFEREFwUVFWSlVUa1ZTTFhKd01JSUJJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBcDB2WnpINWl1R3dnbk55Q3h4dzlXRlB5cmlRdTZxZURFK2M3R0g3bUlhZmtEQTY1OWpDMVovMGhtemlCVStwV1pJTE9QUzhmMzJhVmxQOWdCaUlHdTlscFZjbnZKQ1VENG9GSE5meTNVVm9id0ZsVWZzaWJBMnF3NzE5bjU5M2NxQXMzZjRkdXVxLzhVWDJJWTAxS0lsM3kzdjlpRC9Ka3ozbzAvSXZ5S085R05KcE9LSEhKUjBvTW1RUWVPL3lXckoyVjJVOVhEd1B5NWVidjAzQ0NXWGFxVXg2b0NtVDdNQnp1VHgxTzFrRWgveFFjZ2c0ZnRVRyticUZQbjJjekRMa1FxeERPUk9raTdyTzl4NzBtNWVPTkFZQlVoWVFIMVRmNkNLUXJiYk9MbWpmK3RyKytZRVR4UmVDL1grUXkvaDZqQkVhc1JIMHluSldiQTNFV3Z3SURBUUFCbzBJd1FEQVBCZ05WSFJNQkFmOEVCVEFEQVFIL01CMEdBMVVkRGdRV0JCUVZsK0FLeGRVMjlZWlhsKy9GZWxzR3F1b0xTekFPQmdOVkhROEJBZjhFQkFNQ0FxUXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBSVpUV2YzdWErMnZCSVh6WUo4Z3lZN3czSEtjMmhaVnkwaXUwcmVabkoyVUtsUEFCblhjM0VQbkw1VEIrdXdkTHFWYVdPNVRaL1dPYU90NUJiY25VSkJ5UDhycFBPdm0xRSt2MU9ZYjVDaDhjM1FGQmk3cjVpRmFPUWxEVCtlYnk1c0JNd3FsOUlKc1Q4eUE1MWkwUWxlQ2ZoWVo2MWlIM2U5c2Z4WlhwNTRSWWdMMlNtTFEwSU9oTitMZElBQllxLzhhTkJRWEcvekttT1RybmJ6NHl6Z2pxQkt0Mk1KaEJpQlcrbVRhaHh1TEJyTjJ1YWRoV29SV2xNcTVsOHIrck96d1ord245dHFYbTZJR1liNnQ4eCtKRkUzTlVZTWhsSG1QOC9lU21NVVhDQ25nY3YxN2llUUxUUUNMamE2WlYyNmJ0cHZydFlCWWxrZWV6eEhaSXRNPSJdLCJhbGciOiJSUzI1NiJ9..XbqH3V9vBocsEtwH5G0mjNgDmFzpe-HpxKtB6eGO-qCwMLAG2cwBY5C_ZJ5qLdHlSaUWgDafTErKKB2HHbmOc20yHl8x7f08Rnn8sfXpgCsMlfqsG7yYI2hsGUfvN9z4j-n--rj8EaigPQW9rHTWcplRARMWq9L0QvRIEZMu7sz0DdJbiOtlNKS6gc88kGHMebmF7kej93xnbVeWNzq1Q5ahLFK9_wrDRBG9Wllc8t2kppXMgFXqLN_1cqhqkhFgtX0FPqfUqX3LvH3sRIkkVzdxQAqH_QxtdYOiXo2rxTyksYLwQOKF3bO-hDBLRVCAZqFsy8aHaRkn-HcwrOYHLw");
        autnTxn.setCrBy("IDA");
        autnTxn.setCrDTimes(
                LocalDateTime.of(2021, 11, 2, 12, 24, 38, 755604));
        autnTxn.setUpdBy(null);
        autnTxn.setUpdDTimes(null);
        autnTxn.setDeleted(false);
        autnTxn.setDelDTimes(null);
        return autnTxn;
    }
    
    @Test
    public void testFormatAsJsonWithListInput() {
    	String response = ReflectionTestUtils.invokeMethod(idAuthFraudAnalysisEventManager, "formatAsJson", "[{\"errorCode\" : \"IDA-MLC-009\", \"errorMessage\" : \"Invalid input parameter\"}]");
    	assertEquals("[{\\\"errorCode\\\" : \\\"IDA-MLC-009\\\", \\\"errorMessage\\\" : \\\"Invalid input parameter\\\"}]", response);
    }
    
    @Test
    public void testFormatAsJsonWithStringInput() {
    	String response = ReflectionTestUtils.invokeMethod(idAuthFraudAnalysisEventManager, "formatAsJson", "Auth Success");
    	assertEquals("Auth Success", response);
    }
}
