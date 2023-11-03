package io.mosip.authentication.common.service.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
public class KeyBindedTokenMatcherUtilTest {

    @Mock
    KeymanagerUtil keymanagerUtil;
    @InjectMocks
    KeyBindedTokenMatcherUtil keyBindedTokenMatcherUtil;


    @Test
    public void matchTest() throws IdAuthenticationBusinessException {
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "JWT_CONST", "jwt");
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "INDIVIDUAL_ID", "individualId");
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "TYPE", "type");
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "FORMAT", "format");
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "TOKEN", "token");
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "X5t_HEADER", "x5t_header");

        Map<String, Object> properties =new HashMap<>();
        Map<String, String> bindingCertificates =new HashMap<>();
        Map<String, String> input =new HashMap<>();

        keyBindedTokenMatcherUtil.match(input, bindingCertificates, properties);
    }
}
