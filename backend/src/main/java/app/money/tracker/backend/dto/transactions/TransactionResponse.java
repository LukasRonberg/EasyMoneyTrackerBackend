package app.money.tracker.backend.dto.transactions;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class TransactionResponse {

    private UUID id;
    private UUID accountId;
    private UUID categoryId;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private String merchant;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}