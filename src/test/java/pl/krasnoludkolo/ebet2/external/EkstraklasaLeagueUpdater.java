package pl.krasnoludkolo.ebet2.external;

import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

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
        UUID uuid = externalFacade.initializeLeague(config, "Mock", "ekstraklasa");
        system.setExternalSourceMatchList(updatedMatchList);
        //when
        externalFacade.updateLeague(uuid);
        //then
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(uuid).get();
        int drawSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.DRAW ? 1 : 0).sum();
        assertEquals("Draw sum", 2, drawSum);
        int notSetSum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.NOT_SET ? 1 : 0).sum();
        assertEquals("Not set sum", 2, notSetSum);
    }

}
