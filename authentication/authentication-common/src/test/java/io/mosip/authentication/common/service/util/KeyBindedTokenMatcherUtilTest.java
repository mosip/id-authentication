package io.mosip.authentication.common.service.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RunWith(SpringRunner.class)
public class KeyBindedTokenMatcherUtilTest {

    @Mock
    KeymanagerUtil keymanagerUtil;
    @InjectMocks
    KeyBindedTokenMatcherUtil keyBindedTokenMatcherUtil;


    @Test
    public void matchTestWithInValidToken_thenFail()  {

        Map<String, Object> properties =new HashMap<>();
        Map<String, String> bindingCertificates =new HashMap<>();
        Map<String, String> input =new HashMap<>();
        input.put("individualId","individualId");
        input.put("type","type");
        input.put("format","jwt");
        input.put("token",".eyJ");

        try{
            keyBindedTokenMatcherUtil.match(input, bindingCertificates, properties);
        }catch (IdAuthenticationBusinessException e){
        }
    }
    @Test
    public void matchTestWithExpiredToken_thenFail()  {

        Map<String, Object> properties =new HashMap<>();
        Map<String, String> bindingCertificates =new HashMap<>();
        Map<String, String> input =new HashMap<>();
        input.put("individualId","individualId");
        input.put("type","type");
        input.put("format","jwt");
        input.put("token","eyJ0eXAiOiJKV1QiLCJ4NXQjUzI1NiI6IjBFSmtKMDYyWnZNZ0dKSk9BRVNYWFo1Tl9hamRDOG04Y0hPTXVKVVRGWUEiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE2OTg5ODgyMTcsIm5iZiI6MTY5ODk4ODIxNywiZXhwIjoxNjk4OTg4ODIyLCJqdGkiOiJYZkpRaGVfU3RuNTNmaWc3YVV3V3MiLCJhdWQiOiJpZGEtYmluZGluZyIsInN1YiI6IjQxNTg2MTI2MDkiLCJpc3MiOiJwb3N0bWFuLWluamkifQ.bSqcJZlq5PyAExwPoww41OF-vBIyaADZ8OsXzA_7gtowNl0kChVAB11eIPEcjuFvYeQiSpQgNZsS2-w84ZBdiqh72kkJQLjN7ItMKNf-cekNRmG6XFf1os1vom7CwrguataoYvboiiXYw0WUfsZTmnhcOKC8XN3qAsB2YAyYEnBJBeKy5aCNAfJiOULTMrqAqcu-A1MA_wtAkaCJggiNxf1-5bJWjZYyQOkis0nHmbgWjzzThdd6TzMkLnUyNxzO2n1E9A19OJ2ZH0ZN1d46c8QBMsYmGX-Kz8B8GBDnDlwC4M5g4hmxuXCN6sBcVjAONl92LxI1htSZ6muv3xL1YQ");

        try{
            keyBindedTokenMatcherUtil.match(input, bindingCertificates, properties);
        }catch (IdAuthenticationBusinessException e){
            Assert.assertEquals("IDA-KBT-002",e.getErrorCode());
        }
    }

