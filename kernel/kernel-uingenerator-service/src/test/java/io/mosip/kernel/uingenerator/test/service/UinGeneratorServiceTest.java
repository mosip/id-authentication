package io.mosip.kernel.uingenerator.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import io.mosip.kernel.uingenerator.exception.UinNotFoundException;
import io.mosip.kernel.uingenerator.repository.UinRepository;
import io.mosip.kernel.uingenerator.service.impl.UinGeneratorServiceImpl;
import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UinGeneratorTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class UinGeneratorServiceTest {

	@Autowired
	private UinGeneratorServiceImpl uinGeneratorServiceImpl;

	@MockBean
	private UinRepository uinRepository;

	@Test(expected = UinNotFoundException.class)
	public void uinServiceTest() {

		Mockito.when(uinRepository.findFirstByUsedIsFalse()).thenReturn(null);
		uinGeneratorServiceImpl.getUin();
	}
}