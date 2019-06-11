package pl.krasnoludkolo.ebet2.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.function.UnaryOperator;

@Configuration
public class UserConfiguration {

    @Bean
    public UserFacade userFacade() {
        UnaryOperator<UserEntity> d2e = e -> e;
        UnaryOperator<UserEntity> e2d = e -> e;
        Repository<UserEntity> repository = new UserEntityJOOQRepository(d2e, e2d);
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
