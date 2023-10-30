package io.mosip.authentication.service.kyc.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.OIDCClientData;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.repository.OIDCClientDataRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
@Import(EnvUtil.class)
public class ExchangeDataAttributesUtilTest {
    
    @Autowired
	EnvUtil env;

    @Mock
    private IdInfoHelper idInfoHelper;

    @Mock
    private OIDCClientDataRepository oidcClientDataRepo; 

    @InjectMocks
    private ExchangeDataAttributesUtil exchangeDataAttributesUtil;

    @Before
	public void before() {
		//
	}


    @Test
    public void mapConsentedAttributesToIdSchemaAttributesTest() throws IdAuthenticationBusinessException {
        List<String> consentAttributes = Arrays.asList("name", "gender", "dob", "address");
        List<String> policyAttributes = Arrays.asList("name", "gender", "dob", "address", "picture", "individual_id");
        Set<String> exFilterAttributes = Set.of("fullname", "gender", "dob", "address");

        Mockito.when(idInfoHelper.getIdentityAttributesForIdName("name")).thenReturn(Arrays.asList("fullname"));
        Mockito.when(idInfoHelper.getIdentityAttributesForIdName("gender")).thenReturn(Arrays.asList("gender"));
        Mockito.when(idInfoHelper.getIdentityAttributesForIdName("dob")).thenReturn(Arrays.asList("dob"));
        Mockito.when(idInfoHelper.getIdentityAttributesForIdName("address")).thenReturn(Arrays.asList("address"));

        ReflectionTestUtils.setField(exchangeDataAttributesUtil, "consentedIndividualIdAttributeName", "individual_id");
        Set<String> filterAttributes = new HashSet<>();
        exchangeDataAttributesUtil.mapConsentedAttributesToIdSchemaAttributes(consentAttributes, filterAttributes, policyAttributes);
        assertEquals(exFilterAttributes, filterAttributes);
    }

    @Test
    public void mapConsentedAttributesToIdSchemaAttributesNoIndividualIdTest() throws IdAuthenticationBusinessException {
        List<String> consentAttributes = new ArrayList<>();
        consentAttributes.add("name");
        consentAttributes.add("gender");
        consentAttributes.add("dob");
        consentAttributes.add("address");
        consentAttributes.add("individual_id");
        
        List<String> policyAttributes = Arrays.asList("name", "gender", "dob", "address", "picture");
        List<String> exConsentAttributes = Arrays.asList("name", "gender", "dob", "address");
        
        ReflectionTestUtils.setField(exchangeDataAttributesUtil, "consentedIndividualIdAttributeName", "individual_id");
        Set<String> filterAttributes = new HashSet<>();
        exchangeDataAttributesUtil.mapConsentedAttributesToIdSchemaAttributes(consentAttributes, filterAttributes, policyAttributes);
        assertEquals(consentAttributes, exConsentAttributes);
    }

    @Test
    public void filterByPolicyAllowedAttributesTest() {
        List<String> policyAttributes = Arrays.asList("name", "gender", "dob", "address", "picture", "individual_id");
        Set<String> filterAttributes = Set.of("name", "gender", "dob", "address");

        Set<String> resFilterAttributes = exchangeDataAttributesUtil.filterByPolicyAllowedAttributes(filterAttributes, policyAttributes);
        assertEquals(filterAttributes, resFilterAttributes);
    }

    @Test
    public void getKycExchangeResponseTimeTest() {
        BaseRequestDTO authRequestDTO = new BaseRequestDTO(); 
        authRequestDTO.setRequestTime("2023-10-19T12:35:57.835Z");
        String resValue = exchangeDataAttributesUtil.getKycExchangeResponseTime(authRequestDTO);
        assertNotNull(resValue);
    } 
    
    @Test
    public void filterAllowedUserClaimsTest() {
        List<String> consentAttributes = Arrays.asList("name", "gender", "dob", "address");
        String oidcClientId = "sampleOidcClientId";
        OIDCClientData clientData = new OIDCClientData();
        clientData.setUserClaims(new String [] {"name","gender","dob","address"});

        Mockito.when(oidcClientDataRepo.findByClientId(oidcClientId)).thenReturn(Optional.of(clientData));
        List<String> resAttributes = exchangeDataAttributesUtil.filterAllowedUserClaims(oidcClientId, consentAttributes);
        assertEquals(consentAttributes, resAttributes);
    }

    @Test
    public void filterAllowedUserClaimsNoConsentAttributesTest() {
        List<String> exAttributes = Arrays.asList("name", "gender", "dob");
        String oidcClientId = "sampleOidcClientId";
        OIDCClientData clientData = new OIDCClientData();
        clientData.setUserClaims(new String [] {"name","gender","dob"});

        Mockito.when(oidcClientDataRepo.findByClientId(oidcClientId)).thenReturn(Optional.of(clientData));
        List<String> resAttributes = exchangeDataAttributesUtil.filterAllowedUserClaims(oidcClientId, Collections.emptyList());
        assertEquals(exAttributes, resAttributes);
    }
}
