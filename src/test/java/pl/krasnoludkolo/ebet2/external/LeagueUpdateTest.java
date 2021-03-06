package pl.krasnoludkolo.ebet2.external;

import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.dto.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class LeagueUpdateTest {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);

    private io.vavr.collection.List<MatchInfo> firstMatchList = io.vavr.collection.List.of(
            new MatchInfo("a", "b", 1, true, MatchResult.HOST_WON, nextYear),
            new MatchInfo("c", "d", 1, true, MatchResult.GUEST_WON, nextYear),
            new MatchInfo("a", "c", 2, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("b", "d", 2, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("a", "d", 3, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("c", "b", 3, false, MatchResult.NOT_SET, nextYear)
    );

    private io.vavr.collection.List<MatchInfo> updated = io.vavr.collection.List.of(
            new MatchInfo("a", "b", 1, true, MatchResult.HOST_WON, nextYear),
            new MatchInfo("c", "d", 1, true, MatchResult.GUEST_WON, nextYear),
            new MatchInfo("a", "c", 2, true, MatchResult.DRAW, nextYear),
            new MatchInfo("b", "d", 2, true, MatchResult.DRAW, nextYear),
            new MatchInfo("a", "d", 3, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("c", "b", 3, false, MatchResult.NOT_SET, nextYear)
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
        UUID leagueUUID = leagueFacade.createLeague("test2").get();
        UUID uuid = externalFacade.initializeLeagueConfiguration(config, leagueUUID).get();
        resultFacade.manuallyUpdateLeague(leagueUUID);

        //when
        system.setExternalSourceMatchList(updated);
        resultFacade.manuallyUpdateLeague(leagueUUID);
        //then
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(uuid).get();
        int drawSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.DRAW ? 1 : 0).sum();
        assertEquals(2, drawSum);
        int notSetSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.NOT_SET ? 1 : 0).sum();
        assertEquals(2, notSetSum);
    }

}
