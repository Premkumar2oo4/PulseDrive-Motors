package com.pulsedrive.security;

import com.pulsedrive.entity.User;
import com.pulsedrive.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityRoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    private User customer;
    private User admin;

    private String customerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {

        // =====================================
        // CUSTOMER
        // =====================================

        customer = new User();

        ReflectionTestUtils.setField(
                customer,
                "id",
                4L
        );

        customer.setEmail(
                "customer@example.com"
        );

        customer.setRole(
                "CUSTOMER"
        );

        customer.setEnabled(true);


        // =====================================
        // ADMIN
        // =====================================

        admin = new User();

        ReflectionTestUtils.setField(
                admin,
                "id",
                2L
        );

        admin.setEmail(
                "prem@example.com"
        );

        admin.setRole(
                "ADMIN"
        );

        admin.setEnabled(true);


        // =====================================
        // GENERATE REAL JWT TOKENS
        // =====================================

        customerToken =
                jwtService.generateToken(
                        customer.getEmail(),
                        customer.getRole()
                );

        adminToken =
                jwtService.generateToken(
                        admin.getEmail(),
                        admin.getRole()
                );
    }


    // ==========================================
    // TEST 1
    // CUSTOMER CANNOT ACCESS ADMIN DASHBOARD
    // ==========================================

    @Test
    void customerShouldNotAccessAdminDashboard()
            throws Exception {

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(customer)
        );

        mockMvc.perform(
                        get("/api/admin/dashboard")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }


    // ==========================================
    // TEST 2
    // ADMIN CAN PASS ADMIN SECURITY
    // ==========================================

    @Test
    void adminShouldPassAdminSecurity()
            throws Exception {

        when(
                userRepository.findByEmail(
                        "prem@example.com"
                )
        ).thenReturn(
                Optional.of(admin)
        );

        mockMvc.perform(
                        get("/api/admin/dashboard")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )

                /*
                 * We mainly want to prove that
                 * Spring Security does NOT return 403.
                 *
                 * The actual controller may return
                 * another successful/application
                 * response depending on your DB.
                 */
                .andExpect(
                        result -> {

                            int status =
                                    result.getResponse()
                                            .getStatus();

                            if (status == 403) {

                                throw new AssertionError(
                                        "ADMIN received 403"
                                );
                            }
                        }
                );
    }


    // ==========================================
    // TEST 3
    // CUSTOMER CAN PASS CART SECURITY
    // ==========================================

    @Test
    void customerShouldPassCartSecurity()
            throws Exception {

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(customer)
        );

        mockMvc.perform(
                        get("/api/cart")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken
                                )
                )

                .andExpect(
                        result -> {

                            int status =
                                    result.getResponse()
                                            .getStatus();

                            if (status == 401 ||
                                    status == 403) {

                                throw new AssertionError(
                                        "Authenticated CUSTOMER was blocked"
                                );
                            }
                        }
                );
    }


    // ==========================================
    // TEST 4
    // INVALID JWT CANNOT ACCESS CART
    // ==========================================

    @Test
    void invalidJwtShouldNotAccessCart()
            throws Exception {

        mockMvc.perform(
                        get("/api/cart")
                                .header(
                                        "Authorization",
                                        "Bearer invalid-token"
                                )
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }


    // ==========================================
    // TEST 5
    // DISABLED CUSTOMER CANNOT ACCESS CART
    // ==========================================

    @Test
    void disabledCustomerShouldNotAccessCart()
            throws Exception {

        customer.setEnabled(false);

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(customer)
        );

        mockMvc.perform(
                        get("/api/cart")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken
                                )
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }


    // ==========================================
    // TEST 6
    // CUSTOMER CANNOT ACCESS ADMIN COUPONS
    // ==========================================

    @Test
    void customerShouldNotAccessAdminCoupons()
            throws Exception {

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(customer)
        );

        mockMvc.perform(
                        get("/api/admin/coupons")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }


    // ==========================================
    // TEST 7
    // CUSTOMER CANNOT ACCESS ADMIN USERS
    // ==========================================

    @Test
    void customerShouldNotAccessAdminUsers()
            throws Exception {

        when(
                userRepository.findByEmail(
                        "customer@example.com"
                )
        ).thenReturn(
                Optional.of(customer)
        );

        mockMvc.perform(
                        get("/api/admin/users")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }
}