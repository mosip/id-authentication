package app.dbaccess;

import java.io.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import app.util.PropertiesUtil;

public class DBUtil {

	public static Session obtainSession() {
		Session session = null;

//		SessionFactory sessionFactory = new AnnotationConfiguration().configure(new File("hibernate.cfg.xml"))
//				.buildSessionFactory();
//		SessionFactory sessionFactory = new Configuration().configure(new File("hibernate.cfg.xml"))
//				.buildSessionFactory();

		if ("default".equals(PropertiesUtil.ENVIRONMENT)) {
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			session = sessionFactory.openSession();

		} else {
			File configFile = loadConfigFile();
			SessionFactory sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
			session = sessionFactory.openSession();
		}
		return session;

	}

	private static File loadConfigFile() {
		File file = null;
		if ("qa".equals(PropertiesUtil.ENVIRONMENT)) {

			String regProcDBConfigFile = System.getProperty("user.dir") + "\\"
					+ "src\\main\\resources\\regProc_qa.cfg.xml";
			file = new File(regProcDBConfigFile);
			return file;
		} else if ("pt".equals(PropertiesUtil.ENVIRONMENT)) {
			String regProcDBConfigFile = System.getProperty("user.dir") + "\\"
					+ "src\\main\\resources\\regProc_pt.cfg.xml";
			file = new File(regProcDBConfigFile);
			return file;
		} else {
			String regProcDBConfigFile = System.getProperty("user.dir") + "\\"
					+ "src\\main\\resources\\hibernate.cfg.xml";
			file = new File(regProcDBConfigFile);
			return file;
		}

	}

}
