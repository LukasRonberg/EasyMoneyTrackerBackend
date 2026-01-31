package app.money.tracker.backend.dto;

import app.money.tracker.backend.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private CategoryType type;
}