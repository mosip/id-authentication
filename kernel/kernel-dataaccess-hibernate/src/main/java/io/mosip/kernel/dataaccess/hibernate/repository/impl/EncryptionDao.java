package io.mosip.kernel.dataaccess.hibernate.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.dataaccess.hibernate.entity.SecreteKeyStore;



@Repository
@Transactional
public class EncryptionDao {


	@PersistenceContext
	private EntityManager entityManager;

	public void createPerson(Object entity) {
		try {
			entityManager.persist(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			entityManager.close();
	      }
	}

	public void saveKey(SecreteKeyStore secretKey) {
		try {
			entityManager.persist(secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateKey(SecreteKeyStore secretKey) {
		try {
			entityManager.merge(secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SecreteKeyStore getKey(String id) {
		try {
			SecreteKeyStore secrteKeyStore = entityManager.find(SecreteKeyStore.class, id);
			return secrteKeyStore;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public SecreteKeyStore getKey() {
		try {
			Query query = entityManager.createQuery("SELECT e FROM SecreteKeyStore e where e.expired='false'");
			return (SecreteKeyStore) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
