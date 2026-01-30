package app.money.tracker.backend.repository;

import app.money.tracker.backend.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
}