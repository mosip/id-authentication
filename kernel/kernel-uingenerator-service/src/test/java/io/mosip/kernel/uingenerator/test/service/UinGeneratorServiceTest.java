package io.mosip.kernel.uingenerator.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.exception.UinNotIssuedException;
import io.mosip.kernel.uingenerator.exception.UinStatusNotFoundException;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.impl.UinGeneratorServiceImpl;
import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;
import io.vertx.core.json.JsonObject;

/**
 * @author Megha Tanga
 * 
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UinGeneratorTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class UinGeneratorServiceTest {

	@Autowired
	private UinGeneratorServiceImpl uinGeneratorServiceImpl;

	@MockBean
	private UinRepository uinRepository;

	@Test(expected = UinNotFoundException.class)
	public void getUinNotFoundTest() {

		Mockito.when(uinRepository.findFirstByStatus("UNUSED")).thenReturn(null);
		uinGeneratorServiceImpl.getUin();
	}

	@Test
	public void getUinTest() {
		UinEntity entity = new UinEntity("9723157067", "ISSUED");
		Mockito.when(uinRepository.findFirstByStatus("UNUSED")).thenReturn(entity);
		Mockito.when(uinRepository.save(Mockito.any())).thenReturn(entity);
		uinGeneratorServiceImpl.getUin();

	}

	@Test(expected = UinNotIssuedException.class)
	public void updateUinStatusNotFoundIssuedTest() {
		UinEntity entity = new UinEntity("9723157067", "UNUSED");
		JsonObject uin = new JsonObject();
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}

	@Test(expected = UinNotFoundException.class)
	public void updateUinStatusUinNotFoundTest() {
		UinEntity entity = new UinEntity();
		JsonObject uin = new JsonObject();
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(null);
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}

	@Test(expected = UinStatusNotFoundException.class)
	public void updateUinStatusNotFoundStatusTest() {
		UinEntity entity = new UinEntity("9723157067", "ISSUED");
		JsonObject uin = new JsonObject();
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}

	@Test
	public void updateUinStatusASSIGNEDTest() {
		String content = "{ \"uin\" : \"9723157067\", \"status\" : \"ASSIGNED\" }";
		JsonObject uin = new JsonObject(content);
		UinEntity entity = new UinEntity("9723157067", "ISSUED");
		UinEntity givEntity = new UinEntity("9723157067", "ASSIGNED");
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		Mockito.when(uinRepository.save(Mockito.any())).thenReturn(givEntity);
		//ReflectionTestUtils.setField(uinGeneratorServiceImpl, "assigned", "ASSIGNED");
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}

	@Test
	public void updateUinStatusUNASSIGNEDTest() {
		String content = "{ \"uin\" : \"9723157067\", \"status\" : \"UNASSIGNED\" }";
		JsonObject uin = new JsonObject(content);
		UinEntity entity = new UinEntity("9723157067", "ISSUED");
		UinEntity givEntity = new UinEntity("9723157067", "UNASSIGNED");
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		Mockito.when(uinRepository.save(Mockito.any())).thenReturn(givEntity);
		//ReflectionTestUtils.setField(uinGeneratorServiceImpl, "unassigned", "UNASSIGNED");
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}
}