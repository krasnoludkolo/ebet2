package pl.krasnoludkolo.ebet2.update;

import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class AutoUpdateTest {


    private LeagueFacade leagueFacade;
    private InMemorySystem system;

    @Before
    public void setUp() {
        system = new InMemorySystem();
        leagueFacade = system.leagueFacade();
    }

    @Test
    public void shouldFetchMatchResultAfterMatchIsFinished() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("test").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(createNewMatchDTO(leagueUUID)).get();

        //when
        system.setExternalSourceMatchList(List.of(createMatchInfo()));
        system.advanceTimeBy(2, TimeUnit.HOURS);

        //then
        MatchResult result = leagueFacade.getMatchByUUID(matchUUID).get().getResult();
        assertEquals(MatchResult.DRAW, result);
    }

    @Test
    public void shouldRetryFetchMatchResultAfterMatchIsFinished() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("test").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(createNewMatchDTO(leagueUUID)).get();

        //when
        system.advanceTimeBy(2, TimeUnit.HOURS);
        system.setExternalSourceMatchList(List.of(createMatchInfo()));
        system.advanceTimeBy(10, TimeUnit.MINUTES);

        //then
        MatchResult result = leagueFacade.getMatchByUUID(matchUUID).get().getResult();
        assertEquals(MatchResult.DRAW, result);
    }

    private NewMatchDTO createNewMatchDTO(UUID leagueUUID) {
        return new NewMatchDTO("host", "guest", 1, leagueUUID, system.now());
    }

    private MatchInfo createMatchInfo() {
        return new MatchInfo("host", "guest", 1, true, MatchResult.DRAW, system.now());
    }

}
