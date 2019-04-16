package pl.krasnoludkolo.ebet2;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.bet.BetConfiguration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.external.ExternalConfiguration;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.external.clients.mockclient.ExternalClientMock;
import pl.krasnoludkolo.ebet2.infrastructure.TimeProvider;
import pl.krasnoludkolo.ebet2.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.results.ResultConfiguration;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.user.UserConfiguration;
import pl.krasnoludkolo.ebet2.user.UserFacade;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;

import java.time.LocalDateTime;

public class InMemorySystem {

    private UserFacade userFacade;
    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private ExternalFacade externalFacade;
    private ExternalClientMock externalClientMock;
    private TimeProvider timeProvider;
    private List<UserDetails> usersDetails;

    public InMemorySystem() {
        configureEnvironment();
        configureModules();
        addSampleUsers();
    }

    private void configureEnvironment() {
        timeProvider = TimeProvider.fromSystem();
    }


    private void configureModules() {
        userFacade = new UserConfiguration().inMemoryUserFacade();
        leagueFacade = new LeagueConfiguration().inMemoryLeagueFacade(timeProvider);
        externalClientMock = new ExternalClientMock(ExternalClientMock.SOME_MATCHES);
        betFacade = new BetConfiguration().inMemoryBetFacade(userFacade, leagueFacade);
        resultFacade = new ResultConfiguration().inMemoryResult(betFacade, leagueFacade);
        externalFacade = new ExternalConfiguration().inMemory(leagueFacade, resultFacade, externalClientMock);
    }

    private void addSampleUsers() {
        UserDetails api1 = userFacade.registerUser(new LoginUserInfo("user1", "pass1")).get();
        UserDetails api2 = userFacade.registerUser(new LoginUserInfo("user2", "pass2")).get();
        UserDetails api3 = userFacade.registerUser(new LoginUserInfo("user3", "pass3")).get();
        usersDetails = List.of(api1, api2, api3);
    }

    public UserFacade userFacade() {
        return userFacade;
    }

    public LeagueFacade leagueFacade() {
        return leagueFacade;
    }

    public BetFacade betFacade() {
        return betFacade;
    }

    public ResultFacade resultFacade() {
        return resultFacade;
    }

    public ExternalFacade externalFacade() {
        return externalFacade;
    }

    public void setExternalSourceMatchList(List<MatchInfo> list) {
        externalClientMock.setMatchList(list);
    }

    public List<MatchInfo> getExternalSourceMatchList() {
        return externalClientMock.getMatchList();
    }

    public List<UserDetails> getSampleUserDetailList() {
        return usersDetails;
    }

    public void setNow() {
        timeProvider.setNow();
    }

    public void setFixedTime(LocalDateTime time) {
        timeProvider.setFixed(time);
    }
}
