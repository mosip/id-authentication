package io.mosip.kernel.dataaccess.hibernate.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.HibernateException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.mosip.kernel.dataaccess.hibernate.test.model.Person;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { HibernateDaoConfig.class, PersonRepository.class })

public class HibernateRepositoryTest {

	HibernateRepositoryImpl<Person, Integer> repository;

	@Autowired
	PersonRepository personRepository;

	@Mock
	CustomInterceptor customInterceptor;

	@Mock
	EntityManager em;

	@Mock
	CriteriaBuilder builder;

	@Mock
	CriteriaQuery<Object> criteriaQuery;

	@Mock
	TypedQuery<Object> query;

	@Mock
	JpaEntityInformation<Person, Integer> information;

	@Mock
	CrudMethodMetadata metadata;

	@Before
	public void setUp() {

		when(em.getDelegate()).thenReturn(em);
		when(information.getJavaType()).thenReturn(Person.class);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(builder.createQuery(Object.class)).thenReturn(criteriaQuery);
		when(em.getCriteriaBuilder().createQuery()).thenReturn(criteriaQuery);
		when(em.createQuery(criteriaQuery)).thenReturn(query);
		when(em.createQuery(ArgumentMatchers.anyString())).thenReturn(query);
		when(em.createNamedQuery(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(query);
		repository = new HibernateRepositoryImpl<Person, Integer>(information, em);
		repository.setRepositoryMethodMetadata(metadata);
	}

	@Test
	public void testInterceptor() {
		Person person = new Person("Bal Vikash Sharma");
		person.setId(1);
		personRepository.create(person);
		personRepository.update(person);
		personRepository.findAll();
		personRepository.delete(person);

	}

	@Test
	public void repositoryCreateTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		Mockito.verify(em, times(1)).persist(baseEntity);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateExceptionTest() {

		Person baseEntity = new Person("personName");
		doThrow(new HibernateException("Hibernate exception")).when(em).persist(baseEntity);
		repository.create(baseEntity);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		doThrow(new RuntimeException("Runtime exception")).when(em).persist(baseEntity);
		repository.create(baseEntity);
	}

	@Test
	public void repositoryUpdateTest() {

		Person baseEntity = new Person("personName");
		repository.update(baseEntity);
		Mockito.verify(em, times(1)).merge(baseEntity);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryUpdateExceptionTest() {

		Person baseEntity = new Person("personName");
		doThrow(new HibernateException("Hibernate exception")).when(em).merge(baseEntity);
		repository.update(baseEntity);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryUpdateRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		doThrow(new RuntimeException("Runtime exception")).when(em).merge(baseEntity);
		repository.update(baseEntity);
	}

	@Test
	public void repositoryFindByIdTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findById(Person.class, 1);
		Mockito.verify(em, times(1)).find(Person.class, 1);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryFindByIdExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findById(Person.class, 1);
		doThrow(new HibernateException("Hibernate exception")).when(em).find(Person.class, 1);
		repository.findById(Person.class, 1);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryFindByIdRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findById(Person.class, 1);
		doThrow(new RuntimeException("Runtime exception")).when(em).find(Person.class, 1);
		repository.findById(Person.class, 1);
	}

	@Test
	public void repositoryFindAllTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.findAll(Person.class);
		Mockito.verify(em, times(1)).createQuery(criteriaQuery);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryFindAllExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		doThrow(new HibernateException("Hibernate exception")).when(em).createQuery(criteriaQuery);
		repository.findAll(Person.class);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryFindAllRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		doThrow(new RuntimeException("Runtime exception")).when(em).createQuery(criteriaQuery);
		repository.findAll(Person.class);
	}

	@Test
	public void repositoryDeleteTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.delete(Person.class, 1);
		Mockito.verify(em, times(1)).remove(ArgumentMatchers.any());
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryDeleteExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		doThrow(new HibernateException("Hibernate exception")).when(em).remove(ArgumentMatchers.any());
		repository.delete(Person.class, 1);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryDeleteRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		doThrow(new RuntimeException("Runtime exception")).when(em).remove(ArgumentMatchers.any());
		repository.delete(Person.class, 1);
	}

