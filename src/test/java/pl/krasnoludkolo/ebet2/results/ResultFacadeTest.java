package pl.krasnoludkolo.ebet2.results;

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
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.List;
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
        String user = "user";
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID2));
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        UserResultDTO userResult = dto.get();
        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldAddDwoPointsToOneUser() {
        String user = "user";
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID2));
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);

        //then
        UserResultDTO userResultDTO = resultFacade.getResultsFromLeagueToUser(leagueUUID, user).get();
        assertEquals(2, userResultDTO.getPointCounter());
    }

    @Test
    public void shouldBeOnlyOneUserResultToOneUser() {
        String user = "user";
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID2));
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);

        //then
        int numberOfUserResults = resultFacade.getResultsForLeague(leagueUUID).get().getUserResultDTOS().size();
        assertEquals(1, numberOfUserResults);
    }

    @Test
    public void resultsFromLeagueShouldBeInDecreasingOrder() {
        //when
        String user1 = "user";
        String user2 = "user2";
        String user3 = "user3";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID));
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user1, matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, user1, matchUUID2));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, user1, matchUUID3));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user2, matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user2, matchUUID2));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, user2, matchUUID3));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user3, matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user3, matchUUID2));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user3, matchUUID3));
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        //then
        List<UserResultDTO> userResultDTOS = resultFacade.getResultsForLeague(leagueUUID).get().getUserResultDTOS();
        for (int i = 0; i < userResultDTOS.size() - 1; i++) {
            int current = userResultDTOS.get(i).getPointCounter();
            int next = userResultDTOS.get(i + 1).getPointCounter();
            assertTrue(current >= next);
        }
    }

}