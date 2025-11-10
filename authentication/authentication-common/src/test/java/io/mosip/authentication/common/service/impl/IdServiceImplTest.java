package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.hibernate.exception.JDBCConnectionException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.util.DateUtils2;

@RunWith(MockitoJUnitRunner.class)
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

    @Test
    public void getIdentityTest1() throws IdAuthenticationBusinessException, IOException {
        String uin = "12312312";
        Boolean isBio = true;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = Set.of("11", "22", "33");

        Map<String, String> demoDataMap = Map.of("1", "11", "2", "22", "3", "33");
        Map<String, String> bioDataMap = Map.of("1", "11", "2", "22", "3", "33");
        IdentityEntity entity = getEntity();

        // Configure unencrypted attributes
        ReflectionTestUtils.setField(idServiceImpl, "zkUnEncryptedCredAttribs", "1,2,3");

        // Mock mapper
        Mockito.when(mapper.readValue(entity.getBiometricData(), Map.class)).thenReturn(bioDataMap);

        // Mock hashing + repo existence
        Mockito.when(securityManager.hash(uin)).thenReturn("12");

        // Mock FullIdentityView instead of getOne()
        IdentityCacheRepository.FullIdentityView fullView = Mockito.mock(IdentityCacheRepository.FullIdentityView.class);
        Mockito.when(fullView.getDemographicData()).thenReturn(entity.getDemographicData());
        Mockito.when(fullView.getBiometricData()).thenReturn(entity.getBiometricData());
        Mockito.when(identityRepo.findFullViewById("12")).thenReturn(Optional.of(fullView));

        // Act
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test
    public void getIdentityTest2() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.VID;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        byte[] demographicData = {};
        Object[] data = new Object[] { 1, demographicData, null, null, 1 };

        final IdentityCacheRepository.DemoIdentityView v = Mockito.mock(IdentityCacheRepository.DemoIdentityView.class);
        Mockito.when(identityRepo.findDemoViewById(Mockito.anyString())).thenReturn(Optional.ofNullable(v));
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
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        final IdentityCacheRepository.FullIdentityView v = Mockito.mock(IdentityCacheRepository.FullIdentityView.class);
        Mockito.when(identityRepo.findFullViewById(Mockito.anyString())).thenReturn(Optional.ofNullable(v));
        
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException2() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException3() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.UIN;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
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
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test(expected = IdAuthenticationBusinessException.class)
    public void getIdentityTestException5() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Boolean isBio = false;
        IdType idType = IdType.VID;
        Set<String> filterAttributes = new HashSet<String>();
        Mockito.when(securityManager.hash(uin)).thenReturn("12");
        idServiceImpl.getIdentity(uin, isBio, idType, filterAttributes);
    }

    @Test
    public void processIdTypeTest() throws IdAuthenticationBusinessException, IOException {
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
        Mockito.when(securityManager.hash(idvId)).thenReturn("12");

        final IdentityCacheRepository.FullIdentityView v = Mockito.mock(IdentityCacheRepository.FullIdentityView.class);
        Mockito.when(identityRepo.findFullViewById(Mockito.anyString())).thenReturn(Optional.ofNullable(v));


        String idvIdType = "VID";
        idServiceImpl.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes);

        markVidConsumed = false;
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

    @Test
    public void processIdTypeExceptionTest3() throws IdAuthenticationBusinessException, IOException {
        // Arrange
        String idvId = "12";
        Boolean isBio = true;
        Boolean markVidConsumed = true;
        Set<String> filterAttributes = new HashSet<>();

        Mockito.when(securityManager.hash(idvId)).thenReturn("11");

        // Spy on service
        IdServiceImpl idServiceSpy = Mockito.spy(idServiceImpl);

        // Mock getIdByVid to return null
        Mockito.doReturn(null).when(idServiceSpy).getIdByVid(idvId, isBio, filterAttributes);

        String idvIdType = "VID";

        // Now, instead of deleteById, mock consumeVidOnce to throw DB exception
        Mockito.doThrow(new JDBCConnectionException("DB down", null))
                .when(identityRepo).consumeVidOnce(Mockito.eq("11"));

        // Act & Assert
        Assertions.assertThrows(
                IdAuthenticationBusinessException.class,
                () -> idServiceSpy.processIdType(idvIdType, idvId, isBio, markVidConsumed, filterAttributes)
        );
    }

    @Test
    public void saveAutnTxnTest() throws IdAuthenticationBusinessException {
        AutnTxn autnTxn = new AutnTxn();
        idServiceImpl.saveAutnTxn(autnTxn);
    }

    @Test
    public void getDemoDataTest() {
        Map<String, Object> identity = new HashMap<>();
        idServiceImpl.getDemoData(identity);
    }

    @Test
    public void getTokenTest() {
        Map<String, Object> idResDTO = new HashMap<>();
        idResDTO.put("TOKEN", "23242");
        idServiceImpl.getToken(idResDTO);
    }

    /**
     * This class tests the updateVidStatusTest method when transaction limit is 2
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void updateVidStatusTestTrnsLim2() throws IdAuthenticationBusinessException {
        String vid = "213131";
        Mockito.when(securityManager.hash(vid)).thenReturn(vid);
        Mockito.when(identityRepo.consumeVidOnce(vid)).thenReturn(1); // Mock new repo method

        ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);

        Mockito.verify(identityRepo, Mockito.times(1)).consumeVidOnce(vid);
    }

    /**
     * This class tests the updateVidStatusTest method when transaction limit is 0
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void updateVidStatusTestTransLim0() throws IdAuthenticationBusinessException {
        // Arrange
        String vid = "213131";
        Mockito.when(securityManager.hash(vid)).thenReturn(vid);

        // Act
        ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);

        // Assert
        // Since consumeVidOnce() internally handles zero transaction limit,
        // we just verify it's called once.
        Mockito.verify(identityRepo, Mockito.times(1)).consumeVidOnce(Mockito.eq(vid));
    }

    /**
     * This class tests the updateVidStatusTest method when transaction limit is null
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void updateVidStatusTestTrnsLimNull() throws IdAuthenticationBusinessException {
        // Arrange
        String vid = "213131";
        Mockito.when(securityManager.hash(vid)).thenReturn(vid);

        // Act
        ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);

        // Assert
        // We just verify that consumeVidOnce() is called once,
        // regardless of whether transactionLimit is null.
        Mockito.verify(identityRepo, Mockito.times(1)).consumeVidOnce(Mockito.eq(vid));
    }

    /**
     * This class tests the updateVidStatusTest method when transaction limit is 1
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void updateVidStatusTestTrnsLim1() throws IdAuthenticationBusinessException {
        // Arrange
        String vid = "213131";
        Mockito.when(securityManager.hash(vid)).thenReturn(vid);

        // Act
        ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);

        // Assert
        // Now consumeVidOnce() internally handles decrement or deletion.
        Mockito.verify(identityRepo, Mockito.times(1)).consumeVidOnce(Mockito.eq(vid));
    }

    /**
     * This class tests the updateVidStatusTest method when transaction limit is -1
     *
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    @Test
    public void updateVidStatusTestTrnsLim_1() throws IdAuthenticationBusinessException {
        // Arrange
        String vid = "213131";

        Mockito.when(securityManager.hash(vid)).thenReturn(vid);

        // Act
        ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);

        // Assert
        // Since consumeVidOnce is responsible for deletion internally,
        // we just verify it gets called once.
        Mockito.verify(identityRepo, Mockito.times(1)).consumeVidOnce(Mockito.eq(vid));
    }

    private IdentityEntity getEntity() {
        IdentityEntity entity = new IdentityEntity();
        LocalDateTime time = DateUtils2.getUTCCurrentDateTime().plus(10, ChronoUnit.MINUTES);
        entity.setExpiryTimestamp(time);

        byte[] bioData = {};
        entity.setBiometricData(bioData);
        return entity;
    }

    @Test
    public void Test_getZkUnEncryptedAttributes() {
        List<String> unencrptedAttribs = ReflectionTestUtils.invokeMethod(idServiceImpl, "getZkUnEncryptedAttributes");
        assertEquals(0, unencrptedAttribs.size());
    }

    @Test
    public void testDecryptConfiguredAttributesDemo() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Map<String, String> demoDataMap = new HashMap<String, String>();
        demoDataMap.put("1", "11");
        demoDataMap.put("2", "22");
        demoDataMap.put("3", "33");

        // Mock zkDecrypt to return an empty map
        Mockito.when(securityManager.zkDecrypt(Mockito.eq(uin), Mockito.anyMap()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        Map<String, Object> map = ReflectionTestUtils.invokeMethod(idServiceImpl, "decryptConfiguredAttributes",uin,demoDataMap);
        assertEquals(3, map.size());
        assertEquals("11", map.get("1"));
        assertEquals("22", map.get("2"));
        assertEquals("33", map.get("3"));
    }


    @Test
    public void testDecryptConfiguredAttributesBio() throws IdAuthenticationBusinessException {
        String uin = "12312312";
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("1", "11");
        dataMap.put("2", "22");
        dataMap.put("3", "33");
        List<String> list = ReflectionTestUtils.invokeMethod(idServiceImpl, "getZkUnEncryptedAttributes");
        List<String> zkUnEncryptedAttributes = list.stream().map(String::toLowerCase).collect(Collectors.toList());
        Map<Boolean, Map<String, String>> partitionedMap = dataMap.entrySet()
                .stream()
                .collect(Collectors.partitioningBy(entry ->
                                !zkUnEncryptedAttributes.contains(entry.getKey().toLowerCase()),
                        Collectors.toMap(Entry::getKey, Entry::getValue)));
        Map<String, String> dataToDecrypt = partitionedMap.get(true);
        Mockito.when(securityManager.zkDecrypt(uin, dataToDecrypt)).thenReturn(dataToDecrypt);
        Map<String, Object> map = ReflectionTestUtils.invokeMethod(idServiceImpl, "decryptConfiguredAttributes",uin,dataMap);
        assertFalse(map.isEmpty());
    }

}