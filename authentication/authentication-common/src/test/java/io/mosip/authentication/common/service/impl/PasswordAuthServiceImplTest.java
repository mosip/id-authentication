package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.PasswordAuthType;
import io.mosip.authentication.common.service.impl.match.PasswordMatchType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PasswordAuthServiceImplTest {

    @InjectMocks
    private PasswordAuthServiceImpl service;

    @Mock
    private IdInfoHelper idInfoHelper;

    @Mock
    private MatchInputBuilder matchInputBuilder;

    @Mock
    private IDAMappingConfig idaMappingConfig;

    private AuthRequestDTO authRequestDTO;
    private Map<String, List<IdentityInfoDTO>> idInfo;
    private String partnerId = "partner1";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authRequestDTO = new AuthRequestDTO();
        idInfo = new HashMap<>();
        idInfo.put("id", Collections.singletonList(new IdentityInfoDTO()));
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void testAuthenticate_IdInfoNull_ThrowsException() throws IdAuthenticationBusinessException {
        service.authenticate(authRequestDTO, "IND123", null, partnerId);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void testAuthenticate_IdInfoEmpty_ThrowsException() throws IdAuthenticationBusinessException {
        service.authenticate(authRequestDTO, "IND123", new HashMap<>(), partnerId);
    }

    @Test
    public void testConstructMatchInput() {
        List<MatchInput> mockInputs = Arrays.asList(mock(MatchInput.class));
        when(matchInputBuilder.buildMatchInput(any(), any(), any(), any())).thenReturn(mockInputs);

        List<MatchInput> result = service.constructMatchInput(authRequestDTO, idInfo);
        assertEquals(mockInputs, result);
    }

    @Test
    public void testAuthenticate_Success() throws IdAuthenticationBusinessException {
        // Mock MatchInputBuilder
        MatchInput mockInput = new MatchInput(); // create with proper constructor if needed
        when(matchInputBuilder.buildMatchInput(eq(authRequestDTO), any(), any(), eq(idInfo)))
                .thenReturn(Collections.singletonList(mockInput));

        // Mock MatchOutput returned by IdInfoHelper
        MatchOutput output = new MatchOutput(100, true, "STRATEGY", PasswordMatchType.PASSWORD, "EN", "ID123");
        when(idInfoHelper.matchIdentityData(eq(authRequestDTO), eq(idInfo), anyList(), eq(partnerId)))
                .thenReturn(Collections.singletonList(output));

        // Call authenticate
        AuthStatusInfo result = service.authenticate(authRequestDTO, "IND123", idInfo, partnerId);

        // Verify
        assertNotNull(result);
        verify(matchInputBuilder, times(1)).buildMatchInput(eq(authRequestDTO), any(), any(), eq(idInfo));
        verify(idInfoHelper, times(1)).matchIdentityData(eq(authRequestDTO), eq(idInfo), anyList(), eq(partnerId));
    }
}
