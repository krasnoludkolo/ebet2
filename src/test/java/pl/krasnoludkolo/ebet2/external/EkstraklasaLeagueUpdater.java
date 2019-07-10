package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.dto.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.points.api.PointsError;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class EkstraklasaLeagueUpdater {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);

    private io.vavr.collection.List<MatchInfo> firstMatchList = io.vavr.collection.List.of(
            new MatchInfo("a", "b", 30, true, MatchResult.HOST_WON, nextYear),
            new MatchInfo("c", "d", 30, true, MatchResult.GUEST_WON, nextYear),
            new MatchInfo("a", "b", 31, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("c", "d", 31, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("a", "d", 32, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("c", "b", 32, false, MatchResult.NOT_SET, nextYear)

    );

    private io.vavr.collection.List<MatchInfo> updatedMatchList = io.vavr.collection.List.of(
            new MatchInfo("a", "b", 30, true, MatchResult.HOST_WON, nextYear),
            new MatchInfo("c", "d", 30, true, MatchResult.GUEST_WON, nextYear),
            new MatchInfo("a", "b", 31, true, MatchResult.DRAW, nextYear),
            new MatchInfo("c", "d", 31, true, MatchResult.DRAW, nextYear),
            new MatchInfo("a", "d", 32, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("c", "b", 32, false, MatchResult.NOT_SET, nextYear)

    );

    private ExternalFacade externalFacade;
    private LeagueFacade leagueFacade;
    private InMemorySystem system;
    private ResultFacade resultFacade;

    @Before
    public void setUp() {
        system = new InMemorySystem();
        leagueFacade = system.leagueFacade();
        externalFacade = system.externalFacade();
        resultFacade = system.resultFacade();
    }

    @Test
    public void shouldUpdateLeague() {
        //given
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty("Mock");
        system.setExternalSourceMatchList(firstMatchList);
        UUID leagueUUID = system.sampleLeagueUUID();
        externalFacade.initializeLeagueConfiguration(config, leagueUUID).get();
        resultFacade.manuallyUpdateLeague(leagueUUID);
        system.setExternalSourceMatchList(updatedMatchList);
        //when
        resultFacade.manuallyUpdateLeague(leagueUUID);
        //then
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(leagueUUID).get();
        int drawSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.DRAW ? 1 : 0).sum();
        assertEquals("Draw sum", 2, drawSum);
        int notSetSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.NOT_SET ? 1 : 0).sum();
        assertEquals("Not set sum", 2, notSetSum);
    }

    @Test
    public void shouldAddMatchesWhenAddedToLeagueInExternal() {
        //given
        system.setExternalSourceMatchList(firstMatchList);
        UUID leagueUUID = system.sampleLeagueUUID();
        UUID uuid = externalFacade.initializeLeagueConfiguration(ExternalSourceConfiguration.empty("Mock"), leagueUUID).get();
        resultFacade.manuallyUpdateLeague(leagueUUID);

        //when
        List<MatchInfo> matchInfosWithAdded = List.ofAll(firstMatchList);
        matchInfosWithAdded = matchInfosWithAdded.append(new MatchInfo("x", "x", 12, false, MatchResult.NOT_SET, nextYear));
        system.setExternalSourceMatchList(matchInfosWithAdded);
        resultFacade.manuallyUpdateLeague(leagueUUID);
        //then
        java.util.List<MatchDTO> matchDTOS = leagueFacade.getLeagueByUUID(uuid).get().getMatchDTOS();
        assertEquals("Number of matches after add one", 7, matchDTOS.size());
    }

    @Test
    public void shouldNotUpdateNonExistingLeague() {
        PointsError error = resultFacade.manuallyUpdateLeague(UUID.randomUUID()).getLeft();

        assertEquals(PointsError.LEAGUE_NOT_FOUND, error);
    }
}
