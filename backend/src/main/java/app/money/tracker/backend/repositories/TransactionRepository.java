package app.money.tracker.backend.repositories;

import app.money.tracker.backend.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findByUserIdAndTransactionDateBetween(UUID userId, LocalDate fromDate, LocalDate toDate);
}