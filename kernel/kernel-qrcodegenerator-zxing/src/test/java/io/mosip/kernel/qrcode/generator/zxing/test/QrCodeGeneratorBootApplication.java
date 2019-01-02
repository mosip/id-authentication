package io.mosip.kernel.qrcode.generator.zxing.test;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"io.mosip.kernel.qrcode.generator.zxing.*"})
public class QrCodeGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(QrCodeGeneratorBootApplication.class, args);

	}

}
