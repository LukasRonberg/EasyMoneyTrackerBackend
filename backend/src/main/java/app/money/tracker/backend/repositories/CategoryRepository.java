package app.money.tracker.backend.repositories;

import app.money.tracker.backend.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByUserId(UUID userId);
}