package app.money.tracker.backend.dto.transactions;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransactionTotalResponse {

    private final BigDecimal total;

    public TransactionTotalResponse(BigDecimal total) {
        this.total = total;
    }
}