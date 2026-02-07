package app.money.tracker.backend.controller;

import app.money.tracker.backend.dto.category.CategoryResponse;
import app.money.tracker.backend.dto.category.CreateCategoryRequest;
import app.money.tracker.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        var category = categoryService.createCategory(request.getName());

        return ResponseEntity.ok(
                CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        List<CategoryResponse> categories = categoryService.listCategories()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(categories);
    }
}
