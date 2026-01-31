package app.money.tracker.backend.security;

import java.util.UUID;

public interface UserContext {
    UUID getCurrentUserId();
}