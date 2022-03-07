package io.mosip.authentication.common.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.hibernate.exception.JDBCConnectionException;
import org.junit.Assert;
import org.junit.Test;
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
import io.mosip.kernel.core.util.DateUtils;

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
		Object[] data = new Object[] { 1, demographicData, null, null, 1 };

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
		Object[] data = new Object[] { 1, demographicData, "2018-12-30T19:34:50.63", 1, 1 };

		System.out.println("time=" + LocalDateTime.parse(String.valueOf(data[2])));
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
		Object[] data = new Object[] { 1, demographicData, "2018-12-30T19:34:50.63", 1, 1 };

		System.out.println("time=" + LocalDateTime.parse(String.valueOf(data[2])));
		Mockito.when(identityRepo.findDemoDataById("12")).thenReturn(Collections.singletonList(data));
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
		IdentityEntity entity = getEntity();
		Mockito.when(mapper.readValue(entity.getDemographicData(), Map.class)).thenReturn(demoDataMap);
		Mockito.when(mapper.readValue(entity.getBiometricData(), Map.class)).thenReturn(bioDataMap);
		Mockito.when(securityManager.hash(idvId)).thenReturn("12");
		Mockito.when(identityRepo.existsById("12")).thenReturn(true);
		Mockito.when(identityRepo.getOne("12")).thenReturn(entity);

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

	@Test(expected = IdAuthenticationBusinessException.class)
	public void processIdTypeExceptionTest3() throws IdAuthenticationBusinessException, IOException {
		String idvId = "12";
		Boolean isBio = true;
		Boolean markVidConsumed = true;
		Set<String> filterAttributes = new HashSet<String>();
		Optional<IdentityEntity> entityOpt = Optional.of(getEntity());
		entityOpt.get().setTransactionLimit(0);
		Mockito.when(securityManager.hash(idvId)).thenReturn("11");
		Mockito.when(identityRepo.existsById("11")).thenReturn(true);
		Mockito.when(identityRepo.findById("11")).thenReturn(entityOpt);
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
		Optional<IdentityEntity> entityOpt = Optional.of(getEntity());
		entityOpt.get().setTransactionLimit(2);
		Mockito.when(securityManager.hash(vid)).thenReturn(vid);
		Mockito.when(identityRepo.existsById(vid)).thenReturn(true);
		Mockito.when(identityRepo.findById(vid)).thenReturn(entityOpt);
		ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);
		Integer result = 1;
		Assert.assertEquals(result, identityRepo.findById(vid).get().getTransactionLimit());
	}

	/**
	 * This class tests the updateVidStatusTest method when transaction limit is 0
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Test
	public void updateVidStatusTestTransLim0() throws IdAuthenticationBusinessException {
		String vid = "213131";
		Optional<IdentityEntity> entityOpt = Optional.of(getEntity());
		Mockito.when(securityManager.hash(vid)).thenReturn(vid);
		Mockito.when(identityRepo.existsById(vid)).thenReturn(true);
		entityOpt.get().setTransactionLimit(0);
		Mockito.when(identityRepo.findById(vid)).thenReturn(entityOpt);
		ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);
		Mockito.verify(identityRepo, Mockito.times(1)).deleteById(vid);
	}

	/**
	 * This class tests the updateVidStatusTest method when transaction limit is null
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Test
	public void updateVidStatusTestTrnsLimNull() throws IdAuthenticationBusinessException {
		String vid = "213131";
		Optional<IdentityEntity> entityOpt = Optional.of(getEntity());
		Mockito.when(securityManager.hash(vid)).thenReturn(vid);
		Mockito.when(identityRepo.existsById(vid)).thenReturn(true);
		entityOpt.get().setTransactionLimit(null);
		Mockito.when(identityRepo.findById(vid)).thenReturn(entityOpt);
		ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);
		Mockito.verify(identityRepo, Mockito.times(1)).existsById(vid);
	}

	/**
	 * This class tests the updateVidStatusTest method when transaction limit is 1
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Test
	public void updateVidStatusTestTrnsLim1() throws IdAuthenticationBusinessException {
		String vid = "213131";
		Optional<IdentityEntity> entityOpt = Optional.of(getEntity());
		Mockito.when(securityManager.hash(vid)).thenReturn(vid);
		Mockito.when(identityRepo.existsById(vid)).thenReturn(true);
		entityOpt.get().setTransactionLimit(1);
		Mockito.when(identityRepo.findById(vid)).thenReturn(entityOpt);
		ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);
		Mockito.verify(identityRepo, Mockito.times(1)).deleteById(vid);
	}

	/**
	 * This class tests the updateVidStatusTest method when transaction limit is -1
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Test
	public void updateVidStatusTestTrnsLim_1() throws IdAuthenticationBusinessException {
		String vid = "213131";
		Optional<IdentityEntity> entityOpt = Optional.of(getEntity());
		Mockito.when(securityManager.hash(vid)).thenReturn(vid);
		Mockito.when(identityRepo.existsById(vid)).thenReturn(true);
		entityOpt.get().setTransactionLimit(-1);
		Mockito.when(identityRepo.findById(vid)).thenReturn(entityOpt);
		ReflectionTestUtils.invokeMethod(idServiceImpl, "updateVIDstatus", vid);
		Mockito.verify(identityRepo, Mockito.times(1)).deleteById(vid);
	}

	private IdentityEntity getEntity() {
		IdentityEntity entity = new IdentityEntity();
		LocalDateTime time = DateUtils.getUTCCurrentDateTime().plus(10, ChronoUnit.MINUTES);
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
    public void testDecryptConfiguredAttributesDemo() {
    	String uin = "12312312";
		Map<String, String> demoDataMap = new HashMap<String, String>();
		demoDataMap.put("1", "11");
		demoDataMap.put("2", "22");
		demoDataMap.put("3", "33");
		Map<String, Object> map = ReflectionTestUtils.invokeMethod(idServiceImpl, "decryptConfiguredAttributes",uin,demoDataMap);
		assertTrue(map.isEmpty());
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