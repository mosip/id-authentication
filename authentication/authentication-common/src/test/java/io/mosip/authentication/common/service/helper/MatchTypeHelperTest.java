package io.mosip.authentication.common.service.helper;

import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 @author Kamesh Shekhar Prasad
 */

@RunWith(SpringRunner.class)
public class MatchTypeHelperTest {

    @InjectMocks
    private MatchTypeHelper matchTypeHelper;

    @Mock
    private EntityInfoUtil entityInfoUtil;

    @Test
    public void getEntityInfoTest1() throws Throwable {
        Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
        List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
        IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
        identityInfoDTO.setValue("test@test.com");
        identityInfoList.add(identityInfoDTO);
        demoEntity.put("phoneNumber", identityInfoList);

        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        AuthType demoAuthType = DemoAuthType.DYNAMIC;
        Map<String, Object> matchProperties = null;
        MatchInput matchInput = new MatchInput(demoAuthType, BioMatchType.FACE.getIdMapping().getIdname(), BioMatchType.FACE,
                MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, null);
        EntityValueFetcher entityValueFetcher = null;
        MatchType matchType = BioMatchType.FACE;
        MatchingStrategy strategy = null;
        Map<String, String> entityInfo = new HashMap<>();
        entityInfo.put("1", "a");
        entityInfo.put("2", "b");
        entityInfo.put("3", "c");
        Mockito.when(entityInfoUtil.getIdEntityInfoMap(matchType, demoEntity, matchInput.getLanguage(),
                matchType.getIdMapping().getIdname())).thenReturn(entityInfo);

        ReflectionTestUtils.invokeMethod(matchTypeHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
                matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void TestgetEntityInfo() throws Throwable {
        Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
        List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
        IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
        identityInfoDTO.setValue("test@test.com");
        identityInfoList.add(identityInfoDTO);
        demoEntity.put("phoneNumber", identityInfoList);
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        AuthType demoAuthType = DemoAuthType.DYNAMIC;
        Map<String, Object> matchProperties = null;
        MatchInput matchInput = new MatchInput(demoAuthType, DemoMatchType.PHONE.getIdMapping().getIdname(), DemoMatchType.PHONE,
                MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, null);
        EntityValueFetcher entityValueFetcher = null;
        MatchType matchType = DemoMatchType.PHONE;
        MatchingStrategy strategy = null;
        try {
            ReflectionTestUtils.invokeMethod(matchTypeHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
                    matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
        } catch (UndeclaredThrowableException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void TestBiogetEntityInfo() throws Throwable {
        Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
        List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
        IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
        identityInfoDTO.setValue("test@test.com");
        identityInfoList.add(identityInfoDTO);
        demoEntity.put("phoneNumber", identityInfoList);
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        AuthType demoAuthType = DemoAuthType.DYNAMIC;
        Map<String, Object> matchProperties = null;
        MatchInput matchInput = new MatchInput(demoAuthType, BioMatchType.FACE.getIdMapping().getIdname(), BioMatchType.FACE,
                MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, null);
        EntityValueFetcher entityValueFetcher = null;

        MatchType matchType = BioMatchType.FACE;
        MatchingStrategy strategy = null;

        Map<String, String> entityInfo = new HashMap<>();
        Mockito.when(entityInfoUtil.getIdEntityInfoMap(matchType, demoEntity, matchInput.getLanguage(),
                matchType.getIdMapping().getIdname())).thenReturn(entityInfo);

        try {
            ReflectionTestUtils.invokeMethod(matchTypeHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
                    matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
        } catch (UndeclaredThrowableException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void TestgetEntityInfowithBiowithLanguage() throws Throwable {
        Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
        List<IdentityInfoDTO> identityInfoList = new ArrayList<>();
        IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
        identityInfoDTO.setValue("test@test.com");
        identityInfoList.add(identityInfoDTO);
        demoEntity.put("phoneNumber", identityInfoList);
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        AuthType demoAuthType = DemoAuthType.PERSONAL_IDENTITY;
        Map<String, Object> matchProperties = null;
        MatchInput matchInput = new MatchInput(demoAuthType, DemoMatchType.PHONE.getIdMapping().getIdname(), DemoMatchType.PHONE,
                MatchingStrategyType.PARTIAL.getType(), 60, matchProperties, "fra");
        EntityValueFetcher entityValueFetcher = null;
        MatchType matchType = DemoMatchType.PHONE;
        MatchingStrategy strategy = null;
        try {
            ReflectionTestUtils.invokeMethod(matchTypeHelper, "getEntityInfo", demoEntity, "426789089018", authRequestDTO,
                    matchInput, entityValueFetcher, matchType, strategy, matchType.getIdMapping().getIdname(), "426789089018");
        } catch (UndeclaredThrowableException e) {
            throw e.getCause();
        }
    }
}
