package pl.krasnoludkolo.ebet2.domain.results;

import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.domain.bet.BetFacade;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.domain.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.domain.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.domain.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.domain.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.domain.results.api.UserResultDTO;

import java.util.UUID;

import static org.junit.Assert.*;

public class ResultFacadeTest {

    private ResultFacade resultFacade;
    private LeagueFacade leagueFacade;
    private BetFacade betFacade;

    @Before
    public void setUp() {
        InMemorySystem system = new InMemorySystem();
        resultFacade = system.resultFacade();
        leagueFacade = system.leagueFacade();
        betFacade = system.betFacade();
    }

    @Test
    public void shouldCreateResultsToLeague() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new");
        //then
        Option<LeagueResultsDTO> dto = resultFacade.getResultsForLeague(leagueUUID);
        assertFalse(dto.isEmpty());
    }

    @Test
    public void shouldAddPointForCorrectBet() {
        //given
        String user = "user";
        //when
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, user));
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        if (dto.isEmpty()) {
            fail();
        } else {
            UserResultDTO userResult = dto.get();
            assertEquals(1, userResult.getPointCounter());
        }
    }

    @Test
    public void shouldNotAddPointForIncorrectBet() {
        //when
        String user = "user";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, user));
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        assertTrue(dto.isEmpty());
    }

    @Test
    public void shouldAddPointForCorrectBetAndNotForIncorrect() {
        //when
        String user = "user";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host2", "guest2", 1, leagueUUID));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, user));
        betFacade.addBetToMatch(matchUUID2, new NewBetDTO(BetTyp.DRAW, user));
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        if (dto.isEmpty()) {
            fail();
        } else {
            UserResultDTO userResult = dto.get();
            assertEquals(1, userResult.getPointCounter());
        }
    }


}