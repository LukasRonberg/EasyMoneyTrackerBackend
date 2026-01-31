package app.money.tracker.backend.repository;

import app.money.tracker.backend.dto.category.CategoryTotalResponse;
import app.money.tracker.backend.dto.category.MonthlyTotalResponse;
import app.money.tracker.backend.dto.transactions.TransactionTotalResponse;
import app.money.tracker.backend.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findByUserIdOrderByTransactionDateDesc(UUID userId);

    List<TransactionEntity> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<TransactionEntity> findByUserIdAndAccountIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId,
            UUID accountId,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<TransactionEntity> findByUserIdAndCategoryIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId,
            UUID categoryId,
            LocalDate fromDate,
            LocalDate toDate
    );

    List<TransactionEntity> findByUserIdAndAccountIdAndCategoryIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId,
            UUID accountId,
            UUID categoryId,
            LocalDate fromDate,
            LocalDate toDate
    );

    @Query("""
    select new app.money.tracker.backend.dto.CategoryTotalResponse(
        c.id,
        coalesce(c.name, 'Uncategorized'),
        coalesce(sum(t.amount), 0)
    )
    from TransactionEntity t
    left join t.category c
    where t.user.id = :userId
      and t.transactionDate between :fromDate and :toDate
      and (:accountId is null or t.account.id = :accountId)
    group by c.id, c.name
    order by coalesce(sum(t.amount), 0) desc
""")
    List<CategoryTotalResponse> sumByCategory(
            @Param("userId") UUID userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("accountId") UUID accountId
    );

    @Query("""
    select new app.money.tracker.backend.dto.MonthlyTotalResponse(
        cast(function('date_trunc', 'month', t.transactionDate) as java.time.LocalDate),
        coalesce(sum(t.amount), 0)
    )
    from TransactionEntity t
    where t.user.id = :userId
      and t.transactionDate between :fromDate and :toDate
      and (:accountId is null or t.account.id = :accountId)
    group by function('date_trunc', 'month', t.transactionDate)
    order by function('date_trunc', 'month', t.transactionDate)
""")
    List<MonthlyTotalResponse> sumMonthly(
            @Param("userId") UUID userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("accountId") UUID accountId
    );

    @Query("""
    select new app.money.tracker.backend.dto.TransactionTotalResponse(
        coalesce(sum(t.amount), 0)
    )
    from TransactionEntity t
    where t.user.id = :userId
      and t.transactionDate between :fromDate and :toDate
      and (:accountId is null or t.account.id = :accountId)
      and (:categoryId is null or t.category.id = :categoryId)
""")
    TransactionTotalResponse sumTotal(
            @Param("userId") UUID userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("accountId") UUID accountId,
            @Param("categoryId") UUID categoryId
    );
}