package app.money.tracker.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DevUserContext implements UserContext {

    private final UUID currentUserId;

    public DevUserContext(@Value("${app.testUserId}") UUID currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Override
    public UUID getCurrentUserId() {
        return currentUserId;
    }
}