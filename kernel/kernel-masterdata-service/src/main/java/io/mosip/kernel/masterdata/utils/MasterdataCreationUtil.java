/**
 * 
 */
package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.kernel.masterdata.constant.RequestErrorCode;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * @author Ramadurai Pandian
 *
 */
@Repository
@Transactional(readOnly = true)
public class MasterdataCreationUtil {

	private static final String LANGCODE_COLUMN_NAME = "langCode";

	private static final String ID_COLUMN_NAME = "id";
	
	private static final String CODE_COLUMN_NAME = "code";

	private static final String ISACTIVE_COLUMN_NAME = "isActive";
	
	private static final String NAME_COLUMN_NAME = "name";

	@Value("${mosip.primary-language:eng}")
	private String primaryLang;

	@Value("${mosip.secondary-language:ara}")
	private String secondaryLang;

	/**
	 * Field for interface used to interact with the persistence context.
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Constructor for MasterdataSearchHelper having EntityManager
	 * 
	 * @param entityManager
	 *            The entityManager
	 */
	public MasterdataCreationUtil(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public <E, T> T createMasterData(Class<E> entity, T t)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		String langCode = null, id = null;
		String primaryId = null;
		boolean activeDto=false,activePrimary=false;
		String primaryKeyCol=null,nameCol=null,nameValue=null;
		Class<?> dtoClass = t.getClass();
		for (Field entField: entity.getDeclaredFields()) {
			 if(entField.isAnnotationPresent(Id.class))
			 {
				 entField.setAccessible(true);
				 if (entField.getName() != null && !entField.getName().equals(LANGCODE_COLUMN_NAME)) {
					 primaryKeyCol = entField.getName();
					}
				 if (entField.getName() != null && !entField.getName().equals(NAME_COLUMN_NAME)) {
					 nameCol = entField.getName();
					}
			 }
			}
		for (Field field : dtoClass.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getName() != null && field.getName().equals(LANGCODE_COLUMN_NAME)) {
				langCode = (String) field.get(t);
			}

			if (field.getName() != null && field.getName().equals(primaryKeyCol)) {
				id = (String) field.get(t);
			}
			
			if (field.getName() != null && field.getName().equals(ISACTIVE_COLUMN_NAME)) {
				activeDto = (boolean) field.get(t);
			}
			
			if (field.getName() != null && field.getName().equals(NAME_COLUMN_NAME)) {
				nameValue = (String) field.get(t);
			}
		}
		
