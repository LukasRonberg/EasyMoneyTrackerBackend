package app.money.tracker.backend.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CategoryTotalResponse {

    private final UUID categoryId;
    private final String categoryName;
    private final BigDecimal total;

    public CategoryTotalResponse(UUID categoryId, String categoryName, BigDecimal total) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.total = total;
    }
}
