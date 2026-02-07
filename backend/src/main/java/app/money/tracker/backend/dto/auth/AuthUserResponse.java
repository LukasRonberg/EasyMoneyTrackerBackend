package app.money.tracker.backend.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AuthUserResponse {

    private UUID id;
    private String email;
}
