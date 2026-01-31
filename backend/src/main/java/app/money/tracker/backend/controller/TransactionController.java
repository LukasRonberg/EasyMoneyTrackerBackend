package app.money.tracker.backend.controller;

import app.money.tracker.backend.dto.CategoryTotalResponse;
import app.money.tracker.backend.dto.CreateTransactionRequest;
import app.money.tracker.backend.dto.TransactionResponse;
import app.money.tracker.backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<UUID> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        UUID transactionId = transactionService.createTransaction(request);
        return ResponseEntity.ok(transactionId);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactions(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) UUID categoryId) {
        List<TransactionResponse> transactions = transactionService
                .listTransactions(fromDate, toDate, accountId,categoryId)
                .stream()
                .map(transaction -> TransactionResponse.builder()
                        .id(transaction.getId())
                        .accountId(transaction.getAccount().getId())
                        .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                        .amount(transaction.getAmount())
                        .transactionDate(transaction.getTransactionDate())
                        .description(transaction.getDescription())
                        .merchant(transaction.getMerchant())
                        .createdAt(transaction.getCreatedAt())
                        .updatedAt(transaction.getUpdatedAt())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/summary/categories")
    public ResponseEntity<List<CategoryTotalResponse>> categorySummary(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) UUID accountId
    ) {
        return ResponseEntity.ok(transactionService.sumByCategory(fromDate, toDate, accountId));
    }
}