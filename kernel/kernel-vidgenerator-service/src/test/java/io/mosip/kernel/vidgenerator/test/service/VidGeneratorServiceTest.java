package io.mosip.kernel.vidgenerator.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.vidgenerator.config.HibernateDaoConfig;
import io.mosip.kernel.vidgenerator.constant.VidLifecycleStatus;
import io.mosip.kernel.vidgenerator.entity.VidEntity;
import io.mosip.kernel.vidgenerator.exception.VidGeneratorServiceException;
import io.mosip.kernel.vidgenerator.repository.VidRepository;
import io.mosip.kernel.vidgenerator.service.VidService;



@SpringBootTest
@TestPropertySource({ "classpath:application-test.properties", "classpath:bootstrap.properties" })
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HibernateDaoConfig.class, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class VidGeneratorServiceTest {

	@Autowired
	private VidService vidService;

	@MockBean
	private VidRepository vidRepository;
	
	private VidEntity assignedEntity;
	
	private List<VidEntity> assignedEntities;
	
	private VidEntity expiredEntity;
	
	private List<VidEntity> expiredEntities;
	
	private VidEntity availableEntity;
	
	private VidEntity availableEntityWithExpiry;
	
	@Before
	public void init() {
		availableEntityWithExpiry = new VidEntity("3650694284580734",VidLifecycleStatus.AVAILABLE,null);
		availableEntity = new VidEntity("3650694284580734",VidLifecycleStatus.AVAILABLE,null);
		assignedEntity = new VidEntity("3650694284580734",VidLifecycleStatus.ASSIGNED,DateUtils.getUTCCurrentDateTime().minus(1, ChronoUnit.MONTHS));
		assignedEntity.setCreatedBy("MOSIP_ADMIN");
		assignedEntity.setCreatedtimes(DateUtils.getUTCCurrentDateTime().minusDays(10));
		assignedEntities= new ArrayList<>();
		assignedEntities.add(assignedEntity);
		expiredEntity = new VidEntity("3690694284580734",VidLifecycleStatus.EXPIRED,DateUtils.getUTCCurrentDateTime().minus(7, ChronoUnit.MONTHS));
		assignedEntity.setCreatedBy("MOSIP_ADMIN");
		assignedEntity.setCreatedtimes(DateUtils.getUTCCurrentDateTime().minusDays(10));
		assignedEntity.setUpdatedBy("MOSIP_ADMIN");
		assignedEntity.setUpdatedtimes(DateUtils.getUTCCurrentDateTime().minus(7, ChronoUnit.MONTHS));
		expiredEntities= new ArrayList<>();
		expiredEntities.add(expiredEntity);
	}
	

	@Test(expected = VidGeneratorServiceException.class)
	public void fetchVidNotFoundTest() {
		Mockito.when(vidRepository.findFirstByStatus(VidLifecycleStatus.AVAILABLE)).thenReturn(null);
		vidService.fetchVid(null);
	}
	
	@Test(expected = VidGeneratorServiceException.class)
	public void fetchVidGerDataAccessTest() {
		Mockito.when(vidRepository.findFirstByStatus(VidLifecycleStatus.AVAILABLE)).thenThrow(new DataRetrievalFailureException("DataBase error occur"));
		vidService.fetchVid(null);
	}
	
	@Test(expected = VidGeneratorServiceException.class)
	public void fetchVidGetExceptionTest() {
		Mockito.when(vidRepository.findFirstByStatus(VidLifecycleStatus.AVAILABLE)).thenThrow(new RuntimeException("DataBase error occur"));
		vidService.fetchVid(null);
	}
	
	@Test(expected = VidGeneratorServiceException.class)
	public void fetchVidUpdateDataAccessTest() {
		Mockito.when(vidRepository.findFirstByStatus(VidLifecycleStatus.AVAILABLE)).thenReturn(availableEntity);
		Mockito.doThrow(new DataRetrievalFailureException("DataBase error occur")).when(vidRepository).updateVid(Mockito.eq(VidLifecycleStatus.ASSIGNED),Mockito.anyString(),Mockito.any(),Mockito.eq(availableEntity.getVid()));
		vidService.fetchVid(null);
	}
	
	@Test(expected = VidGeneratorServiceException.class)
	public void fetchVidUpdateExceptionTest() {
		Mockito.when(vidRepository.findFirstByStatus(VidLifecycleStatus.AVAILABLE)).thenReturn(availableEntity);
		Mockito.doThrow(new RuntimeException("DataBase error occur")).when(vidRepository).updateVid(Mockito.eq(VidLifecycleStatus.ASSIGNED),Mockito.anyString(),Mockito.any(),Mockito.eq(availableEntity.getVid()));
		vidService.fetchVid(null);
	}

	@Test
	public void fetchVidTest() {
		Mockito.when(vidRepository.findFirstByStatus(VidLifecycleStatus.AVAILABLE)).thenReturn(availableEntityWithExpiry);
		Mockito.when(vidRepository.save(Mockito.any())).thenReturn(availableEntityWithExpiry);
		vidService.fetchVid(DateUtils.getUTCCurrentDateTime().plusMonths(20));
	}
	
	@Test
	public void fetchVidCountDataAccessExceptionTest() {
		Mockito.when(vidRepository.countByStatusAndIsDeletedFalse(VidLifecycleStatus.AVAILABLE)).thenThrow(new DataRetrievalFailureException("DataBase error occur"));
		vidService.fetchVidCount(VidLifecycleStatus.AVAILABLE);
	}
	
	@Test
	public void fetchVidCountsExceptionTest() {
		Mockito.when(vidRepository.countByStatusAndIsDeletedFalse(VidLifecycleStatus.AVAILABLE)).thenThrow(new RuntimeException("DataBase error occur"));
		vidService.fetchVidCount(VidLifecycleStatus.AVAILABLE);
	}

	@Test
	public void fetchVidCountTest() {
		Mockito.when(vidRepository.countByStatusAndIsDeletedFalse(VidLifecycleStatus.AVAILABLE)).thenReturn(200000L);
		assertThat(vidService.fetchVidCount(VidLifecycleStatus.AVAILABLE),is(200000L));
	}

	
	@Test
	public void expireOrRenewDataAccessExceptionTest() {
		Mockito.when(vidRepository.findByStatusAndIsDeletedFalse(VidLifecycleStatus.ASSIGNED)).thenThrow(new DataRetrievalFailureException("DataBase error occur"));
		vidService.expireAndRenew();
	}
	
	@Test
	public void expireOrRenewExceptionTest() {
		Mockito.when(vidRepository.findByStatusAndIsDeletedFalse(VidLifecycleStatus.ASSIGNED)).thenThrow(new RuntimeException("DataBase error occur"));
		vidService.expireAndRenew();
	}

	@Test
	public void expireOrRenewTest() {
		Mockito.when(vidRepository.findByStatusAndIsDeletedFalse(VidLifecycleStatus.ASSIGNED)).thenReturn(assignedEntities);
		Mockito.when(vidRepository.saveAll(assignedEntities)).thenReturn(assignedEntities);
		Mockito.when(vidRepository.findByStatusAndIsDeletedFalse(VidLifecycleStatus.EXPIRED)).thenReturn(expiredEntities);
		Mockito.when(vidRepository.saveAll(expiredEntities)).thenReturn(expiredEntities);
		vidService.expireAndRenew();
		for (VidEntity vidEntity : assignedEntities) {
			assertThat(vidEntity.getStatus(), is(VidLifecycleStatus.EXPIRED));
		}
		for (VidEntity vidEntity : expiredEntities) {
			assertThat(vidEntity.getStatus(), is(VidLifecycleStatus.AVAILABLE));
			assertNull(vidEntity.getVidExpiry());
		}
	}
	
	@Test
	public void saveVIDDataAccessExceptionTest() {
		Mockito.when(vidRepository.existsById(availableEntity.getVid())).thenReturn(false);
		Mockito.when(vidRepository.saveAndFlush(availableEntity)).thenThrow(new DataRetrievalFailureException("DataBase error occur"));
		vidService.saveVID(availableEntity);
	}
	
	@Test
	public void saveVIDExceptionTest() {
		Mockito.when(vidRepository.existsById(availableEntity.getVid())).thenReturn(false);
		Mockito.when(vidRepository.saveAndFlush(availableEntity)).thenThrow(new RuntimeException("DataBase error occur"));
		vidService.saveVID(availableEntity);
	}

	@Test
	public void saveVIDTest() {
		Mockito.when(vidRepository.existsById(availableEntity.getVid())).thenReturn(false);
		Mockito.when(vidRepository.saveAndFlush(availableEntity)).thenReturn(availableEntity);
		assertThat(vidService.saveVID(availableEntity),is(true));
	}
	
	@Test
	public void saveVIDFalseTest() {
		Mockito.when(vidRepository.existsById(availableEntity.getVid())).thenReturn(true);
		assertThat(vidService.saveVID(availableEntity),is(false));
	}
	
}