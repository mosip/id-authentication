package io.mosip.authentication.internal.service.batch;

import io.mosip.authentication.common.service.impl.idevent.CredentialStoreStatus;
import io.mosip.authentication.common.service.integration.CredentialRequestManager;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.util.DateUtils;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class MissingCredentialsItemReaderTest {

    /** The MissingCredentialsItemReader */
    @InjectMocks
    private MissingCredentialsItemReader missingCredentialsItemReader;

    @Mock
    private CredentialEventStoreRepository credentialEventRepo;

    @Mock
    private CredentialRequestManager credentialRequestManager;

    @Before
    public void Before(){
        ReflectionTestUtils.setField(missingCredentialsItemReader, "maxCredentialPullWindowDays", 5);
        ReflectionTestUtils.setField(missingCredentialsItemReader, "currentPageIndex", new AtomicInteger(0));
    }
    /**
     * This class tests the initialize method
     */
    @Test
    public  void initializeTest(){
        ReflectionTestUtils.invokeMethod(missingCredentialsItemReader, "initialize");
    }

    /**
     * This class tests the read method
     *
     * @throws Exception exception
     */
    @Test
    public void readTest() throws Exception {
        //        requestIdsIterator.hasNext() is false
        missingCredentialsItemReader.read();
        //        requestIdsIterator.hasNext() = true
        CredentialRequestIdsDto credentialRequestIdsDto1= new CredentialRequestIdsDto();
        CredentialRequestIdsDto credentialRequestIdsDto2= new CredentialRequestIdsDto();
        CredentialRequestIdsDto credentialRequestIdsDto3= new CredentialRequestIdsDto();
        List<CredentialRequestIdsDto> credRequests = new ArrayList<>();
        credRequests.add(credentialRequestIdsDto1);
        credRequests.add(credentialRequestIdsDto2);
        credRequests.add(credentialRequestIdsDto3);
        LocalDateTime t=LocalDateTime.now().plusYears(1);
        Mockito.when(credentialEventRepo.findMaxCrDTimesByStatusCode(CredentialStoreStatus.STORED.name())).thenReturn(java.util.Optional.of(t));
        Mockito.when(credentialRequestManager.getMissingCredentialsPageItems(0, DateUtils.formatToISOString(t))).thenReturn(credRequests);
        ReflectionTestUtils.invokeMethod(missingCredentialsItemReader, "read");
    }

    /**
     * This class tests the read method where it
     * failed and throws IdAuthRetryException
     *
     */
    @Test(expected = IdAuthRetryException.class)
    public void readIdAuthRetryExceptionTest() throws Exception {
        LocalDateTime t=LocalDateTime.now().plusYears(1);
        Mockito.when(credentialEventRepo.findMaxCrDTimesByStatusCode(CredentialStoreStatus.STORED.name())).thenReturn(java.util.Optional.of(t));
        Mockito.doThrow(IDDataValidationException.class).when(credentialRequestManager).getMissingCredentialsPageItems(0, DateUtils.formatToISOString(t));
        ReflectionTestUtils.invokeMethod(missingCredentialsItemReader, "read");
    }

    /**
     * This class tests the read method where it
     * failed and throws Exception
     *
     */
    @Test(expected = Exception.class)
    public void readExceptionTest() throws RestServiceException, IDDataValidationException {
        CredentialRequestIdsDto credentialRequestIdsDto1= new CredentialRequestIdsDto();
        CredentialRequestIdsDto credentialRequestIdsDto2= new CredentialRequestIdsDto();
        CredentialRequestIdsDto credentialRequestIdsDto3= new CredentialRequestIdsDto();
        List<CredentialRequestIdsDto> credRequests = new ArrayList<>();
        credRequests.add(credentialRequestIdsDto1);
        credRequests.add(credentialRequestIdsDto2);
        credRequests.add(credentialRequestIdsDto3);
        LocalDateTime t=LocalDateTime.now().plusYears(1);
        List<String> requestIds=new ArrayList<>();
        requestIds.add(null);requestIds.add(null);requestIds.add(null);
        Mockito.when(credentialEventRepo.findMaxCrDTimesByStatusCode(CredentialStoreStatus.STORED.name())).thenReturn(java.util.Optional.of(t));
        Mockito.when(credentialRequestManager.getMissingCredentialsPageItems(0, DateUtils.formatToISOString(t))).thenReturn(credRequests);
        Mockito.when(credentialEventRepo.findDistictCredentialTransactionIdsInList(requestIds)).thenReturn(null);
        ReflectionTestUtils.invokeMethod(missingCredentialsItemReader, "read");
    }

    /**
     * This class tests the getRequestIdsIterator method
     */
    @Test
    public  void getRequestIdsIteratorTest(){
        ReflectionTestUtils.invokeMethod(missingCredentialsItemReader, "getRequestIdsIterator");
    }

    /**
     * This class tests the getEffectiveDTimes method
     *
     */
    @Test
    public void getEffectiveDTimesTest(){
        Mockito.when(credentialEventRepo.findMaxCrDTimesByStatusCode(CredentialStoreStatus.STORED.name())).thenReturn(java.util.Optional.ofNullable(LocalDateTime.now().minus(5, ChronoUnit.DAYS)));
        ReflectionTestUtils.invokeMethod(missingCredentialsItemReader, "getEffectiveDTimes");
    }
}
