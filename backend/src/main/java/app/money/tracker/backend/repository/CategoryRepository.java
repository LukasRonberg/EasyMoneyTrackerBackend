package app.money.tracker.backend.repository;

import app.money.tracker.backend.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByUserId(UUID userId);

    Optional<CategoryEntity> findByIdAndUserId(UUID id, UUID userId);
}