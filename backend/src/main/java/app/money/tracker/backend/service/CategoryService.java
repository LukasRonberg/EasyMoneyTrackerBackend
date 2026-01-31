package app.money.tracker.backend.service;

import app.money.tracker.backend.entity.CategoryEntity;
import app.money.tracker.backend.entity.UserEntity;
import app.money.tracker.backend.enums.CategoryType;
import app.money.tracker.backend.repository.CategoryRepository;
import app.money.tracker.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final UUID TEST_USER_ID =
            UUID.fromString("0d55a075-ac15-46c1-84f9-fa175c0de90d");

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryEntity createCategory(String name, CategoryType type) {

        UserEntity user = userRepository.findById(TEST_USER_ID)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CategoryEntity category = CategoryEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name(name)
                .type(type.name())
                .createdAt(OffsetDateTime.now())
                .build();

        return categoryRepository.save(category);
    }

    public List<CategoryEntity> listCategories() {
        return categoryRepository.findByUserId(TEST_USER_ID);
    }
}