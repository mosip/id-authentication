/*
package io.mosip.registration.packet.decryptor.job;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.packet.decryptor.job.PacketDecryptorJobApplication;
import io.mosip.registration.processor.packet.decryptor.job.schedule.PacketDecryptionScheduler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=PacketDecryptorJobApplication.class)
public class PacketDecryptionScedulerTest {

	@SpyBean
	private PacketDecryptionScheduler packetDecryptionScheduler;
	
	
	
	@Test
    public void packetDecryptionSchedulerTest() {
	 Awaitility.await().atMost(Duration.TEN_SECONDS)
               .untilAsserted(() -> verify(packetDecryptionScheduler, times(1)).packetDecryptorJobScheduler());
    }
	
	
	 
}
*/
