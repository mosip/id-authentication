package io.mosip.resident.util;

import com.google.gson.Gson;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.dto.ClientIdSecretKeyRequestDto;
import io.mosip.resident.dto.TokenRequestDto;
import io.mosip.resident.exception.TokenGenerationFailedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TokenGenerator {

    private static Logger logger = LoggerConfiguration.logConfig(TokenGenerator.class);

    @Autowired
    Environment environment;


    /**
     * This method gets the token for the user details present in config server.
     *
     * @return
     * @throws IOException
     */
    public String getToken() throws IOException {
        return generateToken(setRequestDto());
    }

    public String getRegprocToken() throws IOException {
        return generateToken(setRegprcRequestDto());
    }
    
    public String getAdminToken() throws IOException {
    	return generateToken(setAdminRequestDto());
    }

    private String generateToken(ClientIdSecretKeyRequestDto dto) throws IOException {
        // TokenRequestDTO<PasswordRequest> tokenRequest = new
        // TokenRequestDTO<PasswordRequest>();
        TokenRequestDto tokenRequest = new TokenRequestDto();
        tokenRequest.setId(environment.getProperty("token.request.id"));

        tokenRequest.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
        // tokenRequest.setRequest(setPasswordRequestDTO());
        tokenRequest.setRequest(dto);
        tokenRequest.setVersion(environment.getProperty("token.request.version"));

        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        // HttpPost post = new
        // HttpPost(environment.getProperty("PASSWORDBASEDTOKENAPI"));
        HttpPost post = new HttpPost(environment.getProperty("KERNELAUTHMANAGER"));
        try {
            StringEntity postingString = new StringEntity(gson.toJson(tokenRequest));
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(post);
            org.apache.http.HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity, "UTF-8");
            logger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                    LoggerFileConstant.APPLICATIONID.toString(), "Resonse body=> " + responseBody);
            Header[] cookie = response.getHeaders("Set-Cookie");
            if (cookie.length == 0)
                throw new TokenGenerationFailedException();
            String token = response.getHeaders("Set-Cookie")[0].getValue();
            logger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                    LoggerFileConstant.APPLICATIONID.toString(), "Cookie => " + cookie[0]);

            return token.substring(0, token.indexOf(';'));
        } catch (IOException e) {
            logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
                    LoggerFileConstant.APPLICATIONID.toString(), e.getMessage() + ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    private ClientIdSecretKeyRequestDto setRequestDto() {
        ClientIdSecretKeyRequestDto request = new ClientIdSecretKeyRequestDto();
        request.setAppId(environment.getProperty("resident.appid"));
        request.setClientId(environment.getProperty("resident.clientId"));
        request.setSecretKey(environment.getProperty("resident.secretKey"));
        return request;
    }

    private ClientIdSecretKeyRequestDto setRegprcRequestDto() {
        ClientIdSecretKeyRequestDto request = new ClientIdSecretKeyRequestDto();
        request.setAppId(environment.getProperty("regprc.appid"));
        request.setClientId(environment.getProperty("regprc.clientId"));
        request.setSecretKey(environment.getProperty("regprc.secretKey"));
        return request;
    }
    
    
    private ClientIdSecretKeyRequestDto setAdminRequestDto() {
        ClientIdSecretKeyRequestDto request = new ClientIdSecretKeyRequestDto();
        request.setAppId(environment.getProperty("admin.appid"));
        request.setClientId(environment.getProperty("admin.clientId"));
        request.setSecretKey(environment.getProperty("admin.secretKey"));
        return request;
    }

}
