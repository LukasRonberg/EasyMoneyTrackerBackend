package app.money.tracker.backend.service;

import app.money.tracker.backend.entity.CategoryEntity;
import app.money.tracker.backend.entity.UserEntity;
import app.money.tracker.backend.repository.CategoryRepository;
import app.money.tracker.backend.repository.UserRepository;
import app.money.tracker.backend.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserContext userContext;

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryEntity createCategory(String name) {

        UserEntity user = userRepository.findById(userContext.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CategoryEntity category = CategoryEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name(name)
                .createdAt(OffsetDateTime.now())
                .build();

        return categoryRepository.save(category);
    }

    public List<CategoryEntity> listCategories() {
        return categoryRepository.findByUserId(userContext.getCurrentUserId());
    }
}
