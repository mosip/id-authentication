package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.kernel.dataaccess.constant.HibernateErrorCodes;
import io.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.impl.SyncRegistrationServiceImpl;

/**
 * The Class SyncRegistrationServiceTest.
 * 
 * @author M1047487
 */
@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
@TestPropertySource({ "classpath:status-application.properties" })
@ContextConfiguration
public class SyncRegistrationServiceTest {

	/** The sync registration dto. */
	private SyncRegistrationDto syncRegistrationDto;

	/** The sync registration entity. */
	private SyncRegistrationEntity syncRegistrationEntity;

	/** The entities. */
	private List<SyncRegistrationDto> entities;

	/** The sync registration dao. */
	@Mock
	private SyncRegistrationDao syncRegistrationDao;

	/** The audit request builder. */
	@Mock
	private AuditRequestBuilder auditRequestBuilder;

	/** The audit handler. */
	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;

	/** The sync registration service. */
	@InjectMocks
	private SyncRegistrationService<SyncRegistrationDto> syncRegistrationService = new SyncRegistrationServiceImpl();

	/**
	 * Setup.
	 *
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		entities = new ArrayList<>();

		syncRegistrationDto = new SyncRegistrationDto();

		syncRegistrationDto.setSyncRegistrationId("1001");
		syncRegistrationDto.setRegistrationId("1002");
		syncRegistrationDto.setLangCode("eng");
		syncRegistrationDto.setIsActive(true);
		syncRegistrationDto.setCreatedBy("MOSIP_SYSTEM");

		syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId("1001");
		syncRegistrationEntity.setParentRegistrationId("1002");
		syncRegistrationEntity.setIsActive(true);

		entities.add(syncRegistrationDto);

		AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
		AuditRequestDto auditRequest1 = new AuditRequestDto();

		Field f = SyncRegistrationServiceImpl.class.getDeclaredField("auditRequestBuilder");
		f.setAccessible(true);
		f.set(syncRegistrationService, auditRequestBuilder);

		Field f1 = AuditRequestBuilder.class.getDeclaredField("auditRequest");
		f1.setAccessible(true);
		f1.set(auditRequestBuilder, auditRequest1);

		Field f2 = SyncRegistrationServiceImpl.class.getDeclaredField("auditHandler");
		f2.setAccessible(true);
		f2.set(syncRegistrationService, auditHandler);

		Mockito.when(syncRegistrationDao.save(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);

	}

	/**
	 * Gets the sync registration status test.
	 *
	 * @return the sync registration status test
	 */
	@Test
	public void getSyncRegistrationStatusSuccessTest() {
		syncRegistrationService.sync(entities);
	}

	/**
	 * Gets the sync registration status failure test.
	 *
	 * @return the sync registration status failure test
	 * @throws TablenotAccessibleException
	 *             the tablenot accessible exception
	 */
	@Test(expected = TablenotAccessibleException.class)
	public void getSyncRegistrationStatusFailureTest() throws TablenotAccessibleException {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, "errorMessage",
				new Exception());
		Mockito.when(syncRegistrationDao.save(ArgumentMatchers.any())).thenThrow(exp);
		syncRegistrationService.sync(entities);

	}

	/**
	 * Checks if is present success test.
	 */
	@Test
	public void isPresentSuccessTest() {
		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		boolean result = syncRegistrationService.isPresent("1001");
		assertEquals(true, result);
	}

	/**
	 * Checks if is present success test.
	 */
	@Test
	public void isPresentFailureTest() {
		Mockito.when(syncRegistrationDao.findById(ArgumentMatchers.any())).thenReturn(null);
		boolean result = syncRegistrationService.isPresent("15");
		assertEquals(false, result);
	}

}
