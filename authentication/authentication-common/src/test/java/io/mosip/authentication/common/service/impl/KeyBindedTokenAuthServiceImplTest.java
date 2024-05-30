package io.mosip.authentication.common.service.impl;


import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.repository.IdentityBindingCertificateRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
public class KeyBindedTokenAuthServiceImplTest {

    @InjectMocks
    IdInfoHelper idInfoHelper;

    /** The id info helper. */
    @Mock
    MatchInputBuilder matchInputBuilder;

    /** The ida mapping config. */
    @Mock
    IDAMappingConfig idaMappingConfig;

    @Mock
    IdAuthSecurityManager securityManager;

    @Mock
    IdentityBindingCertificateRepository identityBindingCertificateRepository;

    @InjectMocks
    KeyBindedTokenAuthServiceImpl keyBindedTokenAuthService;

    @Test
    public void authenticateTestWithValidDetails_thenPass() throws IdAuthenticationBusinessException {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();

        ReflectionTestUtils.setField(keyBindedTokenAuthService,"idInfoHelper",idInfoHelper);
        IdentityInfoDTO identityInfoDTO=new IdentityInfoDTO();
        identityInfoDTO.setValue("value");
        identityInfoDTO.setLanguage("end");
        List<IdentityInfoDTO> list=new ArrayList<>();
        list.add(identityInfoDTO);
        Map<String, List<IdentityInfoDTO>> idInfo= new HashedMap();
        idInfo.put("key",list);

        MatchOutput matchOutput=new MatchOutput(5,true,"fingerpring",null,"end","idName");

        matchOutput.setLanguage("end");
        List<MatchOutput> matchOutputList=new ArrayList<>();
        matchOutputList.add(matchOutput);
        keyBindedTokenAuthService.authenticate(authRequestDTO,"individualId",idInfo,"partnerId");

    }
}
