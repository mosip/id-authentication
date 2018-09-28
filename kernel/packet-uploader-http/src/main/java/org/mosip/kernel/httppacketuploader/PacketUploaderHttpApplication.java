package org.mosip.kernel.httppacketuploader;

import org.mosip.kernel.httppacketuploader.config.PacketFileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * Packet Uploader Http application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan
@EnableConfigurationProperties({ PacketFileStorageProperties.class })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class PacketUploaderHttpApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 *            params
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketUploaderHttpApplication.class, args);
	}
}
