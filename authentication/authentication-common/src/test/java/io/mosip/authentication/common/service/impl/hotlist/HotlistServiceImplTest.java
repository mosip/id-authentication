package io.mosip.authentication.common.service.impl.hotlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.common.service.entity.HotlistCache;
import io.mosip.authentication.common.service.repository.HotlistCacheRepository;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class HotlistServiceImplTest {

    /** The Hostlist Service impl. */
    @InjectMocks
    private HotlistServiceImpl hotlistServiceImpl;

    @Mock
    private HotlistCacheRepository hotlistCacheRepo;

    /** The object mapper. */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Before.
     */
    @Before
    public void Before(){
    }

    @Test
    public void initTest(){
        List<String> idTypes = new ArrayList<String>();
        ReflectionTestUtils.setField(hotlistServiceImpl, "idTypes", idTypes);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "init");
    }

    /**
     * This class tests the updateHotlist method
     */
    @Test
    public void updateHotlistTest(){
        String id=null, idType=null, status=null;
        LocalDateTime expiryTimestamp=null;
        Optional<HotlistCache> hotlistData = Optional.of(new HotlistCache());
        Mockito.when(hotlistCacheRepo.findByIdHashAndIdType(id, idType)).thenReturn(hotlistData);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "updateHotlist", id, idType, status, expiryTimestamp);
        //
        //when hotlistData is empty
        hotlistData = Optional.empty();
        Mockito.when(hotlistCacheRepo.findByIdHashAndIdType(id, idType)).thenReturn(hotlistData);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "updateHotlist", id, idType, status, expiryTimestamp);
    }

    /**
     * This class tests the unblock method
     */
    @Test
    public void unblockTest(){
        String id=null; String idType=null;
        Optional<HotlistCache> hotlistData = Optional.of(new HotlistCache());
        Mockito.when(hotlistCacheRepo.findByIdHashAndIdType(id, idType)).thenReturn(hotlistData);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "unblock", id, idType);
    }

    /**
     * This class tests the getHotlistStatus method
     */
    @Test
    public void getHotlistStatusTest(){
        String id =null, idType = null;
        Optional<HotlistCache> hotlistData = Optional.of(new HotlistCache());
        //
        //when ExpiryDTimes = null
        hotlistData.get().setExpiryDTimes(null);
        Mockito.when(hotlistCacheRepo.findByIdHashAndIdType(id, idType)).thenReturn(hotlistData);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "getHotlistStatus", id,idType);
        //
        //ExpiryDTimes is not null and status = BLOCKED
        LocalDateTime now = LocalDateTime.now().plusYears(1).plusMonths(1).plusWeeks(1).plusDays(1);
        hotlistData.get().setExpiryDTimes(now);
        hotlistData.get().setStatus("BLOCKED");
        Mockito.when(hotlistCacheRepo.findByIdHashAndIdType(id, idType)).thenReturn(hotlistData);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "getHotlistStatus", id,idType);
        //
        //status = UNBLOCKED
        hotlistData.get().setStatus("UNBLOCKED");
        Mockito.when(hotlistCacheRepo.findByIdHashAndIdType(id, idType)).thenReturn(hotlistData);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "getHotlistStatus", id,idType);
        //
        //when hotlistData is empty
        hotlistData = Optional.empty();
        Mockito.when(hotlistCacheRepo.findByIdHashAndIdType(id, idType)).thenReturn(hotlistData);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "getHotlistStatus", id, idType);
    }

    /**
     * This class tests the handlingHotlistEvent method
     * @throws IOException
     */
    @Test
    public void handlingHotlistingEventTest() throws IOException {
        Map<String, Object> eventData = getEventData();
        Event event = new Event();
        event.setData(eventData);
        EventModel eventModel = new EventModel();
        eventModel.setEvent(event);
        List<String> idTypes = new ArrayList<String>();
        //when idType=UIN or VID
        idTypes.add("UIN");
        idTypes.add("VID");
        idTypes.add("BLOCKED");
        idTypes.add("UNBLOCKED");
        ReflectionTestUtils.setField(hotlistServiceImpl, "idTypes", idTypes);
//       when status=UNBLOCKED && id type =UIN
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "handlingHotlistingEvent", eventModel);
//        idTypes.contains(eventData.get(ID_TYPE)=false
        eventData.put("idType", "DEVICE");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "handlingHotlistingEvent", eventModel);
//       when status=UNBLOCKED && id type =VID
        eventData.put("idType", "VID");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "handlingHotlistingEvent", eventModel);
//       when status=UNBLOCKED && Objects.isNull(expiryTimestamp)=false
        eventData.put("expiryTimestamp" , "2007-12-03T10:15:30");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "handlingHotlistingEvent", eventModel);
