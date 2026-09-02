package ru.pshiblo.users.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.pshiblo.security.config.SecurityConfig;
import ru.pshiblo.users.service.RegisterService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalUserController.class)
@ImportAutoConfiguration(SecurityConfig.class)
class InternalUserControllerTest {

    private static final String BODY = "{\"passwordHash\":\"{bcrypt}$2a$10$abcdefghijklmnopqrstuv\"}";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegisterService registerService;
    @MockBean
    private JwtDecoder jwtDecoder;
    /** @EnableJpaAuditing на UsersApplication попадает в MVC-срез — без JPA его нечем наполнить */
    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void withoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(put("/internal/users/1/password-hash").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        verify(registerService, never()).updatePasswordHash(anyInt(), anyString());
    }

    @Test
    void userTokenWithoutServerScope_isForbidden() throws Exception {
        mockMvc.perform(put("/internal/users/1/password-hash")
                        .with(jwt().jwt(j -> j.claim("scope", "user").claim("lock", false))
                                .authorities(SecurityConfig.jwtGrantedAuthoritiesConverter()))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
        verify(registerService, never()).updatePasswordHash(anyInt(), anyString());
    }

    @Test
    void serverToken_updatesHash() throws Exception {
        mockMvc.perform(put("/internal/users/7/password-hash")
                        .with(jwt().jwt(j -> j.claim("scope", java.util.List.of("server")).claim("client_id", "auth-service"))
                                .authorities(SecurityConfig.jwtGrantedAuthoritiesConverter()))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk());
        verify(registerService).updatePasswordHash(7, "{bcrypt}$2a$10$abcdefghijklmnopqrstuv");
    }
}
