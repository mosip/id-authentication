package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.EntityValueFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MatchIdentityDataHelperTest {

    @InjectMocks
    private MatchIdentityDataHelper matchIdentityDataHelper;

    @Mock
    private MatchTypeHelper matchTypeHelper;

    @Mock
    private AuthRequestDTO authRequestDTO;

    @Mock
    private EntityValueFetcher entityValueFetcher;

    @Mock
    private MatchInput matchInput1;

    @Mock
    private MatchInput matchInput2;

    @Mock
    private MatchOutput matchOutput1;

    @Mock
    private MatchOutput matchOutput2;

    private String uin = "123456789012";
    private String partnerId = "partner123";

    @Before
    public void setUp() {
        // Verify autowired dependency is injected
        assertNotNull(matchIdentityDataHelper);
    }

    @Test
    public void testMatchIdentityDataEmptyInputListReturnsEmptyList() throws IdAuthenticationBusinessException {
        // Arrange
        Collection<MatchInput> emptyList = new ArrayList<>();

        // Act
        List<MatchOutput> result = matchIdentityDataHelper.matchIdentityData(
                authRequestDTO, uin, emptyList, entityValueFetcher, partnerId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(matchTypeHelper, never()).matchType(any(), any(), any(), any(), any());
    }

    @Test
    public void testMatchIdentityDataNullMatchOutputsReturnsEmptyList() throws IdAuthenticationBusinessException {
        // Arrange
        Collection<MatchInput> matchInputs = Arrays.asList(matchInput1);
        when(matchTypeHelper.matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId))
                .thenReturn(null);

        // Act
        List<MatchOutput> result = matchIdentityDataHelper.matchIdentityData(
                authRequestDTO, uin, matchInputs, entityValueFetcher, partnerId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(matchTypeHelper).matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId);
    }

    @Test
    public void testMatchIdentityDataSingleValidMatchInputReturnsSingleMatchOutput() throws IdAuthenticationBusinessException {
        // Arrange
        Collection<MatchInput> matchInputs = Arrays.asList(matchInput1);
        when(matchTypeHelper.matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId))
                .thenReturn(matchOutput1);

        // Act
        List<MatchOutput> result = matchIdentityDataHelper.matchIdentityData(
                authRequestDTO, uin, matchInputs, entityValueFetcher, partnerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(matchOutput1, result.get(0));
        verify(matchTypeHelper).matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId);
    }

    @Test
    public void testMatchIdentityDataMultipleValidMatchInputsReturnsAllMatchOutputs() throws IdAuthenticationBusinessException {
        // Arrange
        Collection<MatchInput> matchInputs = Arrays.asList(matchInput1, matchInput2);
        when(matchTypeHelper.matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId))
                .thenReturn(matchOutput1);
        when(matchTypeHelper.matchType(authRequestDTO, uin, matchInput2, entityValueFetcher, partnerId))
                .thenReturn(matchOutput2);

        // Act
        List<MatchOutput> result = matchIdentityDataHelper.matchIdentityData(
                authRequestDTO, uin, matchInputs, entityValueFetcher, partnerId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(matchOutput1, result.get(0));
        assertEquals(matchOutput2, result.get(1));
        verify(matchTypeHelper, times(2)).matchType(any(), any(), any(), any(), any());
    }

    @Test
    public void testMatchIdentityDataMixedNullAndValidOutputsReturnsOnlyValidOutputs() throws IdAuthenticationBusinessException {
        // Arrange
        Collection<MatchInput> matchInputs = Arrays.asList(matchInput1, matchInput2);
        when(matchTypeHelper.matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId))
                .thenReturn(matchOutput1);
        when(matchTypeHelper.matchType(authRequestDTO, uin, matchInput2, entityValueFetcher, partnerId))
                .thenReturn(null);

        // Act
        List<MatchOutput> result = matchIdentityDataHelper.matchIdentityData(
                authRequestDTO, uin, matchInputs, entityValueFetcher, partnerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(matchOutput1, result.get(0));
        verify(matchTypeHelper).matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId);
        verify(matchTypeHelper).matchType(authRequestDTO, uin, matchInput2, entityValueFetcher, partnerId);
    }

    @Test
    public void testMatchIdentityDataMatchTypeHelperThrowsExceptionPropagatesException() throws IdAuthenticationBusinessException {
        // Arrange
        Collection<MatchInput> matchInputs = Arrays.asList(matchInput1);
        IdAuthenticationBusinessException expectedException =
                new IdAuthenticationBusinessException("ERROR_CODE", "Error message");
        when(matchTypeHelper.matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId))
                .thenThrow(expectedException);

        // Act & Assert
        try {
            matchIdentityDataHelper.matchIdentityData(authRequestDTO, uin, matchInputs, entityValueFetcher, partnerId);
            fail("Expected IdAuthenticationBusinessException to be thrown");
        } catch (IdAuthenticationBusinessException e) {
            assertEquals("ERROR_CODE", e.getErrorCode());
            assertEquals("ERROR_CODE --> Error message", e.getMessage());
        }
        verify(matchTypeHelper).matchType(authRequestDTO, uin, matchInput1, entityValueFetcher, partnerId);
    }
}


