package ru.pshiblo.starter.feign;

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
        log.warn("Error request: \n" + response.request().toString());
        log.warn("Error response: \n" + response.toString());
        Response.Body body = response.body();
        if (body != null) {
            try(StringWriter stringWriter = new StringWriter();
                Reader reader = body.asReader(StandardCharsets.UTF_8))
            {
                reader.transferTo(stringWriter);
                String reason = stringWriter.toString();
                log.warn("Error request: {} with reason {}. Response body {}, code {}", response.request().url(), response.reason(), reason, response.status());
                return new IntegrationException(response.status(), reason);
            } catch (IOException e) {
                log.error("Error read body", e);
                return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            log.warn("Error request: {} with reason {}. Response body {}, code {}", response.request().url(), response.reason(), "null", response.status());
            return new IntegrationException(response.status(), "{ \"status\":500, \"error\":\"Internal error\" }");
        }
    }
}
