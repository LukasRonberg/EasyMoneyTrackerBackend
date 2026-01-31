package app.money.tracker.backend.service;

import app.money.tracker.backend.dto.category.CategoryTotalResponse;
import app.money.tracker.backend.dto.transactions.CreateTransactionRequest;
import app.money.tracker.backend.dto.category.MonthlyTotalResponse;
import app.money.tracker.backend.dto.transactions.TransactionTotalResponse;
import app.money.tracker.backend.entity.AccountEntity;
import app.money.tracker.backend.entity.CategoryEntity;
import app.money.tracker.backend.entity.TransactionEntity;
import app.money.tracker.backend.entity.UserEntity;
import app.money.tracker.backend.repository.AccountRepository;
import app.money.tracker.backend.repository.CategoryRepository;
import app.money.tracker.backend.repository.TransactionRepository;
import app.money.tracker.backend.repository.UserRepository;
import app.money.tracker.backend.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserContext userContext;

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public UUID createTransaction(CreateTransactionRequest request) {

        UserEntity user = userRepository.findById(userContext.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Account does not belong to user");
        }

        CategoryEntity category = null;

        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        }

        OffsetDateTime now = OffsetDateTime.now();

        TransactionEntity transaction = TransactionEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .account(account)
                .category(category)
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

    public List<TransactionEntity> listTransactions(LocalDate fromDate,
                                                    LocalDate toDate,
                                                    UUID accountId,
                                                    UUID categoryId) {
        boolean noFilters = fromDate == null && toDate == null && accountId == null && categoryId == null;

        if (noFilters) {
            return transactionRepository.findByUserIdOrderByTransactionDateDesc(userContext.getCurrentUserId());
        }

        LocalDate resolvedFromDate = (fromDate != null) ? fromDate : LocalDate.of(1970, 1, 1);
        LocalDate resolvedToDate = (toDate != null) ? toDate : LocalDate.of(2999, 12, 31);

        if (accountId != null && categoryId != null) {
            return transactionRepository.findByUserIdAndAccountIdAndCategoryIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                    userContext.getCurrentUserId(),
                    accountId,
                    categoryId,
                    resolvedFromDate,
                    resolvedToDate
            );
        }

        if (categoryId != null) {
            return transactionRepository.findByUserIdAndCategoryIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                    userContext.getCurrentUserId(),
                    categoryId,
                    resolvedFromDate,
                    resolvedToDate
            );
        }

        if (accountId != null) {
            return transactionRepository.findByUserIdAndAccountIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                    userContext.getCurrentUserId(),
                    accountId,
                    resolvedFromDate,
                    resolvedToDate
            );
        }

        return transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                userContext.getCurrentUserId(),
                resolvedFromDate,
                resolvedToDate
        );
    }

    public List<CategoryTotalResponse> sumByCategory(LocalDate fromDate, LocalDate toDate, UUID accountId) {

        LocalDate resolvedFromDate = (fromDate != null) ? fromDate : LocalDate.of(1970, 1, 1);
        LocalDate resolvedToDate = (toDate != null) ? toDate : LocalDate.of(2999, 12, 31);

        return transactionRepository.sumByCategory(
                userContext.getCurrentUserId(),
                resolvedFromDate,
                resolvedToDate,
                accountId
        );
    }

    public List<MonthlyTotalResponse> sumMonthly(LocalDate fromDate, LocalDate toDate, UUID accountId) {

        LocalDate resolvedFromDate = (fromDate != null) ? fromDate : LocalDate.of(1970, 1, 1);
        LocalDate resolvedToDate = (toDate != null) ? toDate : LocalDate.of(2999, 12, 31);

        return transactionRepository.sumMonthly(
                userContext.getCurrentUserId(),
                resolvedFromDate,
                resolvedToDate,
                accountId
        );
    }

    public TransactionTotalResponse sumTotal(LocalDate fromDate, LocalDate toDate, UUID accountId, UUID categoryId) {

        LocalDate resolvedFromDate = (fromDate != null) ? fromDate : LocalDate.of(1970, 1, 1);
        LocalDate resolvedToDate = (toDate != null) ? toDate : LocalDate.of(2999, 12, 31);

        return transactionRepository.sumTotal(
                userContext.getCurrentUserId(),
                resolvedFromDate,
                resolvedToDate,
                accountId,
                categoryId
        );
    }
}