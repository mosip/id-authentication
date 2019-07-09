package io.mosip.resgistrationProcessor.perf.dbaccess;

import java.io.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class DBUtil {

	public static Session obtainSession() {

//		SessionFactory sessionFactory = new AnnotationConfiguration().configure(new File("hibernate.cfg.xml"))
//				.buildSessionFactory();
//		SessionFactory sessionFactory = new Configuration().configure(new File("hibernate.cfg.xml"))
//				.buildSessionFactory();
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		return session;
	}

}
