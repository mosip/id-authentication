package io.mosip.authentication.common.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.core.dto.ObjectWithIdVersionTransactionID;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.profile.AuthAnonymousProfileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class IdaRequestResponseConsumerUtilTest {

    @InjectMocks
    private IdaRequestResponsConsumerUtil idaRequestResponsConsumerUtil;

    @Mock
    private AuthAnonymousProfileService authAnonymousProfileService;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private IdService<AutnTxn> idService;

    @Mock
    private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;

    private ObjectWithMetadata sourceRequestWithMetadata;

    private ObjectWithIdVersionTransactionID targetResponseWithIdVersion;

    private ObjectWithMetadata targetObjectWithMetadata;

    @Before
    public void Before(){
        targetObjectWithMetadata = new ObjectWithMetadata() {
            @Override
            public Map<String, Object> getMetadata() {
                return null;
            }

            @Override
            public void setMetadata(Map<String, Object> metadata) {

            }
        };
        sourceRequestWithMetadata = new ObjectWithMetadata() {
            @Override
            public Map<String, Object> getMetadata() {
                return null;
            }

            @Override
            public void setMetadata(Map<String, Object> metadata) {

            }
        };
        targetResponseWithIdVersion = new ObjectWithIdVersionTransactionID() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public void setId(String id) {

            }

            @Override
            public String getVersion() {
                return null;
            }

            @Override
            public void setVersion(String version) {

            }

            @Override
            public String getTransactionID() {
                return null;
            }

            @Override
            public void setTransactionID(String transactionID) {

            }
        };
    }

    /**
     * This class tests the storeAnonymousProfile method
     */
    @Test
    public void storeAnonymousProfileTest(){
        Map<String, Object> requestBod = new HashMap<>();
        Map<String, Object> requestMetadata = new HashMap<>();
        Map<String, Object> responseMetadata = new HashMap<>();
        boolean status = true;
        List<AuthError > errors = new ArrayList<>();
        idaRequestResponsConsumerUtil.storeAnonymousProfile(requestBod, requestMetadata, responseMetadata, status, errors);
    }

    /**
     * This class tests the storeAuthTransaction method
     *
     * @throws IdAuthenticationAppException
     */
    @Test
    public void storeAuthTransactionTest() throws IdAuthenticationAppException {
        Map<String, Object> metadata = new HashMap<>();
        Object authTxnObj = new Object();
        metadata.put("AutnTxn", authTxnObj);
        String requestSignature = "requestSignature";
        String responseSignature = "responseSignature";
        AutnTxn autnTxn = new AutnTxn();
        Mockito.when(mapper.convertValue(authTxnObj, AutnTxn.class)).thenReturn(autnTxn);
        idaRequestResponsConsumerUtil.storeAuthTransaction(metadata, requestSignature, responseSignature);
    }

    /**
     * This class tests the processCredentialStoreEvent method
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     * @throws IdAuthenticationAppException
     */
    @Test(expected = Exception.class)
    public void storeAuthTransactionExceptionTest() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
        Map<String, Object> metadata = new HashMap<>();
        Object authTxnObj = new Object();
        metadata.put("AutnTxn", authTxnObj);
        String requestSignature = "requestSignature";
        String responseSignature = "responseSignature";
        AutnTxn autnTxn = new AutnTxn();
        Mockito.when(mapper.convertValue(authTxnObj, AutnTxn.class)).thenReturn(autnTxn);
        autnTxn.setRequestSignature(requestSignature);
        autnTxn.setResponseSignature(responseSignature);
        Mockito.doThrow(IdAuthenticationBusinessException.class).when(idService).saveAutnTxn(autnTxn);
        idaRequestResponsConsumerUtil.storeAuthTransaction(metadata, requestSignature, responseSignature);
    }

    /**
     * This class tests the setIdVersionToResponse method
     */
    @Test
    public void setIdVersionToResponseTest(){
        ReflectionTestUtils.invokeMethod(idaRequestResponsConsumerUtil, "setIdVersionToResponse", sourceRequestWithMetadata, targetResponseWithIdVersion);
    }

    /**
     * This class tests the setTransactionIdToResponse method
     */
    @Test
    public void setTransactionIdToResponseTest(){
        ReflectionTestUtils.invokeMethod(idaRequestResponsConsumerUtil, "setTransactionIdToResponse", sourceRequestWithMetadata, targetResponseWithIdVersion);
    }

    /**
     * This class tests the setIdVersionToObjectWithMetadata method
     */
    @Test
    public void setIdVersionToObjectWithMetadataTest(){
        ReflectionTestUtils.invokeMethod(idaRequestResponsConsumerUtil, "setIdVersionToObjectWithMetadata", sourceRequestWithMetadata, targetObjectWithMetadata);
    }

    /**
     * This class tests the getResponseTime method
     */
    @Test
    public void getResponseTimeTest(){
        String requestTime = "2022-01-18T15:26:41.661Z";
        String dateTimePattern = "yyy-MM-dd'T'HH:mm:ss.SSSXXX";
        ReflectionTestUtils.invokeMethod(idaRequestResponsConsumerUtil, "getResponseTime", requestTime, dateTimePattern);

        requestTime =null;
        ReflectionTestUtils.invokeMethod(idaRequestResponsConsumerUtil, "getResponseTime", requestTime, dateTimePattern);
    }
}
