package io.mosip.kernel.packetuploader.sftp.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"io.mosip.kernel.*"})
public class PacketUploaderSftpBootApplication {
public static void main(String[] args) throws Exception {
	SpringApplication.run(PacketUploaderSftpBootApplication.class, args);
}

}
