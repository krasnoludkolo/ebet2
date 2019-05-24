package pl.krasnoludkolo.ebet2;

import io.haste.BlockingScheduledExecutionService;
import io.haste.Haste;
import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.bet.BetConfiguration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.external.ExternalConfiguration;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.external.clients.mockclient.ExternalClientMock;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.points.PointsConfiguration;
import pl.krasnoludkolo.ebet2.points.PointsFacade;
import pl.krasnoludkolo.ebet2.results.ResultConfiguration;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.user.UserConfiguration;
import pl.krasnoludkolo.ebet2.user.UserFacade;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InMemorySystem {

    private UserFacade userFacade;
    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private ExternalFacade externalFacade;
    private PointsFacade pointsFacade;

    private ExternalClientMock externalClientMock;
    private BlockingScheduledExecutionService timeSource;
    private List<UserDetails> usersDetails;
    private UUID leagueUUID;

    public InMemorySystem() {
        configureEnvironment();
        configureModules();
        addSampleLeague();
        addSampleUsers();
    }

    private void configureEnvironment() {
        timeSource = Haste.ScheduledExecutionService.withFixedClockFromNow();
        externalClientMock = new ExternalClientMock(ExternalClientMock.SOME_MATCHES);
    }


    private void configureModules() {
        userFacade = new UserConfiguration().inMemoryUserFacade();
        externalFacade = new ExternalConfiguration().inMemory(externalClientMock);
        leagueFacade = new LeagueConfiguration().inMemoryLeagueFacade(timeSource);
        betFacade = new BetConfiguration().inMemoryBetFacade(userFacade, leagueFacade);
        pointsFacade = new PointsConfiguration().inMemoryPointsFacade(betFacade, leagueFacade);
        resultFacade = new ResultConfiguration().inMemoryResult(leagueFacade, externalFacade, pointsFacade, timeSource);
    }

    private void addSampleLeague() {
        leagueUUID = leagueFacade.createLeague("test").get();
        externalFacade.initializeLeagueConfiguration(ExternalSourceConfiguration.empty(), "Mock", leagueUUID);
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

    public PointsFacade pointsFacade() {
        return pointsFacade;
    }

    public void recreateResultsModule(InMemoryRepository repository) {
        externalClientMock = new ExternalClientMock(getExternalSourceMatchList());
        resultFacade = new ResultConfiguration().inMemoryResultWithData(leagueFacade, externalFacade, pointsFacade, timeSource, repository);
    }

    public UUID sampleLeagueUUID() {
        return leagueUUID;
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

    public void advanceTimeBy(long daleyTime, TimeUnit timeUnit) {
        timeSource.advanceTimeBy(daleyTime, timeUnit);
    }

    public LocalDateTime now() {
        return timeSource.now();
    }
}
