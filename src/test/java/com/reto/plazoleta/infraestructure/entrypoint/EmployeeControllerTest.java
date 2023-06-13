package com.reto.plazoleta.infraestructure.entrypoint;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class EmployeeControllerTest {

    @Test
    void test_assignOrderAndChangeStatusToPending_withMultipleIdOrdersValidAndTokenCorrect_shouldReturnStatusOKAndAssignedIdOrders() {

    }

    @Test
    void test_assignOrderAndChangeStatusToPending_withSingleValidIdOrderAndCorrectToken_shouldReturnOKStatusAndAssignedIdOrder() {

    }

    @Test
    void test_assignOrderAndChangeStatusToPending_withNonExistingIdOrdersAndCorrectToken_shouldReturnNotFoundStatus() {

    }

    @Test
    void test_assignOrderAndChangeStatusToPending_withNonExistingRestaurantForEmployeeAndCorrectToken_shouldReturnNotFoundStatus() {

    }

    @Test
    void test_assignOrderAndChangeStatusToPending_withOrderAlreadyAssignedAndCorrectToken_shouldReturnConflictStatus() {

    }
}