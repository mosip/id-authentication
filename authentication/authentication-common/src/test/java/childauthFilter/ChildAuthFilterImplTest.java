package childauthFilter;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.childauthfilter.impl.ChildAuthFilterImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

public class ChildAuthFilterImplTest {

    private ChildAuthFilterImpl filter;

    @Before
    public void setUp() {
        filter = new ChildAuthFilterImpl();

        // Inject values normally assigned by @Value
        TestUtils.setField(filter, "dateOfBirthAttributeName", "dob");
        TestUtils.setField(filter, "dateOfBirthPattern", "yyyy-MM-dd");
        TestUtils.setField(filter, "childMaxAge", 6);
        TestUtils.setField(filter, "factorsDeniedForChild", new String[]{"otp", "bio"});
    }

    /** Utility for reflection */
    public static class TestUtils {
        static void setField(Object target, String field, Object value) {
            try {
                java.lang.reflect.Field f = target.getClass().getDeclaredField(field);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    private IdentityInfoDTO createDob(String dob) {
        IdentityInfoDTO dto = new IdentityInfoDTO();
        dto.setValue(dob);
        dto.setLanguage("en");
        return dto;
    }

    /**
     * Case 1: dob list missing → throws ID_NOT_AVAILABLE
     */
    @Test(expected = IdAuthenticationFilterException.class)
    public void testGetDobNullList() throws Exception {
        filter.validate(new AuthRequestDTO(), Collections.emptyMap(), new HashMap<>());
    }

    /**
     * Case 2: dob empty → exception
     */
    @Test(expected = IdAuthenticationFilterException.class)
    public void testGetDobEmptyDobList() throws Exception {
        Map<String, List<IdentityInfoDTO>> data = new HashMap<>();
        data.put("dob", Collections.emptyList());
        filter.validate(new AuthRequestDTO(), data, new HashMap<>());
    }

    /**
     * Case 3: dob value null → exception
     */
    @Test(expected = IdAuthenticationFilterException.class)
    public void testGetDobNullValue() throws Exception {
        IdentityInfoDTO dto = new IdentityInfoDTO();
        dto.setValue(null);
        Map<String, List<IdentityInfoDTO>> data = new HashMap<>();
        data.put("dob", Arrays.asList(dto));
        filter.validate(new AuthRequestDTO(), data, new HashMap<>());
    }

    @Test(expected = IdAuthenticationFilterException.class)
    public void testGetDobInvalidFormat() throws Exception {
        Map<String, List<IdentityInfoDTO>> data = new HashMap<>();
        data.put("dob", Arrays.asList(createDob("not-a-date")));
        filter.validate(new AuthRequestDTO(), data, new HashMap<>());
    }

    @Test
    public void testAdultNoError() throws Exception {
        String dob = LocalDate.now().minusYears(10).toString(); // Older than 6
        Map<String, List<IdentityInfoDTO>> data = new HashMap<>();
        data.put("dob", Arrays.asList(createDob(dob)));

        filter.validate(new AuthRequestDTO(), data, new HashMap<>());
    }

}


