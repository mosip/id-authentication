package io.mosip.registration.processor.manual.adjudication.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.bind.MethodArgumentNotValidException;

import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.manual.adjudication.repository.ManualAdjudiacationRepository;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

/**
 * 
 * @author M1049617
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ManualAdjudicationDaoTest {

	/** The manualAdjudicationDao dao. */
	@InjectMocks
	ManualAdjudicationDao manualAdjudicationDao=new ManualAdjudicationDao();
	
	/** The manualAdjudiacationRepository repository. */
	@Mock
	ManualAdjudiacationRepository<ManualVerificationEntity, ManualVerificationPKEntity> manualAdjudiacationRepository;
	@Mock
	private ManualVerificationEntity manualAdjudicationEntity;
	
	ManualVerificationPKEntity manualVerificationPKEntity=new ManualVerificationPKEntity();
	private List<ManualVerificationEntity> manualAdjudicationEntityList;
	private String status="PENDING";
	
	@Before
	public void setUp() {
		manualAdjudicationEntityList =new ArrayList<ManualVerificationEntity>();
		manualAdjudicationEntity = new ManualVerificationEntity();
		manualAdjudicationEntity.setPkId(manualVerificationPKEntity);
		manualAdjudicationEntity.getPkId().setRegId("12345");
		manualAdjudicationEntity.getPkId().setMatchedRefType("12345");
		manualAdjudicationEntity.getPkId().setMatchedRefId("12345");
		manualAdjudicationEntity.setCrBy("USER");
		manualAdjudicationEntity.setIsActive(false);
		manualAdjudicationEntity.setMvUsrId("mvuser");
		manualAdjudicationEntity.setMatchedScore(BigDecimal.TEN);
		manualAdjudicationEntity.setStatusCode("APPROVED");
		manualAdjudicationEntity.setMvUsrId("mv");
		
		Mockito.when(manualAdjudiacationRepository.save(ArgumentMatchers.any())).thenReturn(manualAdjudicationEntity);
		Mockito.when(manualAdjudiacationRepository.getByRegId(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(manualAdjudicationEntity);

		Mockito.when(manualAdjudiacationRepository.getFirstApplicantDetails(ArgumentMatchers.any()))
				.thenReturn(manualAdjudicationEntityList);
		
		
		
	}
	@Test
	public void updateTest() {
		Mockito.when(manualAdjudiacationRepository.save(ArgumentMatchers.any())).thenReturn(manualAdjudicationEntity);
		manualAdjudicationEntity= manualAdjudicationDao.update(manualAdjudicationEntity);
	}
	
	@Test
	public void getFirstApplicantDetailsTest() {
		List<ManualVerificationEntity> manualAdjudicationEntitiesResult= manualAdjudicationDao.getFirstApplicantDetails(status);
	}
	@Test
	public void getByRegIdTest() {
		ManualVerificationEntity manualAdjudicationEntityResult=manualAdjudicationDao.getByRegId(manualAdjudicationEntity.getPkId().getRegId(), manualAdjudicationEntity.getPkId().getMatchedRefId(), manualAdjudicationEntity.getMvUsrId());
	}
	@Test
	public void getAssignedApplicantDetailsTest() {
		ManualVerificationEntity manualAdjudicationEntityResult=manualAdjudicationDao.getAssignedApplicantDetails(manualAdjudicationEntity.getMvUsrId(), manualAdjudicationEntity.getStatusCode());
	}
}
