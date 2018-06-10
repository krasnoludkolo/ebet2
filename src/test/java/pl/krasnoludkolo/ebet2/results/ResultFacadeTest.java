package pl.krasnoludkolo.ebet2.results;

import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchNotFound;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ResultFacadeTest {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);

    private ResultFacade resultFacade;
    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private String auth;
    private String auth2;
    private String auth3;

    @Before
    public void setUp() {
        InMemorySystem system = new InMemorySystem();
        resultFacade = system.resultFacade();
        leagueFacade = system.leagueFacade();
        betFacade = system.betFacade();
        auth = system.getSampleUsersApiToken().get(0);
        auth2 = system.getSampleUsersApiToken().get(1);
        auth3 = system.getSampleUsersApiToken().get(2);
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
        String user = "user1";
        //when
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);

        //then
        UserResultDTO userResult = resultFacade.getResultsFromLeagueToUser(leagueUUID, user).get();

        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldNotAddPointForIncorrectBet() {
        //when
        String user = "user1";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);

        resultFacade.updateLeagueResultsForMatch(matchUUID);

        //then
        UserResultDTO dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user).get();
        assertEquals(0, dto.getPointCounter());
    }

    @Test
    public void shouldAddPointForCorrectBetAndNotForIncorrect() {
        String user = "user1";
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        UserResultDTO userResult = dto.get();
        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldAddDwoPointsToOneUser() {
        String user = "user1";
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);

        //then
        UserResultDTO userResultDTO = resultFacade.getResultsFromLeagueToUser(leagueUUID, user).get();
        assertEquals(2, userResultDTO.getPointCounter());
    }

    @Test
    public void shouldBeOnlyOneUserResultToOneUser() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);


        //then
        int numberOfUserResults = resultFacade.getResultsForLeague(leagueUUID).get().getUserResultDTOS().size();
        assertEquals(1, numberOfUserResults);
    }

    @Test
    public void resultsFromLeagueShouldBeInDecreasingOrder() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID2), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID3), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth2);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth2);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID3), auth2);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth3);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth3);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID3), auth3);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);
        resultFacade.updateLeagueResultsForMatch(matchUUID3);
        //then
        List<UserResultDTO> userResultDTOS = resultFacade.getResultsForLeague(leagueUUID).get().getUserResultDTOS();
        for (int i = 0; i < userResultDTOS.size() - 1; i++) {
            int current = userResultDTOS.get(i).getPointCounter();
            int next = userResultDTOS.get(i + 1).getPointCounter();
            assertTrue(current >= next);
        }
    }

    @Test(expected = MatchNotFound.class)
    public void shouldNotUpdateNoExistingMatch() {
        resultFacade.updateLeagueResultsForMatch(UUID.randomUUID());
    }

    @Test
    public void shouldCreateEmptyResultIfUserDidNotGetAnyPoint() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        //then
        UserResultDTO user = resultFacade.getResultsFromLeagueToUser(leagueUUID, "user1").get();
        assertEquals(0, user.getPointCounter());
    }

}