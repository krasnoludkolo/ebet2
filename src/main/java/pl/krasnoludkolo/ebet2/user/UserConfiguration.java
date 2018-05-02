package pl.krasnoludkolo.ebet2.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;

import java.util.UUID;
import java.util.function.Function;

@Configuration
public class UserConfiguration {

    @Bean
    public UserFacade userFacade(CrudRepository<UserEntity, UUID> repo) {
        Function<UserEntity, UserEntity> d2e = e -> e;
        Function<UserEntity, UserEntity> e2d = e -> e;
        Repository<UserEntity> repository = new SpringDataRepositoryAdapter<>(repo, d2e, e2d);
        JWTTokenManager tokenManager = new JWTTokenManager();
        UserManager userManager = new UserManager(repository, tokenManager);
        return new UserFacade(userManager, tokenManager);
    }


    public UserFacade inMemoryUserFacade() {
        Repository<UserEntity> repository = new InMemoryRepository<>();
        JWTTokenManager tokenManager = new JWTTokenManager();
        UserManager userManager = new UserManager(repository, tokenManager);
        return new UserFacade(userManager, tokenManager);
    }


}
