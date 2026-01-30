package app.money.tracker.backend.repositories;

import app.money.tracker.backend.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findByUserId(UUID userId);
}