	@Test
	public void repositoryCreateQuerySelectTest() {

		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createQuerySelect(qlString, parameterMap);
		Mockito.verify(query, times(1)).getResultList();
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQuerySelectExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new HibernateException("Hibernate exception")).when(query).getResultList();
		repository.createQuerySelect(qlString, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQuerySelectNoResultExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new NoResultException("Hibernate exception")).when(query).getResultList();
		repository.createQuerySelect(qlString, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQuerySelectRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new RuntimeException("Runtime exception")).when(query).getResultList();
		repository.createQuerySelect(qlString, null);
	}

	@Test
	public void repositoryCreateQuerySelectLimitTest() {

		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createQuerySelect(qlString, parameterMap, 10);
		Mockito.verify(query, times(1)).getResultList();
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQuerySelectLimitExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new HibernateException("Hibernate exception")).when(query).getResultList();
		repository.createQuerySelect(qlString, null, 10);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQuerySelectLimitNoResultExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new NoResultException("Hibernate exception")).when(query).getResultList();
		repository.createQuerySelect(qlString, null, 10);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQuerySelectLimitRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new RuntimeException("Runtime exception")).when(query).getResultList();
		repository.createQuerySelect(qlString, null, 10);
	}

	@Test
	public void repositoryCreateQueryUpdateOrDeleteTest() {

		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createQueryUpdateOrDelete(qlString, parameterMap);
		Mockito.verify(query, times(1)).executeUpdate();
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQueryUpdateOrDeleteExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new HibernateException("Hibernate exception")).when(query).executeUpdate();
		repository.createQueryUpdateOrDelete(qlString, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQueryUpdateOrDeleteNoResultExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new NoResultException("Hibernate exception")).when(query).executeUpdate();
		repository.createQueryUpdateOrDelete(qlString, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateQueryUpdateOrDeleteRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String qlString = "SELECT c FROM Person c WHERE c.name LIKE :personName";
		doThrow(new RuntimeException("Runtime exception")).when(query).executeUpdate();
		repository.createQueryUpdateOrDelete(qlString, null);
	}

	@Test
	public void repositoryCreateNamedQuerySelectTest() {

		String name = "findAllCustomersWithName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createNamedQuerySelect(name, Person.class, parameterMap);
		Mockito.verify(query, times(1)).getResultList();
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateNamedQuerySelectExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String name = "findAllCustomersWithName";
		doThrow(new HibernateException("Hibernate exception")).when(query).getResultList();
		repository.createNamedQuerySelect(name, Person.class, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateNamedQuerySelectNoResultExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String name = "findAllCustomersWithName";
		doThrow(new NoResultException("Hibernate exception")).when(query).getResultList();
		repository.createNamedQuerySelect(name, Person.class, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateNamedQuerySelectRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String name = "findAllCustomersWithName";
		doThrow(new RuntimeException("Runtime exception")).when(query).getResultList();
		repository.createNamedQuerySelect(name, Person.class, null);
	}

	@Test
	public void repositoryCreateNamedQueryUpdateOrDeleteTest() {

		String name = "findAllCustomersWithName";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("personName", "personName");
		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		repository.createNamedQueryUpdateOrDelete(name, Person.class, parameterMap);
		Mockito.verify(query, times(1)).executeUpdate();
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateNamedQueryUpdateOrDeleteExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String name = "findAllCustomersWithName";
		doThrow(new HibernateException("Hibernate exception")).when(query).executeUpdate();
		repository.createNamedQueryUpdateOrDelete(name, Person.class, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateNamedQueryUpdateOrDeleteNoResultExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String name = "findAllCustomersWithName";
		doThrow(new NoResultException("Hibernate exception")).when(query).executeUpdate();
		repository.createNamedQueryUpdateOrDelete(name, Person.class, null);
	}

	@Test(expected = DataAccessLayerException.class)
	public void repositoryCreateNamedQueryUpdateOrDeleteRuntimeExceptionTest() {

		Person baseEntity = new Person("personName");
		repository.create(baseEntity);
		String name = "findAllCustomersWithName";
		doThrow(new RuntimeException("Runtime exception")).when(query).executeUpdate();
		repository.createNamedQueryUpdateOrDelete(name, Person.class, null);
	}

}