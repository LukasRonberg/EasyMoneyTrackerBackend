package app.money.tracker.backend.dto.transactions;

import app.money.tracker.backend.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateTransactionRequest {

    @NotNull
    private UUID accountId;

    @NotNull
    private UUID categoryId;

    @NotNull
    private TransactionType transactionType;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDate transactionDate;

    private String description;

    private String merchant;
}
