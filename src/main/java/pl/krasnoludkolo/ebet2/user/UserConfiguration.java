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
        UserAuthentication userAuthentication = new UserAuthentication(repository, passwordEncrypt);
        UserRegistration userRegistration = new UserRegistration(repository, passwordEncrypt);
        UserAuthorization userAuthorization = new UserAuthorization(repository);
        return new UserFacade(userAuthentication, userAuthorization, tokenManager, userRegistration);
    }


    public UserFacade inMemoryUserFacade() {
        Repository<UserEntity> repository = new InMemoryRepository<>();
        JWTTokenManager tokenManager = new JWTTokenManager();
        PasswordEncrypt passwordEncrypt = new PlainTextPasswordEncrypt();
        UserAuthentication userAuthentication = new UserAuthentication(repository, passwordEncrypt);
        UserRegistration userRegistration = new UserRegistration(repository, passwordEncrypt);
        UserAuthorization userAuthorization = new UserAuthorization(repository);
        return new UserFacade(userAuthentication, userAuthorization, tokenManager, userRegistration);
    }


}
