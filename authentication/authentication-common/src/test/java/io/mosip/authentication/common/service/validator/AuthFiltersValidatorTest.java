package io.mosip.authentication.common.service.validator;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.factory.MosipAuthFilterFactory;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthFiltersValidatorTest {

    @Mock
    private MosipAuthFilterFactory mosipAuthFilterFactory;

    @Mock
    private IMosipAuthFilter filter1;

    @Mock
    private IMosipAuthFilter filter2;

    @InjectMocks
    private AuthFiltersValidator validator;

    private AuthRequestDTO authRequestDTO;
    private Map<String, List<IdentityInfoDTO>> identityData;
    private Map<String, Object> properties;

    @Before
    public void setUp() {
        authRequestDTO = new AuthRequestDTO();
        identityData = Map.of();
        properties = Map.of();
    }

    @Test
    public void validateAuthFilters_callsEachEnabledFilterInOrder() throws Exception {
        when(mosipAuthFilterFactory.getEnabledAuthFilters()).thenReturn(List.of(filter1, filter2));

        validator.validateAuthFilters(authRequestDTO, identityData, properties);

        verify(filter1, times(1)).validate(authRequestDTO, identityData, properties);
        verify(filter2, times(1)).validate(authRequestDTO, identityData, properties);
    }

    @Test(expected = IdAuthenticationFilterException.class)
    public void validateAuthFilters_stopsWhenAFilterThrows() throws Exception {
        when(mosipAuthFilterFactory.getEnabledAuthFilters()).thenReturn(List.of(filter1, filter2));
        doThrow(new IdAuthenticationFilterException("E", "m")).when(filter1).validate(authRequestDTO, identityData, properties);

        validator.validateAuthFilters(authRequestDTO, identityData, properties);

        verify(filter2, never()).validate(authRequestDTO, identityData, properties);
    }
}


