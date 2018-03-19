package pl.krasnoludkolo.ebet2.results.domain;

import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

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
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID));
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        UserResultDTO userResult = dto.get();
        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldNotAddPointForIncorrectBet() {
        //when
        String user = "user";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID));
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
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID2));
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        UserResultDTO userResult = dto.get();
        assertEquals(1, userResult.getPointCounter());
    }


}