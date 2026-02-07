package app.money.tracker.backend.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CsrfTokenResponse {

    private String token;
    private String headerName;
    private String parameterName;
}
