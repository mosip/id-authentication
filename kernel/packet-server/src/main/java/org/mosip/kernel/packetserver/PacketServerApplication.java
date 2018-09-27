package org.mosip.kernel.packetserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * Packet Server spring boot application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@SpringBootApplication
@PropertySource(value = { "classpath:packet-server-configuration.properties" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class PacketServerApplication {

	/**
	 * main method which runs the server
	 * 
	 * @param args params
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketServerApplication.class, args);

	}
}
