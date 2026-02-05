package app.money.tracker.controller;

import app.money.tracker.backend.config.ApiExceptionHandler;
import app.money.tracker.backend.controller.TransactionController;
import app.money.tracker.backend.dto.transactions.CreateTransactionRequest;
import app.money.tracker.backend.dto.transactions.TransactionTotalResponse;
import app.money.tracker.backend.entity.AccountEntity;
import app.money.tracker.backend.entity.CategoryEntity;
import app.money.tracker.backend.entity.TransactionEntity;
import app.money.tracker.backend.service.TransactionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
@Import(ApiExceptionHandler.class)
public class TransactionControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    public void createTransaction_shouldReturn200AndUuidBody() throws Exception {
        UUID transactionId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        CreateTransactionRequest createTransactionRequest = new CreateTransactionRequest();
        createTransactionRequest.setAccountId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        createTransactionRequest.setCategoryId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        createTransactionRequest.setAmount(new BigDecimal("123.45"));
        createTransactionRequest.setTransactionDate(LocalDate.of(2026, 1, 31));
        createTransactionRequest.setDescription("Groceries");
        createTransactionRequest.setMerchant("NETTO");

        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(transactionId);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + transactionId + "\""));

        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void createTransaction_shouldReturn400_whenAccountIdIsMissing() throws Exception {
        String invalidJsonBody = """
                {
                  "amount": 10.00,
                  "transactionDate": "2026-01-31"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(transactionService);
    }

    @Test
    public void createTransaction_shouldReturn400_whenAmountIsNotPositive() throws Exception {
        String invalidJsonBody = """
                {
                  "accountId": "22222222-2222-2222-2222-222222222222",
                  "amount": 0,
                  "transactionDate": "2026-01-31"
                }
                """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(transactionService);
    }

    @Test
    public void createTransaction_shouldReturn400_whenServiceThrowsIllegalArgumentException() throws Exception {
        UUID accountId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        CreateTransactionRequest createTransactionRequest = new CreateTransactionRequest();
        createTransactionRequest.setAccountId(accountId);
        createTransactionRequest.setAmount(new BigDecimal("10.00"));
        createTransactionRequest.setTransactionDate(LocalDate.of(2026, 1, 31));

        when(transactionService.createTransaction(any(CreateTransactionRequest.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void listTransactions_shouldReturn200AndMappedTransactionResponses() throws Exception {
        UUID transactionId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID accountId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID categoryId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

        OffsetDateTime createdAt = OffsetDateTime.parse("2026-01-31T10:15:30+01:00");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-01-31T10:20:30+01:00");

        AccountEntity accountEntity = mock(AccountEntity.class);
        when(accountEntity.getId()).thenReturn(accountId);

        CategoryEntity categoryEntity = mock(CategoryEntity.class);
        when(categoryEntity.getId()).thenReturn(categoryId);

        TransactionEntity transactionEntity = mock(TransactionEntity.class);
        when(transactionEntity.getId()).thenReturn(transactionId);
        when(transactionEntity.getAccount()).thenReturn(accountEntity);
        when(transactionEntity.getCategory()).thenReturn(categoryEntity);
        when(transactionEntity.getAmount()).thenReturn(new BigDecimal("99.99"));
        when(transactionEntity.getTransactionDate()).thenReturn(LocalDate.of(2026, 1, 31));
        when(transactionEntity.getDescription()).thenReturn("Coffee");
        when(transactionEntity.getMerchant()).thenReturn("7-Eleven");
        when(transactionEntity.getCreatedAt()).thenReturn(createdAt);
        when(transactionEntity.getUpdatedAt()).thenReturn(updatedAt);

        when(transactionService.listTransactions(null, null, null, null)).thenReturn(List.of(transactionEntity));

        mockMvc.perform(get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].accountId").value(accountId.toString()))
                .andExpect(jsonPath("$[0].categoryId").value(categoryId.toString()))
                .andExpect(jsonPath("$[0].amount").value(99.99))
                .andExpect(jsonPath("$[0].transactionDate").value("2026-01-31"))
                .andExpect(jsonPath("$[0].description").value("Coffee"))
                .andExpect(jsonPath("$[0].merchant").value("7-Eleven"))
                .andExpect(jsonPath("$[0].createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("$[0].updatedAt").value(updatedAt.toString()));

        verify(transactionService, times(1)).listTransactions(null, null, null, null);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void listTransactions_shouldReturn200_whenTransactionHasNullCategory() throws Exception {
        UUID transactionId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        UUID accountId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

        AccountEntity accountEntity = mock(AccountEntity.class);
        when(accountEntity.getId()).thenReturn(accountId);

        TransactionEntity transactionEntity = mock(TransactionEntity.class);
        when(transactionEntity.getId()).thenReturn(transactionId);
        when(transactionEntity.getAccount()).thenReturn(accountEntity);
        when(transactionEntity.getCategory()).thenReturn(null);
        when(transactionEntity.getAmount()).thenReturn(new BigDecimal("1.00"));
        when(transactionEntity.getTransactionDate()).thenReturn(LocalDate.of(2026, 1, 31));

        when(transactionService.listTransactions(null, null, null, null)).thenReturn(List.of(transactionEntity));

        mockMvc.perform(get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].accountId").value(accountId.toString()))
                .andExpect(jsonPath("$[0].categoryId").doesNotExist());

        verify(transactionService, times(1)).listTransactions(null, null, null, null);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void totalSummary_shouldReturn200AndTotal() throws Exception {
        TransactionTotalResponse transactionTotalResponse = new TransactionTotalResponse(new BigDecimal("1234.56"));

        when(transactionService.sumTotal(null, null, null, null)).thenReturn(transactionTotalResponse);

        mockMvc.perform(get("/api/transactions/summary/total")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(1234.56));

        verify(transactionService, times(1)).sumTotal(null, null, null, null);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void categorySummary_shouldReturn200() throws Exception {
        when(transactionService.sumByCategory(null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/summary/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).sumByCategory(null, null, null);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void monthlySummary_shouldReturn200() throws Exception {
        when(transactionService.sumMonthly(null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/summary/monthly")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).sumMonthly(null, null, null);
        verifyNoMoreInteractions(transactionService);
    }
}