package io.mosip.kernel.syncdata.test.integration;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.syncdata.entity.SyncJobDef;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.exception.SyncServiceException;
import io.mosip.kernel.syncdata.service.SyncJobDefService;
//import io.mosip.kernel.syncdata.syncjob.repository.SyncJobDefRepository;
import io.mosip.kernel.syncdata.test.TestBootApplication;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
public class SyncJobDefIntegrationTest {

	private List<SyncJobDef> syncJobDefs = null;

	@Autowired
	SyncJobDefService syncJobDefService;

	// @MockBean
	// private SyncJobDefRepository syncJobDefRepository;

	@Autowired
	private RestTemplate restTemplate;

	private static final String JSON_SYNC_JOB_DEF = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-04-02T07:49:18.454Z\", \"metadata\": null, \"response\": { \"syncJobDefinitions\": [ { \"id\": \"LCS_J00002\", \"name\": \"Login Credentials Sync\", \"apiName\": null, \"parentSyncJobId\": \"NULL\", \"syncFreq\": \"0 0 11 * * ?\", \"lockDuration\": \"NULL\"  ] }, \"errors\": null } ";
	private String TOKEN_EXPIRED_ERROR_MESSAGE = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-05-11T11:02:20.521Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-ATH-402\", \"message\": \"Token expired\" } ] }";
	private String FORBIDDEN_ERROR_MESSAGE = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-05-11T11:02:20.521Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-ATH-403\", \"message\": \"Forbidden\" } ] }";

	@Value("${mosip.kernel.syncdata.syncjob-base-url}")
	private String baseUri;

	private LocalDateTime lastUpdatedTime = LocalDateTime.now(ZoneOffset.UTC);

	private LocalDateTime currentTimeStamp = LocalDateTime.now(ZoneOffset.UTC).minusHours(1);

	 private String url;

	@Before
	public void setup() {
		url = new StringBuilder(baseUri).append("?lastupdatedtimestamp=").append(lastUpdatedTime).append("Z").toString();
		SyncJobDef syncJobDef = new SyncJobDef();
		syncJobDef.setApiName("sync");
		syncJobDef.setId("REGISRATION");
		syncJobDef.setLangCode("eng");
		syncJobDef.setLockDuration("10000");
		syncJobDefs = new ArrayList<>();
		syncJobDefs.add(syncJobDef);
	}

	/*
	 * @Test public void testSyncJob() {
	 * when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(
	 * Mockito.any(), Mockito.any())) .thenReturn(syncJobDefs);
	 * syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);
	 * 
	 * }
	 * 
	 * @Test public void syncJobDefNullLastUpdatedTimeTest(){
	 * when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(
	 * Mockito.any(), Mockito.any())) .thenReturn(syncJobDefs);
	 * syncJobDefService.getSyncJobDefDetails(null, currentTimeStamp); }
	 * 
	 * @Test public void testSyncJobDataAccessException() {
	 * when(syncJobDefRepository.findLatestByLastUpdatedTimeAndCurrentTimeStamp(
	 * Mockito.any(), Mockito.any())) .thenReturn(null);
	 * syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);
	 * 
	 * }
	 */

	@Test(expected = BadCredentialsException.class)
	@WithUserDetails(value = "reg-officer")
	public void syncJobDefUnAuthZException() throws Exception {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(url))
				.andRespond(withUnauthorizedRequest());
		syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);
	}

	@Test(expected = AccessDeniedException.class)
	@WithUserDetails(value = "reg-officer")
	public void syncJobDefForbiddenException() throws Exception {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(url))
				.andRespond(withStatus(HttpStatus.FORBIDDEN));
		syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);

	}

	@Test(expected = AuthNException.class)
	@WithUserDetails(value = "reg-officer")
	public void syncJobDefTokenExpiredException() throws Exception {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(url))
				.andRespond(withUnauthorizedRequest().body(TOKEN_EXPIRED_ERROR_MESSAGE));
		syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);

	}

	@Test(expected = AuthZException.class)
	@WithUserDetails(value = "reg-officer")
	public void syncJobForbiddenException() throws Exception {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(url))
				.andRespond(withStatus(HttpStatus.FORBIDDEN).body(FORBIDDEN_ERROR_MESSAGE));
		syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);

	}

	@Test(expected = SyncDataServiceException.class)
	@WithUserDetails(value = "reg-officer")
	public void syncJobSyncServiceException() throws Exception {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(url))
				.andRespond(withServerError());
		syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);

	}

	@Test(expected = SyncServiceException.class)
	@WithUserDetails(value = "reg-officer")
	public void syncJobServiceException() throws Exception {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(url))
				.andRespond(withSuccess().body(TOKEN_EXPIRED_ERROR_MESSAGE));
		syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);

	}

	@Test(expected = ParseResponseException.class)
	@WithUserDetails(value = "reg-officer")
	public void syncJobIOException() throws Exception {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(url))
				.andRespond(withSuccess().body(JSON_SYNC_JOB_DEF));
		syncJobDefService.getSyncJobDefDetails(lastUpdatedTime, currentTimeStamp);

	}
}
