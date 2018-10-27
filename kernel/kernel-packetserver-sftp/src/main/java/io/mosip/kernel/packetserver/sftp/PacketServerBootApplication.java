package io.mosip.kernel.packetserver.sftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Packet Server spring boot application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class})
public class PacketServerBootApplication {

	/**
	 * main method which runs the server
	 * 
	 * @param args
	 *            params
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketServerBootApplication.class, args);

	}
}
