package app.money.tracker.backend.config;

import app.money.tracker.backend.entity.AccountEntity;
import app.money.tracker.backend.entity.UserEntity;
import app.money.tracker.backend.repository.AccountRepository;
import app.money.tracker.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, AccountRepository accountRepository) {
        return arguments -> {

            if (userRepository.count() > 0) {
                return;
            }

            UserEntity user = UserEntity.builder()
                    .id(UUID.randomUUID())
                    .email("test@local.dev")
                    .createdAt(OffsetDateTime.now())
                    .build();

            userRepository.save(user);

            AccountEntity account = AccountEntity.builder()
                    .id(UUID.randomUUID())
                    .user(user)
                    .name("Main account")
                    .currency("DKK")
                    .createdAt(OffsetDateTime.now())
                    .build();

            accountRepository.save(account);

            System.out.println("Seeded userId=" + user.getId());
            System.out.println("Seeded accountId=" + account.getId());
        };
    }
}