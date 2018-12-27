package io.mosip.kernel.qrcode.generator.test;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"io.mosip.kernel.*"})
public class QrCodeGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(QrCodeGeneratorBootApplication.class, args);

	}

}