//      when status = BLOCKED && hotlistEventData.containsKey(EXPIRY_TIMESTAMP)=true
        eventData.put("status", "BLOCKED");
        eventData.put("expiryTimestamp" , null);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "handlingHotlistingEvent", eventModel);
//        Objects.nonNull(eventData)=false && !((Map) eventData).isEmpty()=false
        eventData.clear();
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "handlingHotlistingEvent", eventModel);
        eventData=null;
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "handlingHotlistingEvent", eventModel);
    }

    /**
     * This class tests the validateHotlistEvent method
     * @throws IOException
     */
    @Test
    public void validateHotlistEventTest() throws IOException {
        Map<String, Object> eventData = getEventData();
        eventData.remove("status");
//        when hotlistEventData.containsKey(ID)=false
        eventData.remove("id");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "validateHotlistEventData", eventData);
//        when hotlistEventData.containsKey(ID)=true
        eventData.put("id", "13E0F1FD2C6B21F33CE5B24D1ACDCCDD02858D2ED91018663425CCB77B5A9799");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "validateHotlistEventData", eventData);
//        when hotlistEventData.containsKey(ID_TYPE)=false
        eventData.remove("idType");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "validateHotlistEventData", eventData);
//        when hotlistEventData.containsKey(ID_TYPE)=true
        eventData.put("idType", "UIN");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "validateHotlistEventData", eventData);
//        when hotlistEventData.containsKey(STATUS)=false
        eventData.remove("status");
        LocalDateTime now = LocalDateTime.now().plusYears(1).plusMonths(1).plusWeeks(1).plusDays(1);
        eventData.put("expiryTimestamp", now);
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "validateHotlistEventData", eventData);
//        when hotlistEventData.containsKey(EXPIRY_TIMESTAMP)=false
        eventData.remove("expiryTimestamp");
        ReflectionTestUtils.invokeMethod(hotlistServiceImpl, "validateHotlistEventData", eventData);
    }

    private Map<String, Object> getEventData() throws IOException {
        Map<String, Object> eventData = objectMapper.readValue(getEventDataJsonStr(), Map.class);
        eventData.put("idType", "UIN");
        eventData.put("id", "13E0F1FD2C6B21F33CE5B24D1ACDCCDD02858D2ED91018663425CCB77B5A9799");
        eventData.put("status", "UNBLOCKED");
        eventData.put("expiryTimestamp", null);
        return eventData;
    }

    private String getEventDataJsonStr(){
        return "{\"SALT\":\"Q74F5OnTZdw5qiOFp6h6Ww\",\"MODULO\":\"943\",\"expiry_timestamp\":null,\"transaction_limit\":null,\"id_hash\":\"9DCF43F9973826A8331209CAA22A8080995420D992D0BBEE2A3356077EA525E3\",\"TOKEN\":\"362737013453447806883457690320262449\",\"demoEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4GYkjXPjxZHR4MFHob_CKM9Wx0ZOpy0oA8Kcv-0kI1GEDfl070vppDaS0gG30P6QOUy5aEYY4nXffc0nqqrqdC4rzY4LdWbrxxkoyx1Q4BhNZHiA7Tm931-kjdC2YJkDFMHburu72N8CuI6wktAuhQPagWCGL2hGUkdbcFhvD8045Y9oggLfDYNH1Oaj9XIwEEvyAaHvH8mfJxQ5aiLp23mt6PA3QZ4uaVxMvprYBGR8CypGZIBKLfCCfamHsW01ae33mPFyQH1AKKIaJ1XaoeoIgJq9ocUZ292hl3rtgdOuj6eJmcrMOwpiOhHNMY0ndAuH1-RMWdY6CZ2FG-o5VI\",\"demoRankomKeyIndex\":\"4150\",\"bioEncryptedRandomKey\":\"1v87FAFPnjRzKo8mF6K715e0EXv-rnqF9_aJoN5OUp4U5vX_6IU_5l5gi_ATfr1cgICFAViR06A9LrDq8J01i_ICoHaCMqmKMBVk3B4lJ8zAyyYDbM4ztLYIDK95ozZArJuB4i3aJp7lh2XhEqn4xpSzLXJRIjKGuHckq-81IxQmozgo1eE07NlJ3-7tt9thjWujgEmJABpir893tZnxG3br9yAecqUTbnjxrXpvNRdCJm7SgwgJ-tQyE49QvDeXWw_ucXnx1SyQ99eeDafMzgc4JffrQeFCWwryo0q6TzgJ9qAm8SITAy0sc3Q3BJgddCnFH0s1GHjKPBsJBxknb_N3b5zB2yXomVn3bsKV7V-bXiC8Gf9CNj0yZf9xV-Ns\",\"bioRankomKeyIndex\":\"8678\",\"proof\": null,\"credentialType\":\"auth\",\"protectionKey\":null}";
    }

}
