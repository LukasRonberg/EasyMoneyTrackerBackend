package app.money.tracker.backend.service;

import app.money.tracker.backend.dto.CreateTransactionRequest;
import app.money.tracker.backend.dto.TransactionResponse;
import app.money.tracker.backend.entity.AccountEntity;
import app.money.tracker.backend.entity.TransactionEntity;
import app.money.tracker.backend.entity.UserEntity;
import app.money.tracker.backend.repository.AccountRepository;
import app.money.tracker.backend.repository.TransactionRepository;
import app.money.tracker.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final UUID TEST_USER_ID = UUID.fromString("0d55a075-ac15-46c1-84f9-fa175c0de90d");

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public UUID createTransaction(CreateTransactionRequest request) {

        UserEntity user = userRepository.findById(TEST_USER_ID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Account does not belong to user");
        }

        OffsetDateTime now = OffsetDateTime.now();

        TransactionEntity transaction = TransactionEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .account(account)
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .merchant(request.getMerchant())
                .createdAt(now)
                .updatedAt(now)
                .build();

        transactionRepository.save(transaction);

        return transaction.getId();
    }

    public List<TransactionEntity> listTransactions(LocalDate fromDate, LocalDate toDate, UUID accountId) {

        if (fromDate == null && toDate == null && accountId == null) {
            return transactionRepository.findByUserIdOrderByTransactionDateDesc(TEST_USER_ID);
        }

        LocalDate resolvedFromDate = (fromDate != null) ? fromDate : LocalDate.of(1970, 1, 1);
        LocalDate resolvedToDate = (toDate != null) ? toDate : LocalDate.of(2999, 12, 31);

        if (accountId != null) {
            return transactionRepository.findByUserIdAndAccountIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                    TEST_USER_ID,
                    accountId,
                    resolvedFromDate,
                    resolvedToDate
            );
        }

        return transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                TEST_USER_ID,
                resolvedFromDate,
                resolvedToDate
        );
    }
}