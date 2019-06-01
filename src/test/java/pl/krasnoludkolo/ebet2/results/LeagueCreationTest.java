package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.api.ResultError;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class LeagueCreationTest {

    private static final MatchResult MATCH_RESULT = MatchResult.DRAW;

    private LeagueFacade leagueFacade;
    private InMemorySystem system;
    private ResultFacade resultFacade;
    private LocalDateTime matchStartDate;

    @Before
    public void setUp() {
        system = new InMemorySystem();
        leagueFacade = system.leagueFacade();
        this.resultFacade = system.resultFacade();
        matchStartDate = system.now();
    }

    @Test
    public void shouldCreateLeagueWithExternalConfigurationAndFetchInitialResult() {
        //given
        system.setExternalSourceMatchList(List.of(finished()));
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty("Mock");

        //when
        UUID leagueUUID = resultFacade.registerLeague("leagueName", config).get();

        //then
        List<MatchDTO> matches = leagueFacade.getAllMatchesFromLeague(leagueUUID);
        assertEquals(1, matches.size());
    }

    @Test
    public void shouldFetchPlannedResultFromRegisteredLeague() {
        //given
        system.setExternalSourceMatchList(List.of(planned()));
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty("Mock");
        UUID leagueUUID = resultFacade.registerLeague("leagueName", config).get();

        //when
        system.setExternalSourceMatchList(List.of(finished()));
        system.advanceTimeBy(3, TimeUnit.HOURS);

        //then
        List<MatchDTO> matches = leagueFacade.getAllMatchesFromLeague(leagueUUID);
        assertEquals(1, matches.size());
        assertEquals(MATCH_RESULT, matches.get(0).getResult());
    }

    @Test
    public void shouldFetchMatchesWithResults() {
        //given
        system.setExternalSourceMatchList(List.of(finished()));
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty("Mock");

        //when
        UUID leagueUUID = resultFacade.registerLeague("leagueName", config).get();

        //then
        List<MatchDTO> matches = leagueFacade.getAllMatchesFromLeague(leagueUUID);
        assertEquals(1, matches.size());
        assertEquals(MATCH_RESULT, matches.get(0).getResult());
    }

    @Test
    public void shouldNotRegisterLeagueIchNameIsDuplicated() {
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty("Mock");
        ResultError error = resultFacade.registerLeague("test", config).getLeft();

        assertEquals(ResultError.LEAGUE_NAME_DUPLICATION, error);
    }

    private MatchInfo planned() {
        matchStartDate = system.now();
        return new MatchInfo("host", "guest", 1, false, MatchResult.NOT_SET, matchStartDate);
    }

    private MatchInfo finished() {
        return new MatchInfo("host", "guest", 1, true, MATCH_RESULT, matchStartDate);
    }

}
