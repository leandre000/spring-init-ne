package com.utility.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.billing.dto.AdminUserCreateRequest;
import com.utility.billing.dto.LoginRequest;
import com.utility.billing.dto.RegisterRequest;
import com.utility.billing.entity.User;
import com.utility.billing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        // Log in as admin to obtain access token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@utility.com");
        loginRequest.setPassword("Secret@123");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
        adminToken = (String) dataMap.get("accessToken");
    }

    @Test
    void testPublicRegisterDefaultsToCustomerAndPending() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Test Public Customer");
        registerRequest.setEmail("testcustomer@gmail.com");
        registerRequest.setPhoneNumber("+250780000010");
        registerRequest.setPassword("Secret@123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Test Public Customer"))
                .andExpect(jsonPath("$.data.email").value("testcustomer@gmail.com"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.roleName").value("ROLE_CUSTOMER"));

        User savedUser = userRepository.findByEmail("testcustomer@gmail.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getStatus()).isEqualTo("PENDING");
        assertThat(savedUser.getRole().getName()).isEqualTo("ROLE_CUSTOMER");
    }

    @Test
    void testAdminCreateUserSuccess() throws Exception {
        AdminUserCreateRequest createRequest = new AdminUserCreateRequest();
        createRequest.setFullName("Test Operator User");
        createRequest.setEmail("testoperator@utility.com");
        createRequest.setPhoneNumber("+250780000020");
        createRequest.setPassword("Secret@123");
        createRequest.setRoleName("ROLE_OPERATOR");

        mockMvc.perform(post("/api/v1/auth/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Test Operator User"))
                .andExpect(jsonPath("$.data.email").value("testoperator@utility.com"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.roleName").value("ROLE_OPERATOR"));

        User savedUser = userRepository.findByEmail("testoperator@utility.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getStatus()).isEqualTo("ACTIVE");
        assertThat(savedUser.getRole().getName()).isEqualTo("ROLE_OPERATOR");
    }

    @Test
    void testAdminCreateUserForbiddenForNonAdmin() throws Exception {
        // Log in as a customer
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("customer@utility.com");
        loginRequest.setPassword("Secret@123");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
        String customerToken = (String) dataMap.get("accessToken");

        AdminUserCreateRequest createRequest = new AdminUserCreateRequest();
        createRequest.setFullName("Unauthorized User");
        createRequest.setEmail("unauthorized@utility.com");
        createRequest.setPhoneNumber("+250780000030");
        createRequest.setPassword("Secret@123");
        createRequest.setRoleName("ROLE_OPERATOR");

        mockMvc.perform(post("/api/v1/auth/users")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAdminCreateUserUnauthorizedWithoutToken() throws Exception {
        AdminUserCreateRequest createRequest = new AdminUserCreateRequest();
        createRequest.setFullName("Unauthorized User");
        createRequest.setEmail("unauthorized@utility.com");
        createRequest.setPhoneNumber("+250780000030");
        createRequest.setPassword("Secret@123");
        createRequest.setRoleName("ROLE_OPERATOR");

        mockMvc.perform(post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
}
