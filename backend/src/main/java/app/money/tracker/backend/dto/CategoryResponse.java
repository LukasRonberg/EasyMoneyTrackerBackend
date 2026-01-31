package app.money.tracker.backend.dto;

import app.money.tracker.backend.enums.CategoryType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CategoryResponse {

    private UUID id;
    private String name;
    private CategoryType type;
}