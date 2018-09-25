package org.mosip.registration.processor.status.dao;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.status.dao.RegistrationStatusDao;
import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RegistrationStatusDaoTest {

	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	@Test
	public void DAOCheck() {
		RegistrationStatusEntity registrationStatusEntity = new RegistrationStatusEntity("100898",
				"PACKET_UPLOADED_TO_LANDING_ZONE", 0);

		testEntityManager.persist(registrationStatusEntity);
		testEntityManager.flush();

		assertEquals("The registration status should be fetched successfully", true,
				registrationStatusDao.existsById("100898"));

	}

	@Test
	public void addDAOCheck() {
		RegistrationStatusEntity registrationStatusEntity = new RegistrationStatusEntity("1008",
				"PACKET_UPLOADED_TO_LANDING_ZONE", 0);

		registrationStatusDao.save(registrationStatusEntity);
		Optional<RegistrationStatusEntity> output = registrationStatusDao.findById("1008");

		assertEquals("The registration status should get addded successfully", "1008", output.get().getEnrolmentId());
		assertEquals("The registration status should get addded successfully", "PACKET_UPLOADED_TO_LANDING_ZONE",
				output.get().getStatus());

	}

}