    @Test
    public void matchTestWithInValidThumbprint_thenFail()  {

        Map<String, Object> properties =new HashMap<>();
        Map<String, String> bindingCertificates =new HashMap<>();
        Map<String, String> input =new HashMap<>();
        input.put("individualId","individualId");
        input.put("type","type");
        input.put("format","jwt");
        input.put("token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsInVzZSI6InNpZyIsImtpZCI6IkFBNzBWbUQ2OGltaUU2NDhSdDBzRWNhc1N3VkJDNGxtbno4ZUFPZmRYQzgiLCJhbGciOiJSUzI1NiIsIm4iOiJzZFNtY0o1bzJZX1JMOURORXFEQl9fV0xqV1RUY3JvRHpMaXRFRUxSb3h3MF9talV1azAyd1NlcTY3YlR4YzR0b2JMTS04Y19DNkVsNUgtQzlsTFF3VURZYnZ1VmxpX2lBLWtvZ0Rxa1dOTEEwbWZaLW9FRUtEYTlJV280VmY4MHl2Z09EN2hfSlpCWG53dHZjaTRqUlNaekc1R05sU2VJY25ZU0tiNVpvTTBOVDEtb0VrLU1tQnIxcmZGYWpGZ0hvMXhQSTQwUkN6UFhNWnkwOXY5Y2R5SVdTbWFzd2VRMmcxbEtsVHhqcVhibEtiTjZMQzNhUXc2azlFVVEzNjJ5QW5FMmZWOWpkcmNFb29RT0ZYZk1ZTHQ2UlFRMmJQSjIxb01aT0RGUjU0cHlVT0RxdlowMnczcnZMS0ozU2dvTHEtLVlDUTEzNi04cWJqNXpTVnNwMlEiLCJpYXQiOjE2OTg5OTQzMzR9.J4DNzGDNWE6AIIg7PAF8CZufFOOKA97ngBn1xMU05T9X_eqV9mfSk3G-fDXRRGS3ucS25gB2k6kOh7vt0eoVYEgw1lOQ2ERM2UoT7sWzUYvt0zedkP2zgkcubkeOwC-dY65_NiFRZ4iXudu38iLd2iQcAdwnp9e5HBfCFxiVkxIByMfGac6SwrCByNnPQnaiYn_988UKW7YVoqK4NK2kIJ405bz9kRWb8MMIRSTRskg0gYoQs9tCQGfD0QJWjJGk_Qqj2eDuH2pHresKELchjhe9hbbkajG021azpvPdq3t4PrYlqhiFajE-MRKwAR7Ey3_CfSHSoJ4mg2OBrhOVsA");

        try{
            keyBindedTokenMatcherUtil.match(input, bindingCertificates, properties);
        }catch (IdAuthenticationBusinessException e){
            Assert.assertEquals("IDA-MLC-009",e.getErrorCode());
        }
    }
    @Test
    public void matchTestWithInValidCerts_thenFail() throws IdAuthenticationBusinessException {
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "iatAdjSeconds", 30000000);
        Map<String, Object> properties =new HashMap<>();
        Map<String, String> bindingCertificates =new HashMap<>();
        Map<String, String> input =new HashMap<>();
        input.put("individualId","individualId");
        input.put("type","type");
        input.put("format","jwt");
        input.put("token","eyJ0eXAiOiJKV1QiLCJ4NXQjUzI1NiI6IjBFSmtKMDYyWnZNZ0dKSk9BRVNYWFo1Tl9hamRDOG04Y0hPTXVKVVRGWUEiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE2OTg5ODgyMTcsIm5iZiI6MTY5ODk4ODIxNywiZXhwIjoxNjk4OTg4ODIyLCJqdGkiOiJYZkpRaGVfU3RuNTNmaWc3YVV3V3MiLCJhdWQiOiJpZGEtYmluZGluZyIsInN1YiI6IjQxNTg2MTI2MDkiLCJpc3MiOiJwb3N0bWFuLWluamkifQ.bSqcJZlq5PyAExwPoww41OF-vBIyaADZ8OsXzA_7gtowNl0kChVAB11eIPEcjuFvYeQiSpQgNZsS2-w84ZBdiqh72kkJQLjN7ItMKNf-cekNRmG6XFf1os1vom7CwrguataoYvboiiXYw0WUfsZTmnhcOKC8XN3qAsB2YAyYEnBJBeKy5aCNAfJiOULTMrqAqcu-A1MA_wtAkaCJggiNxf1-5bJWjZYyQOkis0nHmbgWjzzThdd6TzMkLnUyNxzO2n1E9A19OJ2ZH0ZN1d46c8QBMsYmGX-Kz8B8GBDnDlwC4M5g4hmxuXCN6sBcVjAONl92LxI1htSZ6muv3xL1YQ");

