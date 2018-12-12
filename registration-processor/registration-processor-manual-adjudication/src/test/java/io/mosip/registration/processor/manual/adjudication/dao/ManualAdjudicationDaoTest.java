package io.mosip.registration.processor.manual.adjudication.dao;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
/**
 * The Class ManualAdjudicationDaoTest.
 *
 * @author M1049387
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ManualAdjudicationDaoTest {

	/** The manualAdjudicationDao dao. */
	@Mock
	private BasePacketRepository<ManualVerificationEntity, String> basePacketRepository;
	
	/** The manual verification entity. */
	@Mock
	private ManualVerificationEntity manualVerificationEntity;

	/** The manual verification PK entity. */
	ManualVerificationPKEntity manualVerificationPKEntity=new ManualVerificationPKEntity();

	/** The manual adjudication entity list. */
	private ManualVerificationEntity manualAdjudicationEntity;

	/** The status. */
	private String status="PENDING";

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		manualVerificationEntity = new ManualVerificationEntity();
		manualVerificationEntity.setId(manualVerificationPKEntity);
		manualVerificationEntity.getId().setRegId("12345");
		manualVerificationEntity.getId().setMatchedRefType("12345");
		manualVerificationEntity.getId().setMatchedRefId("12345");
		manualVerificationEntity.setCrBy("USER");
		manualVerificationEntity.setIsActive(false);
		manualVerificationEntity.setMvUsrId("mvuser");
		manualVerificationEntity.setMatchedScore(BigDecimal.TEN);
		manualVerificationEntity.setStatusCode("APPROVED");
		manualVerificationEntity.setMvUsrId("mv");
		Mockito.when(basePacketRepository.save(ArgumentMatchers.any())).thenReturn(manualVerificationEntity);
		Mockito.when(basePacketRepository.getSingleAssignedRecord(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(manualVerificationEntity);
		Mockito.when(basePacketRepository.getFirstApplicantDetails(ArgumentMatchers.any())).thenReturn(manualAdjudicationEntity);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(manualVerificationEntity);



	}
	
	/**
	 * Update test.
	 */
	@Test
	public void updateTest() {
		ManualVerificationEntity manualAdjudicationEntityResult= basePacketRepository.update(manualVerificationEntity);
		assertEquals(manualVerificationEntity, manualAdjudicationEntityResult);
	}

	/**
	 * Gets the first applicant details test.
	 *
	 * @return the first applicant details test
	 */
	@Test
	public void getFirstApplicantDetailsTest() {
		ManualVerificationEntity manualAdjudicationEntitiesResult= basePacketRepository.getFirstApplicantDetails(status);
		assertEquals(manualAdjudicationEntity, manualAdjudicationEntitiesResult);
	}
	
	/**
	 * Gets the by reg id test.
	 *
	 * @return the by reg id test
	 */
	@Test
	public void getByRegIdTest() {
		ManualVerificationEntity manualAdjudicationEntityResult=basePacketRepository.getSingleAssignedRecord(manualVerificationEntity.getId().getRegId(), manualVerificationEntity.getId().getMatchedRefId(), manualVerificationEntity.getMvUsrId());
		assertEquals(manualVerificationEntity, manualAdjudicationEntityResult);
	}
	
	/**
	 * Gets the assigned applicant details test.
	 *
	 * @return the assigned applicant details test
	 */
	@Test
	public void getAssignedApplicantDetailsTest() {
		ManualVerificationEntity manualAdjudicationEntityResult=basePacketRepository.getAssignedApplicantDetails(manualVerificationEntity.getMvUsrId(), manualVerificationEntity.getStatusCode());
		assertEquals(manualVerificationEntity, manualAdjudicationEntityResult);

	}
}
