package com.reto.plazoleta.infraestructure.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void initializeTestEnvironment() {

    }

    @Test
    void test_getAllOrdersFilterByStatus_withTheFieldsSizeItemsAndStatusValidAndTokenValid_shouldReturnAStatusOKAndListFromOrdersPaginatedByStatus() {

    }

    @Test
    void test_getAllOrdersFilterByStatus_withTheFieldsSizeItemsAndStatusValidAndTokenValidButNoOrderFound_shouldReturnAStatusNotContent() {

    }

    @Test
    void test_getAllOrdersFilterByStatus_withTheFieldsSizeItemsAndStatusValidByTheDefaultValueAndTokenValid_shouldReturnListFromOrdersPaginatedWithASizeByPageDefaultAndOrderedByStatusDefaultAndStatusOK() {

    }
}