        try {
            keyBindedTokenMatcherUtil.match(input, bindingCertificates, properties);
        }catch (IdAuthenticationBusinessException e){
            Assert.assertEquals("IDA-KBT-001",e.getErrorCode());
        }
    }

    @Test
    public void matchTestWithValidCerts_thenFail() throws IdAuthenticationBusinessException {
        ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "iatAdjSeconds", 300000000);
        Map<String, Object> properties =new HashMap<>();
        Map<String, String> bindingCertificates =new HashMap<>();
        bindingCertificates.put("D04264274EB666F32018924E0044975D9E4DFDA8DD0BC9BC70738CB895131580-TYPE","X509");
        Map<String, String> input =new HashMap<>();
        input.put("individualId","individualId");
        input.put("type","type");
        input.put("format","jwt");
        input.put("token","eyJ0eXAiOiJKV1QiLCJ4NXQjUzI1NiI6IjBFSmtKMDYyWnZNZ0dKSk9BRVNYWFo1Tl9hamRDOG04Y0hPTXVKVVRGWUEiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE2OTg5ODgyMTcsIm5iZiI6MTY5ODk4ODIxNywiZXhwIjoxNjk4OTg4ODIyLCJqdGkiOiJYZkpRaGVfU3RuNTNmaWc3YVV3V3MiLCJhdWQiOiJpZGEtYmluZGluZyIsInN1YiI6IjQxNTg2MTI2MDkiLCJpc3MiOiJwb3N0bWFuLWluamkifQ.bSqcJZlq5PyAExwPoww41OF-vBIyaADZ8OsXzA_7gtowNl0kChVAB11eIPEcjuFvYeQiSpQgNZsS2-w84ZBdiqh72kkJQLjN7ItMKNf-cekNRmG6XFf1os1vom7CwrguataoYvboiiXYw0WUfsZTmnhcOKC8XN3qAsB2YAyYEnBJBeKy5aCNAfJiOULTMrqAqcu-A1MA_wtAkaCJggiNxf1-5bJWjZYyQOkis0nHmbgWjzzThdd6TzMkLnUyNxzO2n1E9A19OJ2ZH0ZN1d46c8QBMsYmGX-Kz8B8GBDnDlwC4M5g4hmxuXCN6sBcVjAONl92LxI1htSZ6muv3xL1YQ");
        String certificateString="-----BEGIN CERTIFICATE-----\n" +
                "MIIC6jCCAdKgAwIBAgIGAYuT8Am8MA0GCSqGSIb3DQEBCwUAMDYxNDAyBgNVBAMM\n" +
                "K0FBNzBWbUQ2OGltaUU2NDhSdDBzRWNhc1N3VkJDNGxtbno4ZUFPZmRYQzgwHhcN\n" +
                "MjMxMTAzMDY0NzQzWhcNMjQwODI5MDY0NzQzWjA2MTQwMgYDVQQDDCtBQTcwVm1E\n" +
                "NjhpbWlFNjQ4UnQwc0VjYXNTd1ZCQzRsbW56OGVBT2ZkWEM4MIIBIjANBgkqhkiG\n" +
                "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsdSmcJ5o2Y/RL9DNEqDB//WLjWTTcroDzLit\n" +
                "EELRoxw0/mjUuk02wSeq67bTxc4tobLM+8c/C6El5H+C9lLQwUDYbvuVli/iA+ko\n" +
                "gDqkWNLA0mfZ+oEEKDa9IWo4Vf80yvgOD7h/JZBXnwtvci4jRSZzG5GNlSeIcnYS\n" +
                "Kb5ZoM0NT1+oEk+MmBr1rfFajFgHo1xPI40RCzPXMZy09v9cdyIWSmasweQ2g1lK\n" +
                "lTxjqXblKbN6LC3aQw6k9EUQ362yAnE2fV9jdrcEooQOFXfMYLt6RQQ2bPJ21oMZ\n" +
                "ODFR54pyUODqvZ02w3rvLKJ3SgoLq++YCQ136+8qbj5zSVsp2QIDAQABMA0GCSqG\n" +
                "SIb3DQEBCwUAA4IBAQAR4qLsRAmLc3iNcX2I+YXdHHh1Vmoje2xMELZwpGbXq9LE\n" +
                "ozKEQxjSoidwmXwH/m7biH0/X7w1fFgT3ZxgaCVk3BWF+oS691+nQZceORbWYGDg\n" +
                "fyyliMT/f25bIfqfqLnk1p6A6RyAGkU5ICPEchDKziX6X4AkbIYXGkcNwi7naSpo\n" +
                "VULtcruR7Q3yCnXLJC4hyT7q8dp2GsmUiB/xP5jw2WtxwJZy60Syea0h2e8GEAmn\n" +
                "K25CO5bPD/lEVhvwEzY2ZWg7ZMp1FE02fhFbSXpbF9BACy8UsEZ0Pcr0daWUtXTC\n" +
                "5xbRhnAbNF0ixcSvZFcZvPhHDSnmnjABuHmuCUAQ\n" +
                "-----END CERTIFICATE-----";
        Certificate certificate=convertToCertificate(certificateString);
        Mockito.when(keymanagerUtil.convertToCertificate(Mockito.anyString())).thenReturn(certificate);
        try{
            keyBindedTokenMatcherUtil.match(input, bindingCertificates, properties);
        }catch (IdAuthenticationBusinessException e){
            Assert.assertEquals("IDA-KBT-003",e.getErrorCode());
        }
    }

    public static Certificate convertToCertificate(String certData) {
        try {
            StringReader strReader = new StringReader(certData);
            PemReader pemReader = new PemReader(strReader);
            PemObject pemObject = pemReader.readPemObject();
            if (Objects.isNull(pemObject)) {
                throw new RuntimeException("Invalid certificate");
            }
            byte[] certBytes = pemObject.getContent();
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (IOException | CertificateException e) {
            throw new RuntimeException("Invalid certificate");
        }
    }
}