		if (langCode.equals(primaryLang)) {
			Field isActive = dtoClass.getDeclaredField(ISACTIVE_COLUMN_NAME);
			isActive.setAccessible(true);
			isActive.set(t, Boolean.FALSE);
			if(primaryKeyCol!=null&&primaryKeyCol.equals(CODE_COLUMN_NAME))
			{
				Field idColumn = dtoClass.getDeclaredField(CODE_COLUMN_NAME);
				idColumn.setAccessible(true);
				primaryId =  generateId();
				E primary = getResultSet(entity, primaryLang, primaryId,primaryKeyCol);
				if(primary!=null)
				{
					idColumn.set(t, primaryId);
				}
				else
				{
					idColumn.set(t, generateId());
				}
				
			}
			if(primaryKeyCol!=null&&primaryKeyCol.equals(ID_COLUMN_NAME))
			{
				Field idColumn = dtoClass.getDeclaredField(ID_COLUMN_NAME);
				idColumn.setAccessible(true);
				if(!entity.equals(RegistrationCenter.class))
				{
					primaryId = generateId();
					E primary = getResultSet(entity, primaryLang, primaryId,primaryKeyCol);
					if(primary!=null)
					{
						idColumn.set(t, primaryId);
					}
					else
					{
						idColumn.set(t, generateId());
					}
				}
			}
			return t;
		}
		if (langCode.equals(secondaryLang)) {
			
			E primaryEntity = getResultSet(entity, primaryLang, id,primaryKeyCol);
			if (primaryEntity != null) {
				for (Field field : primaryEntity.getClass().getDeclaredFields()) {
					field.setAccessible(true);
							if (field.getName() != null && field.getName().equals(ISACTIVE_COLUMN_NAME)) {
								activePrimary = (boolean) field.get(t);
					}
							
				}
				if(activeDto==true)
				{
					Field isActive = dtoClass.getDeclaredField(ISACTIVE_COLUMN_NAME);
					isActive.setAccessible(true);
					isActive.set(t, Boolean.TRUE);
					updatePrimaryToTrue(primaryEntity.getClass(),id,primaryKeyCol,true);
				}
				if(activeDto==false)
				{
					Field isActive = dtoClass.getDeclaredField(ISACTIVE_COLUMN_NAME);
					isActive.setAccessible(true);
					isActive.set(t, Boolean.FALSE);
					updatePrimaryToTrue(primaryEntity.getClass(),id,primaryKeyCol,false);
				}
				return t;
			} else {
				throw new MasterDataServiceException(RequestErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
						"Cannot create data in secondary language as data does not exist in primary language");
			}
		}
		return null;
	}
	
	private String getCodeFromName(String name) {
		return RandomStringUtils.random(name.length(), name);
	}
	
	private String generateId()
	{
		return UUID.randomUUID().toString();
	}
	
	private<E> int updatePrimaryToTrue(Class<E> entityClass, String id,String primaryKeyCol,boolean active) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();		
		CriteriaUpdate<E> update = criteriaBuilder.createCriteriaUpdate(entityClass);
		Root<E> root = update.from(entityClass);
		Predicate idPredicate = setId(criteriaBuilder, root, id,primaryKeyCol);
		predicates.add(idPredicate);
		update.where(predicates.toArray(new Predicate[] {}));
		update.set(root.get(ISACTIVE_COLUMN_NAME), active);
		Query executableQuery = entityManager.createQuery(update);
		return executableQuery.executeUpdate();
	}

	public <E, T> T updateMasterData(Class<E> entity, T t)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		String langCode = null, id = null;
		boolean activeDto=false,activePrimary=false;
		String primaryKeyCol=null,nameCol=null,nameValue=null;
		Field isActive;
		Class<?> dtoClass = t.getClass();
		for (Field entField: entity.getDeclaredFields()) {
			 if(entField.isAnnotationPresent(Id.class))
			 {
				 entField.setAccessible(true);
				 if (entField.getName() != null && !entField.getName().equals(LANGCODE_COLUMN_NAME)) {
					 primaryKeyCol = entField.getName();
					}
				 if (entField.getName() != null && !entField.getName().equals(NAME_COLUMN_NAME)) {
					 nameCol = entField.getName();
					}
			 }
			}
		for (Field field : dtoClass.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getName() != null && field.getName().equals(LANGCODE_COLUMN_NAME)) {
				langCode = (String) field.get(t);
			}

			if (field.getName() != null && field.getName().equals(primaryKeyCol)) {
				id = (String) field.get(t);
			}
			
			if (field.getName() != null && field.getName().equals(ISACTIVE_COLUMN_NAME)) {
				activeDto = (boolean) field.get(t);
			}
		}
		
		if (langCode.equals(primaryLang)) {
			if(activeDto==true)
			{
				isActive = dtoClass.getDeclaredField(ISACTIVE_COLUMN_NAME);
				isActive.setAccessible(true);
				isActive.set(t, Boolean.TRUE);
			}
			if(activeDto==false)
			{
				isActive = dtoClass.getDeclaredField(ISACTIVE_COLUMN_NAME);
				isActive.setAccessible(true);
				isActive.set(t, Boolean.FALSE);
			}
			return t;
		}
		if (langCode.equals(secondaryLang)) {
			
			E primaryEntity = getResultSet(entity, primaryLang, id,primaryKeyCol);
			if (primaryEntity != null) {
				for (Field field : primaryEntity.getClass().getDeclaredFields()) {
					field.setAccessible(true);
							if (field.getName() != null && field.getName().equals(ISACTIVE_COLUMN_NAME)) {
								activePrimary = (boolean) field.get(t);
					}
				}
				}
				if(activeDto==true)
				{
					isActive = dtoClass.getDeclaredField(ISACTIVE_COLUMN_NAME);
					isActive.setAccessible(true);
					isActive.set(t, Boolean.TRUE);
					updatePrimaryToTrue(primaryEntity.getClass(),id,primaryKeyCol,true);
				}
				if(activeDto==false)
				{
					isActive = dtoClass.getDeclaredField(ISACTIVE_COLUMN_NAME);
					isActive.setAccessible(true);
					isActive.set(t, Boolean.FALSE);
					updatePrimaryToTrue(primaryEntity.getClass(),id,primaryKeyCol,false);
				}
			if (secondaryLang != null) {
				return t;
			} else {
				throw new MasterDataServiceException(RequestErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
						"Cannot update data in secondary language as data does not exist in primary language");
			}
		}
		return null;
	}


	private <E> E getResultSet(Class<E> entity, String langCode, String id,String idColumn) {
		E result = null;
		try {
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<E> selectQuery = criteriaBuilder.createQuery(entity);
			Root<E> rootQuery = selectQuery.from(entity);
			Predicate predicate = setLangCode(criteriaBuilder, rootQuery, langCode);
			Predicate idPredicate = setId(criteriaBuilder, rootQuery, id,idColumn);
			predicates.add(predicate);
			predicates.add(idPredicate);
			selectQuery.where(predicates.toArray(new Predicate[] {}));
			TypedQuery<E> executableQuery = entityManager.createQuery(selectQuery);
			result = executableQuery.getSingleResult();
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCode.HIBERNATE_EXCEPTION.getErrorCode(),
					hibernateException.getMessage(), hibernateException);
		} catch (Exception e) {
			if (e instanceof NoResultException) {
				return null;
			} else {
				throw new MasterDataServiceException(HibernateErrorCode.HIBERNATE_EXCEPTION.getErrorCode(),
						e.getMessage());
			}
		}
		return result;
	}

	/**
	 * Method to add the Language Code in the criteria query
	 * 
	 * @param builder
	 *            used to construct the criteria query
	 * @param root
	 *            root type in the from clause,always refers entity
	 * @param langCode
	 *            language code
	 * @return {@link Predicate}
	 */
	private <E> Predicate setLangCode(CriteriaBuilder builder, Root<E> root, String langCode) {
		if (langCode != null && !langCode.isEmpty()) {
			Path<Object> langCodePath = root.get(LANGCODE_COLUMN_NAME);
			if (langCodePath != null) {
				return builder.equal(langCodePath, langCode);
			}
		}
		return null;
	}

	private <E> Predicate setId(CriteriaBuilder builder, Root<E> root, String id,String idColumn) {
		if (id != null && !id.isEmpty()) {
			Path<Object> idPath = root.get(idColumn);
			if (id != null) {
				return builder.equal(idPath, id);
			}
		}
		return null;
	}

}
