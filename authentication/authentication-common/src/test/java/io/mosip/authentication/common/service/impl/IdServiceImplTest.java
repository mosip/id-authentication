package io.mosip.authentication.common.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.JDBCConnectionException;
import org.junit.Ignore;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;

@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebMvcTest
@RunWith(SpringRunner.class)
public class IdServiceImplTest {

    @InjectMocks
    private IdServiceImpl idServiceImpl;

    @Mock
    private IdAuthSecurityManager securityManager;

    @Mock
    private IdentityCacheRepository identityRepo;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private AutnTxnRepository autntxnrepository;

    @Ignore
    @Test
    public void getIdentityTest1() throws IdAuthenticationBusinessException, IOException {
        String uin = "12312312";
        Boolean isBio = true;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        filterAttributes.add("11");
        filterAttributes.add("22");
        filterAttributes.add("33");
        Map<String, String> demoDataMap = new HashMap<String, String>();
        demoDataMap.put("1", "11");
        demoDataMap.put("2", "22");
        demoDataMap.put("3", "33");

        Map<String, String> bioDataMap = new HashMap<String, String>();
        bioDataMap.put("1", "11");
        bioDataMap.put("2", "22");
        bioDataMap.put("3", "33");
        IdentityEntity entity = getEntity();
        Mockito.when(mapper.readValue(entity.getDemographicData(), Map.class)).thenReturn(demoDataMap);
        Mockito.when(mapper.readValue(entity.getBiometricData(), Map.class)).thenReturn(bioDataMap);
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(true);

        Mockito.when(identityRepo.getOne("12")).thenReturn(entity);
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test
    public void getIdentityTest2() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.VID;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(true);
        byte[] demographicData = {};
        LocalDateTime ltime= LocalDateTime.now();
        Object[] data = new Object[]{1, demographicData, null, null, 1};

        Mockito.when(identityRepo.findDemoDataById("12")).thenReturn(Collections.singletonList(data));
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException1() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = true;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.doThrow(IdAuthenticationBusinessException.class).when(securityManager).hash(uin);
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Ignore
    @Test
    public void getIdentityTest3() throws IdAuthenticationBusinessException, IOException {
        String uin = "12312312";
        Boolean isBio = true;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        Map<String, String> demoDataMap = new HashMap<String, String>();
        demoDataMap.put("1", "11");
        demoDataMap.put("2", "22");
        demoDataMap.put("3", "33");

        Map<String, String> bioDataMap = new HashMap<String, String>();
        bioDataMap.put("1", "11");
        bioDataMap.put("2", "22");
        bioDataMap.put("3", "33");
        IdentityEntity entity = getEntity();
        Mockito.when(mapper.readValue(entity.getDemographicData(), Map.class)).thenReturn(demoDataMap);
        Mockito.when(mapper.readValue(entity.getBiometricData(), Map.class)).thenReturn(bioDataMap);
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(true);

        Mockito.when(identityRepo.getOne("12")).thenReturn(entity);
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException2() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(true);
        byte[] demographicData = {};
        LocalDateTime ltime= LocalDateTime.now();
        Object[] data = new Object[]{1, demographicData, "2018-12-30T19:34:50.63", 1, 1};

        System.out.println("time="+ LocalDateTime.parse(String.valueOf(data[2])) );
        Mockito.when(identityRepo.findDemoDataById("12")).thenReturn(Collections.singletonList(data));
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException3() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(false);
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException4() throws IdAuthenticationBusinessException, IOException {
        String uin = "12312312";
        Boolean isBio = true;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        filterAttributes.add("11");
        filterAttributes.add("22");
        filterAttributes.add("33");
        IdentityEntity entity = getEntity();
        Mockito.doThrow(IOException.class).when(mapper).readValue(entity.getDemographicData(), Map.class);
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(true);
        Mockito.when(identityRepo.getOne("12")).thenReturn(entity);
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException5() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.VID;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(true);
        byte[] demographicData = {};
        LocalDateTime ltime= LocalDateTime.now();
        Object[] data = new Object[]{1, demographicData, "2018-12-30T19:34:50.63", 1, 1};

        System.out.println("time="+ LocalDateTime.parse(String.valueOf(data[2])) );
        Mockito.when(identityRepo.findDemoDataById("12")).thenReturn(Collections.singletonList(data));
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Ignore
    @Test
    public void processIdTypeTest() throws IdAuthenticationBusinessException, IOException {
//        String idvIdType, String idvId, boolean isBio, boolean markVidConsumed, Set<String> filterAttributes
        String idvId = "12312312";
        Boolean isBio = true;
        Boolean markVidConsumed = true;
        Set<String> filterAttributes = new HashSet<String>();
        filterAttributes.add("11");
        filterAttributes.add("22");
        filterAttributes.add("33");
        Map<String, String> demoDataMap = new HashMap<String, String>();
        demoDataMap.put("1", "11");
        demoDataMap.put("2", "22");
        demoDataMap.put("3", "33");

        Map<String, String> bioDataMap = new HashMap<String, String>();
        bioDataMap.put("1", "11");
        bioDataMap.put("2", "22");
        bioDataMap.put("3", "33");
        IdentityEntity entity = getEntity();
        Mockito.when(mapper.readValue(entity.getDemographicData(), Map.class)).thenReturn(demoDataMap);
        Mockito.when(mapper.readValue(entity.getBiometricData(), Map.class)).thenReturn(bioDataMap);
        Mockito.when(securityManager.hash(idvId)).thenReturn("12");
        Mockito.when(identityRepo.existsById("12")).thenReturn(true);
        Mockito.when(identityRepo.getOne("12")).thenReturn(entity);

        String idvIdType = "VID";
        idServiceImpl.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);

        markVidConsumed=false;
        idServiceImpl.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);

        idvIdType = "UIN";
        idServiceImpl.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);

        idvIdType = "";
        idServiceImpl.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void processIdTypeExceptionTest1() throws IdAuthenticationBusinessException, IOException {
        String idvIdType = "UIN";
        String idvId = "12312312";
        Boolean isBio = true;
        Boolean markVidConsumed = true;
        Set<String> filterAttributes = new HashSet<String>();
        idServiceImpl.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void processIdTypeExceptionTest2() throws IdAuthenticationBusinessException, IOException {
        String idvIdType = "VID";
        String idvId = "12312312";
        Boolean isBio = true;
        Boolean markVidConsumed = true;
        Set<String> filterAttributes = new HashSet<String>();
        idServiceImpl.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void processIdTypeExceptionTest3() throws IdAuthenticationBusinessException, IOException {
//        String idvIdType, String idvId, boolean isBio, boolean markVidConsumed, Set<String> filterAttributes
        String idvId = "12";
        Boolean isBio = true;
        Boolean markVidConsumed = true;
        Set<String> filterAttributes = new HashSet<String>();
        IdentityEntity entity = getEntity();
        entity.setTransactionLimit(5);
        Mockito.when(securityManager.hash(idvId)).thenReturn("11");
        Mockito.when(identityRepo.existsById("11")).thenReturn(true);
        Mockito.when(identityRepo.getOne("11")).thenReturn(entity);
        IdServiceImpl idServiceSpy = Mockito.spy(idServiceImpl);
        Mockito.doReturn(null).when(idServiceSpy).getIdByVid(idvId, isBio, filterAttributes);
        String idvIdType = "VID";
        Mockito.doThrow(JDBCConnectionException.class).when(identityRepo).deleteById("11");
        idServiceSpy.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);
    }

    @Test
    public void saveAutnTxnTest() throws IdAuthenticationBusinessException {
        AutnTxn autnTxn = new AutnTxn();
        idServiceImpl.saveAutnTxn(autnTxn);
    }

    @Test
    public void getDemoDataTest(){
        Map<String, Object> identity = new HashMap<>();
        idServiceImpl.getDemoData(identity);
    }

    @Test
    public void getTokenTest(){
        Map<String, Object> idResDTO = new HashMap<>();
        idResDTO.put("TOKEN", "23242");
        idServiceImpl.getToken(idResDTO);
    }

    @Test
    public void updateVidStatusTest() throws IdAuthenticationBusinessException {
        String vid = "213131";
        Mockito.when(identityRepo.existsById(vid)).thenReturn(true);
        IdentityEntity entity = getEntity();
        entity.setTransactionLimit(5);
        Mockito.when(securityManager.hash(vid)).thenReturn(vid);
        Mockito.when(identityRepo.existsById(vid)).thenReturn(true);
        Mockito.when(identityRepo.getOne(vid)).thenReturn(entity);
        ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);
    }

    private IdentityEntity getEntity(){
        IdentityEntity entity = new IdentityEntity();
        LocalDateTime time = LocalDateTime.now();
        entity.setExpiryTimestamp(time);
        byte[] bioData = {};
        entity.setBiometricData(bioData);
        return entity;
    }
}
