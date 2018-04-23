package pl.krasnoludkolo.ebet2;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.bet.BetConfiguration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.external.ExternalConfiguration;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.external.externalClients.mockclient.ExternalClientMock;
import pl.krasnoludkolo.ebet2.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.results.ResultConfiguration;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.user.UserConfiguration;
import pl.krasnoludkolo.ebet2.user.UserFacade;

public class InMemorySystem {

    private UserFacade userFacade;
    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private ExternalFacade externalFacade;
    private ExternalClientMock externalClientMock;
    private List<String> sampleUsersApiToken;

    public InMemorySystem() {
        configureModules();
        addSampleUsers();
    }

    private void configureModules() {
        userFacade = new UserConfiguration().inMemoryUserFacade();
        externalClientMock = new ExternalClientMock(ExternalClientMock.SOME_MATCHES);
        betFacade = new BetConfiguration().inMemoryBetFacade(userFacade);
        resultFacade = new ResultConfiguration().inMemoryResult(betFacade);
        leagueFacade = new LeagueConfiguration().inMemoryLeagueFacade(resultFacade);
        externalFacade = new ExternalConfiguration().inMemory(leagueFacade, externalClientMock);
    }

    private void addSampleUsers() {
        String api1 = userFacade.registerUser("user1", "pass1").get();
        String api2 = userFacade.registerUser("user2", "pass2").get();
        String api3 = userFacade.registerUser("user3", "pass3").get();
        sampleUsersApiToken = List.of(api1, api2, api3);
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

    public List<String> getSampleUsersApiToken() {
        return sampleUsersApiToken;
    }
}
