package org.mosip.kernel.uingenerator.batch;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UinGenerationWriter implements ItemWriter<List<UinBean>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UinGenerationWriter.class);

	@Autowired
	private EntityManager entityManager;

	private Session session;

	@Override
	public void write(List<? extends List<UinBean>> uins) {
		setSession();
		LOGGER.info("Persisting generated uins in database");

		uins.stream().flatMap(List::stream).forEach(this::persistUin);

		LOGGER.info("Persisted generated uins in database");
	}

	private void persistUin(UinBean item) {
		Session currentSession = getSession();
		if (!currentSession.getTransaction().isActive()) {
			currentSession.getTransaction().begin();
		}
		currentSession.save(item);
		try {
			currentSession.flush();
			currentSession.getTransaction().commit();
		} catch (PersistenceException e) {
			currentSession.getTransaction().rollback();
		} finally {
			currentSession.clear();
		}
	}

	private void setSession() {
		entityManager = entityManager.getEntityManagerFactory().createEntityManager();
		this.session = entityManager.unwrap(Session.class);
	}

	private Session getSession() {
		return session;
	}
}