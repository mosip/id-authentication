package org.mosip.kernel.uingenerator.batch;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.HibernateItemWriter;

public class UinGenWriter extends HibernateItemWriter<Object> {

	private final Logger log = LoggerFactory.getLogger(UinGenWriter.class);

	private EntityManager entityManager;

	public UinGenWriter(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void write(List<? extends Object> items) {
		List<UinBean> unwrappedItems = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<List<UinBean>> wrappedItems = (List<List<UinBean>>) items;
		for (List<UinBean> singleList : wrappedItems) {
			unwrappedItems.addAll(singleList);
		}
		doWriteItems(entityManager, unwrappedItems);
	}

	void doWriteItems(EntityManager entityManager, List<UinBean> items) {
		Session currentSession = entityManager.unwrap(Session.class);
		for (UinBean item : items) {
			currentSession.save(item);
			try {
				currentSession.flush();
			} catch (ConstraintViolationException e) {
				log.info(" {} already exists. Failed to insert: {} ", item.getUin(), e.getMessage());
			} finally {
				currentSession.clear();
			}
		}
	}
}