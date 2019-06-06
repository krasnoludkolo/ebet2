package pl.krasnoludkolo.ebet2.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;

import javax.persistence.EntityManagerFactory;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Configuration
public class UserConfiguration {

    @Bean
    public UserFacade userFacade(CrudRepository<UserEntity, UUID> repo, EntityManagerFactory entityManager) {
        UnaryOperator<UserEntity> d2e = e -> e;
        UnaryOperator<UserEntity> e2d = e -> e;
        Repository<UserEntity> repository = new SpringDataRepositoryAdapter<>(repo, d2e, e2d, entityManager);
        JWTTokenManager tokenManager = new JWTTokenManager();
        PasswordEncrypt passwordEncrypt = new BCryptPasswordEncrypt();
        UserManager userManager = new UserManager(repository, tokenManager, passwordEncrypt);
        return new UserFacade(userManager);
    }


    public UserFacade inMemoryUserFacade() {
        Repository<UserEntity> repository = new InMemoryRepository<>();
        JWTTokenManager tokenManager = new JWTTokenManager();
        PasswordEncrypt passwordEncrypt = new PlainTextPasswordEncrypt();
        UserManager userManager = new UserManager(repository, tokenManager, passwordEncrypt);
        return new UserFacade(userManager);
    }


}
