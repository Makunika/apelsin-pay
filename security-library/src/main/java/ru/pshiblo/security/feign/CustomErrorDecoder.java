package ru.pshiblo.security.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.pshiblo.common.exception.IntegrationException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    private final static Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        try(StringWriter stringWriter = new StringWriter();
            Reader reader = response.body().asReader(StandardCharsets.UTF_8))
        {
            reader.transferTo(stringWriter);
            String reason = stringWriter.toString();
            log.warn("Error request: {} with reason {}. Response body {}, code {}", response.request().url(), response.reason(), reason, response.status());
            return new IntegrationException(response.status(), reason);
        } catch (IOException e) {
            log.error("Error read body", e);
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
