package pl.krasnoludkolo.ebet2.external;

import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class LeagueUpdateTest {

    private io.vavr.collection.List<MatchInfo> firstMatchList = io.vavr.collection.List.of(
            new MatchInfo("a", "b", 1, true, MatchResult.HOST_WON),
            new MatchInfo("c", "d", 1, true, MatchResult.GUEST_WON),
            new MatchInfo("a", "c", 2, false, MatchResult.NOT_SET),
            new MatchInfo("b", "d", 2, false, MatchResult.NOT_SET),
            new MatchInfo("a", "d", 3, false, MatchResult.NOT_SET),
            new MatchInfo("c", "b", 3, false, MatchResult.NOT_SET)
    );

    private io.vavr.collection.List<MatchInfo> updated = io.vavr.collection.List.of(
            new MatchInfo("a", "b", 1, true, MatchResult.HOST_WON),
            new MatchInfo("c", "d", 1, true, MatchResult.GUEST_WON),
            new MatchInfo("a", "c", 2, true, MatchResult.DRAW),
            new MatchInfo("b", "d", 2, true, MatchResult.DRAW),
            new MatchInfo("a", "d", 3, false, MatchResult.NOT_SET),
            new MatchInfo("c", "b", 3, false, MatchResult.NOT_SET)
    );

    private ExternalFacade externalFacade;
    private LeagueFacade leagueFacade;
    private InMemorySystem system;

    @Before
    public void setUp() {
        system = new InMemorySystem();
        leagueFacade = system.leagueFacade();
        externalFacade = system.externalFacade();

    }


    @Test
    public void shouldUpdateLeague() {
        //given
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        system.setExternalSourceMatchList(firstMatchList);
        UUID uuid = externalFacade.initializeLeague(config, "Mock", "testName");
        system.setExternalSourceMatchList(updated);
        //when
        externalFacade.updateLeague(uuid);
        //then
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(uuid).get();
        int drawSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.DRAW ? 1 : 0).sum();
        assertEquals(2, drawSum);
        int notSetSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.NOT_SET ? 1 : 0).sum();
        assertEquals(2, notSetSum);
    }

}
