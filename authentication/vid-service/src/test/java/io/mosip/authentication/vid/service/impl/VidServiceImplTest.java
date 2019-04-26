/**
 * 
 */
package io.mosip.authentication.vid.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.VIDEntity;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.repository.VIDRepository;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idgenerator.vid.impl.VidGeneratorImpl;

/**
 * @author Dinesh Karuppiah.T
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class VidServiceImplTest {

	@InjectMocks
	private VIDServiceImpl vidServiceImpl;

	@Mock
	private IdService<AutnTxn> idAuthService;

	@Mock
	private AuditHelper auditHelper;

	@Autowired
	Environment environment;

	@Mock
	private VIDRepository vidRepository;

	@Mock
	private VidGeneratorImpl vidGenerator;

	@Before
	public void before() {
		ReflectionTestUtils.setField(vidServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(vidServiceImpl, "env", environment);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidgenerateVID() throws IdAuthenticationBusinessException {
		Map<String, Object> value = new HashMap<>();
		Mockito.when(idAuthService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		vidServiceImpl.generateVID("274390482564");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestVidNotExiststhrowsException() throws IdAuthenticationBusinessException {
		getVidData();
		Mockito.when(vidRepository.save(Mockito.any())).thenThrow(new DataAccessException("Data Save Failed") {
		});
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.vid.validity.hours", "24");
		ReflectionTestUtils.setField(vidServiceImpl, "env", mockenv);
		vidServiceImpl.generateVID("274390482564");
	}

	@Test
	public void TestNewVidGeneration() throws IdAuthenticationBusinessException {
		getVidData();
		VIDEntity vidEntity = new VIDEntity();
		String vid = "247334310780728918141754192454591343";
		vidEntity.setId(vid);
		vidEntity.setActive(true);
		Mockito.when(vidRepository.save(Mockito.any())).thenReturn((Object) vidEntity);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.vid.validity.hours", "24");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(vidServiceImpl, "env", mockenv);
		VIDResponseDTO vidresponse = vidServiceImpl.generateVID("274390482564");
		assertTrue(vidresponse.getErrors().isEmpty());
	}

	@Test
	public void TestVidRegenerationwithFutureDate() throws IdAuthenticationBusinessException {
		getVidData();
		List<VIDEntity> vidEntityList = new ArrayList<>();
		String vid = "247334310780728918141754192454591343";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(true);
		vidEntity.setId(vid);
		vidEntityList.add(vidEntity);
		Mockito.when(vidRepository.findByUIN(Mockito.anyString(), Mockito.any())).thenReturn(vidEntityList);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.vid.validity.hours", "24");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(vidServiceImpl, "env", mockenv);
		LocalDateTime parseToLocalDateTime = DateUtils.parseUTCToLocalDateTime(
				Instant.now().plus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530")).toString(),
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		vidEntity.setExpiryDate(parseToLocalDateTime);
		Mockito.when(vidGenerator.generateId()).thenReturn(vid);
		VIDResponseDTO vidResponseDTO = vidServiceImpl.generateVID("274390482564");
		assertFalse(vidResponseDTO.getErrors().isEmpty());
	}

	@Test
	public void TestVidRegenerationwithPreviousDate() throws IdAuthenticationBusinessException {
		getVidData();
		List<VIDEntity> vidEntityList = new ArrayList<>();
		String vid = "247334310780728918141754192454591343";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(true);
		vidEntity.setId(vid);
		vidEntityList.add(vidEntity);
		Mockito.when(vidRepository.findByUIN(Mockito.anyString(), Mockito.any())).thenReturn(vidEntityList);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.vid.validity.hours", "24");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(vidServiceImpl, "env", mockenv);
		LocalDateTime parseToLocalDateTime = DateUtils.parseUTCToLocalDateTime(
				Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530")).toString(),
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		vidEntity.setExpiryDate(parseToLocalDateTime);
		Mockito.when(vidGenerator.generateId()).thenReturn(vid);
		VIDResponseDTO vidResponseDTO = vidServiceImpl.generateVID("274390482564");
		assertTrue(vidResponseDTO.getErrors().isEmpty());
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestVidGenerationFailed() throws IdAuthenticationBusinessException {
		getVidData();
		List<VIDEntity> vidEntityList = new ArrayList<>();
		String vid = "247334310780728918141754192454591343";
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setActive(true);
		vidEntity.setId(vid);
		vidEntityList.add(vidEntity);
		Mockito.when(vidRepository.findByUIN(Mockito.anyString(), Mockito.any())).thenReturn(vidEntityList);
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("mosip.vid.validity.hours", "24");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(vidServiceImpl, "env", mockenv);
		LocalDateTime parseToLocalDateTime = DateUtils.parseUTCToLocalDateTime(
				Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530")).toString(),
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		vidEntity.setExpiryDate(parseToLocalDateTime);
		Mockito.when(vidGenerator.generateId()).thenReturn(vid);
		Mockito.when(vidRepository.save(Mockito.any())).thenThrow(new DataAccessException("Data Save Failed") {
		});
		vidServiceImpl.generateVID("274390482564");
	}

	private void getVidData() throws IdAuthenticationBusinessException {
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(idAuthService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		List<VIDEntity> vidEntityList = new ArrayList<>();
		Mockito.when(vidRepository.findByUIN(Mockito.any(), Mockito.any())).thenReturn(vidEntityList);
		String vid = "247334310780728918141754192454591343";
		Mockito.when(vidGenerator.generateId()).thenReturn(vid);
	}

}
