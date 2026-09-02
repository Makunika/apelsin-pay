package ru.pshiblo.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import ru.pshiblo.auth.clients.PersonalInfoClient;
import ru.pshiblo.auth.clients.UsersClient;
import ru.pshiblo.auth.model.AuthUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationServerTest {

    private static final String REDIRECT_URI = "http://localhost:3000/login";
    private static final String CODE_VERIFIER = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersClient usersClient;
    @MockBean
    private PersonalInfoClient personalInfoClient;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        when(usersClient.getByUsername(eq(AuthTestSupport.LOGIN)))
                .thenReturn(AuthTestSupport.user(passwordEncoder.encode(AuthTestSupport.PASSWORD)));
        when(personalInfoClient.getByUserId(anyLong())).thenReturn(AuthTestSupport.personInfo());
        authUser = AuthUser.fromUser(AuthTestSupport.user("{bcrypt}x"), "test@example.com", "Пшибло Максим", "CONFIRMED", false);
    }

    @Test
    void clientCredentials_issuesJwtWithClientIdAndScopes() throws Exception {
        MvcResult result = mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("transaction-service", "transaction-secret"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials")
                        .param("scope", "server account_b_s"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andReturn();

        Jwt jwt = jwtDecoder.decode(accessToken(result));
        assertThat(jwt.getClaimAsString("client_id")).isEqualTo("transaction-service");
        assertThat(jwt.getClaimAsStringList("scope")).containsExactlyInAnyOrder("server", "account_b_s");
        assertThat(jwt.getClaims()).doesNotContainKey("user_name");
        assertThat(jwt.getIssuer().toString()).isEqualTo("http://localhost:5000");
    }

    @Test
    void clientCredentials_wrongSecret_isRejected() throws Exception {
        mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic("transaction-service", "wrong"))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "client_credentials"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwks_exposesSigningKey() throws Exception {
        mockMvc.perform(get("/oauth2/jwks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
                .andExpect(jsonPath("$.keys[0].kid").exists())
                .andExpect(jsonPath("$.keys[0].n").exists())
                .andExpect(jsonPath("$.keys[0].d").doesNotExist());
    }

    @Test
    void authorize_unauthenticated_redirectsToLoginPage() throws Exception {
        mockMvc.perform(authorizeRequest(AuthTestSupport.s256(CODE_VERIFIER)))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/index.html"));
    }

    @Test
    void authorizationCodeWithPkce_fullFlow_issuesUserJwt() throws Exception {
        String code = obtainCode();

        MvcResult tokenResult = mockMvc.perform(post("/oauth2/token")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("code", code)
                        .param("redirect_uri", REDIRECT_URI)
                        .param("client_id", "browser_main")
                        .param("code_verifier", CODE_VERIFIER))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"))
                .andExpect(jsonPath("$.access_token").exists())
                .andReturn();

        JsonNode body = objectMapper.readTree(tokenResult.getResponse().getContentAsString());
        // SAS 0.4.x не выдаёт refresh_token public-клиентам
        assertThat(body.has("refresh_token")).isFalse();

        Jwt jwt = jwtDecoder.decode(body.get("access_token").asText());
        assertThat(jwt.getClaimAsString("user_name")).isEqualTo(AuthTestSupport.LOGIN);
        assertThat((Object) jwt.getClaim("id")).isEqualTo((long) AuthTestSupport.USER_ID);
        assertThat(jwt.getClaimAsString("email")).isEqualTo("test@example.com");
        assertThat(jwt.getClaimAsString("name")).isEqualTo("Пшибло Максим");
        assertThat(jwt.getClaimAsString("status")).isEqualTo("CONFIRMED");
        assertThat(jwt.getClaimAsBoolean("lock")).isFalse();
        assertThat(jwt.getClaimAsStringList("authorities")).containsExactly("ROLE_USER");
        assertThat(jwt.getClaimAsStringList("scope")).containsExactly("user");
        assertThat(jwt.getClaimAsString("client_id")).isEqualTo("browser_main");
    }

    @Test
    void authorizationCode_withoutCodeVerifier_isRejected() throws Exception {
        String code = obtainCode();

        mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("code", code)
                        .param("redirect_uri", REDIRECT_URI)
                        .param("client_id", "browser_main"))
                // без code_verifier public-клиент вообще не аутентифицируется на /oauth2/token:
                // запрос считается анонимным и уходит на страницу логина, токен не выдаётся
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/index.html"))
                .andExpect(jsonPath("$.access_token").doesNotExist());
    }

    @Test
    void authorizationCode_withWrongCodeVerifier_isRejected() throws Exception {
        String code = obtainCode();

        mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("code", code)
                        .param("redirect_uri", REDIRECT_URI)
                        .param("client_id", "browser_main")
                        .param("code_verifier", "not-the-right-verifier-not-the-right-verifier-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    @Test
    void authorize_withoutCodeChallenge_isRejectedForPublicClient() throws Exception {
        mockMvc.perform(get("/oauth2/authorize")
                        .with(user(authUser))
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "browser_main")
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("scope", "user")
                        .queryParam("state", "xyz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, org.hamcrest.Matchers.containsString("error=invalid_request")));
    }

    @Test
    void formLogin_withLegacyHmacHash_succeeds() throws Exception {
        String legacyHash = ru.pshiblo.common.security.ApelsinPasswordEncoders
                .createDelegatingPasswordEncoder("test-hmac-salt")
                .encode(AuthTestSupport.PASSWORD);
        assertThat(legacyHash).startsWith("{bcrypt}");
        String rawHmac = new ru.pshiblo.common.security.Hmac512PasswordEncoder("test-hmac-salt").encode(AuthTestSupport.PASSWORD);
        when(usersClient.getByUsername(eq(AuthTestSupport.LOGIN))).thenReturn(AuthTestSupport.user(rawHmac));

        mockMvc.perform(post("/perform_login")
                        .param("username", AuthTestSupport.LOGIN)
                        .param("password", AuthTestSupport.PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/"));
    }

    @Test
    void formLogin_withLegacyHmacHash_upgradesHashToBcrypt() throws Exception {
        String rawHmac = new ru.pshiblo.common.security.Hmac512PasswordEncoder("test-hmac-salt").encode(AuthTestSupport.PASSWORD);
        when(usersClient.getByUsername(eq(AuthTestSupport.LOGIN))).thenReturn(AuthTestSupport.user(rawHmac));

        mockMvc.perform(post("/perform_login")
                        .param("username", AuthTestSupport.LOGIN)
                        .param("password", AuthTestSupport.PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/"));

        org.mockito.ArgumentCaptor<ru.pshiblo.auth.clients.dto.UpdatePasswordHash> captor =
                org.mockito.ArgumentCaptor.forClass(ru.pshiblo.auth.clients.dto.UpdatePasswordHash.class);
        org.mockito.Mockito.verify(usersClient).updatePasswordHash(eq(AuthTestSupport.USER_ID), captor.capture());
        String upgraded = captor.getValue().getPasswordHash();
        assertThat(upgraded).startsWith("{bcrypt}");
        assertThat(passwordEncoder.matches(AuthTestSupport.PASSWORD, upgraded)).isTrue();
        assertThat(passwordEncoder.upgradeEncoding(upgraded)).isFalse();
    }

    @Test
    void formLogin_withBcryptHash_doesNotCallUpgrade() throws Exception {
        mockMvc.perform(post("/perform_login")
                        .param("username", AuthTestSupport.LOGIN)
                        .param("password", AuthTestSupport.PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/"));

        org.mockito.Mockito.verify(usersClient, org.mockito.Mockito.never())
                .updatePasswordHash(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void formLogin_wrongPassword_redirectsWithError() throws Exception {
        mockMvc.perform(post("/perform_login")
                        .param("username", AuthTestSupport.LOGIN)
                        .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/index.html?error"));
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder authorizeRequest(String codeChallenge) {
        return get("/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "browser_main")
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("scope", "user")
                .queryParam("state", "xyz")
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256");
    }

    private String obtainCode() throws Exception {
        MvcResult result = mockMvc.perform(authorizeRequest(AuthTestSupport.s256(CODE_VERIFIER)).with(user(authUser)))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String location = result.getResponse().getHeader(HttpHeaders.LOCATION);
        assertThat(location).startsWith(REDIRECT_URI + "?");
        List<String> codes = UriComponentsBuilder.fromUriString(location).build().getQueryParams().get("code");
        List<String> states = UriComponentsBuilder.fromUriString(location).build().getQueryParams().get("state");
        assertThat(codes).hasSize(1);
        assertThat(states).containsExactly("xyz");
        return codes.get(0);
    }

    private String accessToken(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("access_token").asText();
    }
}
