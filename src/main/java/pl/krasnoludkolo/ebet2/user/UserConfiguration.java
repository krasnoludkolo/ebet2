package pl.krasnoludkolo.ebet2.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

@Configuration
public class UserConfiguration {

    @Bean
    public UserFacade inMemoryUserFacade() {
        Repository<UserEntity> repository = new InMemoryRepository<>();
        JWTTokenManager tokenManager = new JWTTokenManager();
        UserManager userManager = new UserManager(repository, tokenManager);
        return new UserFacade(userManager, tokenManager);
    }


}
