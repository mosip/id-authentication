package io.mosip.kernel.uingenerator.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import io.mosip.kernel.uingenerator.config.HibernateDaoConfig;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.exception.UinNotIssuedException;
import io.mosip.kernel.uingenerator.exception.UinStatusNotFoundException;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.UinService;

/**
 * @author Megha Tanga
 * @since 1.0.0
 * 
 */

@SpringBootTest
@TestPropertySource({ "classpath:application-test.properties", "classpath:bootstrap.properties" })
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HibernateDaoConfig.class, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UinGeneratorServiceTest {

	@Autowired
	private UinService uinGeneratorServiceImpl;

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
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}

	@Test(expected = UinNotFoundException.class)
	public void updateUinStatusUinNotFoundTest() {
		UinEntity entity = new UinEntity();
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(null);
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}

	@Test(expected = UinStatusNotFoundException.class)
	public void updateUinStatusNotFoundStatusTest() {
		UinEntity entity = new UinEntity("9723157067", "ISSUED");
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		uinGeneratorServiceImpl.updateUinStatus(entity);
	}

	@Test
	public void updateUinStatusASSIGNEDTest() {
		UinEntity extEntity = new UinEntity("9723157067", "ISSUED");
		UinEntity givEntity = new UinEntity("9723157067", "ASSIGNED");
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(extEntity);
		Mockito.when(uinRepository.save(Mockito.any())).thenReturn(givEntity);
		uinGeneratorServiceImpl.updateUinStatus(givEntity);
	}

	@Test
	public void updateUinStatusUNASSIGNEDTest() {
		UinEntity entity = new UinEntity("9723157067", "ISSUED");
		UinEntity givEntity = new UinEntity("9723157067", "UNASSIGNED");
		Mockito.when(uinRepository.findByUin(Mockito.any())).thenReturn(entity);
		Mockito.when(uinRepository.save(Mockito.any())).thenReturn(givEntity);
		uinGeneratorServiceImpl.updateUinStatus(givEntity);
	}
}