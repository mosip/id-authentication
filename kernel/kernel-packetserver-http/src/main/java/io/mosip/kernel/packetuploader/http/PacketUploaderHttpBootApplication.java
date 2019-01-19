package io.mosip.kernel.packetuploader.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import io.mosip.kernel.packetuploader.http.config.PacketFileStorageProperties;

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
public class PacketUploaderHttpBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 *            params
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketUploaderHttpBootApplication.class, args);
	}
}
