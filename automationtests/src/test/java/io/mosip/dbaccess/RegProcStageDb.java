package io.mosip.dbaccess;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.mosip.dbdto.DemoDedupeDto;
import io.mosip.dbdto.IndividualDemoghraphicDedupeEntity;

public class RegProcStageDb {
	public static SessionFactory factory;
	static Session session;
	private static Logger logger = Logger.getLogger(RegProcStageDb.class);
	static String registrationListConfigFilePath=System.getProperty("user.dir")+"\\"+"src\\test\\resources\\regproc_qa.cfg.xml";
	static File registrationListConfigFile=new File(registrationListConfigFilePath);

	@SuppressWarnings("deprecation")
	public List<DemoDedupeDto> regproc_IndividualDemoghraphicDedupe(String regId)
	{
		factory = new Configuration().configure(registrationListConfigFile).buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		List<DemoDedupeDto> dto = getDemoById(session, regId);
		if(dto!=null && !dto.isEmpty())
		{
			session.close();
			factory.close();
			return dto;
		}
		return null;
	}

	private List<DemoDedupeDto> getDemoById(Session session2, String regId) {
		DemoDedupeDto demoDedupeDto = null;
		List<DemoDedupeDto> demoDedupeDtoList = null;
		String queryString = "Select * from regprc.individual_demographic_dedup where "
				+ "regprc.individual_demographic_dedup.reg_id =:regId";
		Query query = session.createSQLQuery(queryString);
		query.setParameter("regId", regId);

		List<Object> records = query.getResultList();
		Object[] TestData = null;
		// reading data retrieved from query
		if(records!=null && !records.isEmpty()){
			demoDedupeDtoList = new ArrayList<>();
			for (Object record : records) {
				demoDedupeDto = new DemoDedupeDto();
				TestData = (Object[]) record;
				demoDedupeDto.setRegId((String)TestData[0]);
				demoDedupeDto.setUin((String)TestData[1]);
				demoDedupeDto.setName((String)TestData[2]);
				demoDedupeDto.setDob((String)TestData[3]);
				demoDedupeDto.setGenderCode((String)TestData[4]);
				demoDedupeDto.setLangCode((String)TestData[5]);

				demoDedupeDtoList.add(demoDedupeDto);
			}

			logger.info("demoDedupeDtoList : "+demoDedupeDtoList);
		}

		session.getTransaction().commit();

		return demoDedupeDtoList;
	}

	public List<DemoDedupeDto> regproc_AllIndividualDemoghraphicDedupe(String name, String genderCode, String dob,
			String langCode) {

		factory = new Configuration().configure(registrationListConfigFile).buildSessionFactory();	
		session = factory.getCurrentSession();
		session.beginTransaction();

		List<DemoDedupeDto> dto = getAllDemo(session, name, genderCode, dob, langCode);
		if(dto!=null && !dto.isEmpty())
		{
			session.close();
			factory.close();
			return dto;
		}
		return null;
	}

	private List<DemoDedupeDto> getAllDemo(Session session2, String name, String genderCode, String dob,
			String langCode) {
		DemoDedupeDto demoDedupeDto = null;
		List<DemoDedupeDto> demoDedupeDtoList = null;
		String queryString = "Select * from regprc.individual_demographic_dedup where "
				+ "regprc.individual_demographic_dedup.name =:name and regprc.individual_demographic_dedup.gender =:genderCode "
				+ "and regprc.individual_demographic_dedup.dob =:dob and regprc.individual_demographic_dedup.lang_code =:langCode and "
				+ "regprc.individual_demographic_dedup.is_active =:isActive and "
				+ "regprc.individual_demographic_dedup.uin IS NOT NULL";
		Query query = session.createSQLQuery(queryString);
		query.setParameter("name", name);
		query.setParameter("genderCode", genderCode);
		query.setParameter("dob", dob);
		query.setParameter("langCode", langCode);
		query.setParameter("isActive", true);

		List<Object> records = query.getResultList();
		Object[] TestData = null;
		// reading data retrieved from query
		if(records!=null && !records.isEmpty()){
			demoDedupeDtoList = new ArrayList<>();
			for (Object record : records) {
				demoDedupeDto = new DemoDedupeDto();
				TestData = (Object[]) record;
				demoDedupeDto.setRegId((String)TestData[0]);
				demoDedupeDto.setUin((String)TestData[1]);
				demoDedupeDto.setName((String)TestData[2]);
				demoDedupeDto.setDob((String)TestData[3]);
				demoDedupeDto.setGenderCode((String)TestData[4]);
				demoDedupeDto.setLangCode((String)TestData[5]);

				demoDedupeDtoList.add(demoDedupeDto);
			}

			logger.info("demoDedupeDtoList : "+demoDedupeDtoList);
		}

		session.getTransaction().commit();

		return demoDedupeDtoList;
	}
}
