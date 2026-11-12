package io.mosip.authentication.service.kyc.impl;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.repository.IdentityBindingCertificateRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class IdentityKeyBindingServiceImplTest {

    @Mock
    IDAMappingConfig idMappingConfig;

    @Mock
    IdentityBindingCertificateRepository bindingCertificateRepo;

    @Mock
    IdAuthSecurityManager securityManager;

    @InjectMocks
    IdentityKeyBindingServiceImpl identityKeyBindingServiceImpl;

    Map<String,Object> pubblicKeyMap;

    @BeforeEach
    public void initialize() {
        pubblicKeyMap = new HashMap<>();
        pubblicKeyMap.put("n", "isAXe1AStinOg3KSCyTDAvu38KRS7ZmKv3Etmt7lSy3SPEg1jOqycdpL4YfFf2uh4rrUEMwsizyIlvWrN6C_ytEx8Non6noXnYfuuePRvL6kaTGdd_lbrC7eh1FI2c2cPzWRTq-CMBCSAdxmjD6PIqaVk5WtliU4qt27F5xfo7lG8lMlREgLb7u0HB9W7B8PjxvWmZ6cDle6eSnb1zOxAAFzB-GbGhRpPF-6ki25mdUrWJGlEkXGSCW1SohSM3YKPJW_xY6_520XdSeHFS9X84f6BXEz_fYTQcBPiNKaxObRkqZ-24PnRzy5vOytjeEnwusenBUHtri4aj1rKkTmIQ");
        pubblicKeyMap.put("e", "AQAB");
        pubblicKeyMap.put("kid", "zcbgDyrQdhwLlaEPW_JeKTE5CiUCMLdDvftRC5Y8h8U");
        pubblicKeyMap.put("alg", "RS256");
        pubblicKeyMap.put("exp", "exp");
    }



    @Test
    public void isPublicKeyBindedWithValidDetails_thenPass() throws IdAuthenticationBusinessException {

        Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("idVidHash");
        Mockito.when(bindingCertificateRepo.countPublicKeysByIdHash(Mockito.anyString(),Mockito.any())).thenReturn(1);

        boolean flag=identityKeyBindingServiceImpl.isPublicKeyBinded("idVid", pubblicKeyMap, "vid");
        Assertions.assertFalse(flag);

    }

    @Test
    public void createAndSaveKeyBindingCertificateWithValidDetails_thenPass() throws CertificateEncodingException, IdAuthenticationBusinessException {

        ReflectionTestUtils.setField(identityKeyBindingServiceImpl,"defaultLangCode","eng");
        IdentityKeyBindingDTO identityKeyBindingDTO=new IdentityKeyBindingDTO();
        identityKeyBindingDTO.setPublicKeyJWK(pubblicKeyMap);

        IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO=new IdentityKeyBindingRequestDTO();
        identityKeyBindingRequestDTO.setIdentityKeyBinding(identityKeyBindingDTO);

        Map<String, List<IdentityInfoDTO>> identityInfo=new HashMap<>();
        List<IdentityInfoDTO> identityInfoDTOList=new ArrayList<>();
        IdentityInfoDTO identityInfoDTO=new IdentityInfoDTO();
        identityInfoDTO.setLanguage("eng");
        identityInfoDTO.setValue("value");
        identityInfoDTOList.add(identityInfoDTO);
        identityInfo.put("name",identityInfoDTOList);

        Map.Entry<String, String> certificateEntry=Map.entry("certThumbprint","certificateData");

        Mockito.when(securityManager.generateKeyBindingCertificate(Mockito.any(),Mockito.any())).thenReturn(certificateEntry);
        Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("idVidHash");
        List<String> names=new ArrayList<>();
        names.add("name");
        Mockito.when(idMappingConfig.getName()).thenReturn(names);

        identityKeyBindingServiceImpl.createAndSaveKeyBindingCertificate(identityKeyBindingRequestDTO,identityInfo,"token","partnerId");

    }

    @Test
    public void createAndSaveKeyBindingCertificateWithInValidIdentityName_thenFail() throws CertificateEncodingException, IdAuthenticationBusinessException {

        ReflectionTestUtils.setField(identityKeyBindingServiceImpl,"defaultLangCode","eng");
        IdentityKeyBindingDTO identityKeyBindingDTO=new IdentityKeyBindingDTO();
        identityKeyBindingDTO.setPublicKeyJWK(pubblicKeyMap);

        IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO=new IdentityKeyBindingRequestDTO();
        identityKeyBindingRequestDTO.setIdentityKeyBinding(identityKeyBindingDTO);

        Map<String, List<IdentityInfoDTO>> identityInfo=new HashMap<>();
        List<IdentityInfoDTO> identityInfoDTOList=new ArrayList<>();
        IdentityInfoDTO identityInfoDTO=new IdentityInfoDTO();
        identityInfoDTO.setLanguage("eng");
        identityInfoDTO.setValue("value");
        identityInfoDTOList.add(identityInfoDTO);
        identityInfo.put("name",identityInfoDTOList);

        Map.Entry<String, String> certificateEntry=Map.entry("certThumbprint","certificateData");

        Mockito.when(securityManager.generateKeyBindingCertificate(Mockito.any(),Mockito.any())).thenReturn(certificateEntry);
        Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("idVidHash");
        List<String> names=new ArrayList<>();
        names.add("name");

        try{
            identityKeyBindingServiceImpl.createAndSaveKeyBindingCertificate(identityKeyBindingRequestDTO,identityInfo,"token","partnerId");
            Assertions.fail();
        }catch (IdAuthenticationBusinessException e){
            Assertions.assertEquals("IDA-IKB-004",e.getErrorCode());
        }
    }

    @Test
    public void createAndSaveKeyBindingCertificateWithInValidCertificateEntry_thenFail() throws CertificateEncodingException, IdAuthenticationBusinessException {

        ReflectionTestUtils.setField(identityKeyBindingServiceImpl,"defaultLangCode","eng");
        IdentityKeyBindingDTO identityKeyBindingDTO=new IdentityKeyBindingDTO();
        identityKeyBindingDTO.setPublicKeyJWK(pubblicKeyMap);

        IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO=new IdentityKeyBindingRequestDTO();
        identityKeyBindingRequestDTO.setIdentityKeyBinding(identityKeyBindingDTO);

        Map<String, List<IdentityInfoDTO>> identityInfo=new HashMap<>();
        List<IdentityInfoDTO> identityInfoDTOList=new ArrayList<>();
        IdentityInfoDTO identityInfoDTO=new IdentityInfoDTO();
        identityInfoDTO.setLanguage("eng");
        identityInfoDTO.setValue("value");
        identityInfoDTOList.add(identityInfoDTO);
        identityInfo.put("name",identityInfoDTOList);

        Mockito.when(securityManager.generateKeyBindingCertificate(Mockito.any(),Mockito.any())).thenThrow(CertificateEncodingException.class);
        Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("idVidHash");
        List<String> names=new ArrayList<>();
        names.add("name");
        Mockito.when(idMappingConfig.getName()).thenReturn(names);

        try{
            identityKeyBindingServiceImpl.createAndSaveKeyBindingCertificate(identityKeyBindingRequestDTO,identityInfo,"token","partnerId");
            Assertions.fail();
        }catch (IdAuthenticationBusinessException e){
            Assertions.assertEquals("IDA-IKB-005",e.getErrorCode());
        }
    }
}
