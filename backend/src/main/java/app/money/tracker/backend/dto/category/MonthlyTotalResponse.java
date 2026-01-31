package app.money.tracker.backend.dto.category;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class MonthlyTotalResponse {

    private final LocalDate month;
    private final BigDecimal total;

    public MonthlyTotalResponse(LocalDate month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }
}