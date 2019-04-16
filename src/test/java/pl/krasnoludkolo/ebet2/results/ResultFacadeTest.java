package pl.krasnoludkolo.ebet2.results;

import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Ignore;
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
import pl.krasnoludkolo.ebet2.user.api.UserDetails;

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
    private UserDetails auth;
    private UserDetails auth2;
    private UserDetails auth3;

    @Before
    public void setUp() {
        InMemorySystem system = new InMemorySystem();
        resultFacade = system.resultFacade();
        leagueFacade = system.leagueFacade();
        betFacade = system.betFacade();
        auth = system.getSampleUserDetailList().get(0);
        auth2 = system.getSampleUserDetailList().get(1);
        auth3 = system.getSampleUserDetailList().get(2);
    }

    @Test
    public void shouldCreateResultsToLeague() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        //then
        Option<LeagueResultsDTO> dto = resultFacade.getResultsForLeague(leagueUUID);
        assertFalse(dto.isEmpty());
    }

    @Test
    public void shouldAddPointForCorrectBet() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);

        //then
        UserResultDTO userResult = resultFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();

        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldNotAddPointForIncorrectBetButCreateUserResult() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);

        resultFacade.updateLeagueResultsForMatch(matchUUID);

        //then
        UserResultDTO dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        assertEquals(0, dto.getPointCounter());
    }

    @Test
    public void shouldAddPointForCorrectBetAndNotForIncorrect() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth.getToken());
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID());
        UserResultDTO userResult = dto.get();
        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldAddDwoPointsToOneUser() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth.getToken());
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);

        //then
        UserResultDTO userResultDTO = resultFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        assertEquals(2, userResultDTO.getPointCounter());
    }

    @Test
    public void shouldBeOnlyOneUserResultToOneUser() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth.getToken());
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);


        //then
        int numberOfUserResults = resultFacade.getResultsForLeague(leagueUUID).get().getGeneralResult().size();
        assertEquals(1, numberOfUserResults);
    }

    @Test
    public void resultsFromLeagueShouldBeInDecreasingOrder() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID3), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID3), auth3.getToken());
        //when
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);
        resultFacade.updateLeagueResultsForMatch(matchUUID3);
        //then
        List<UserResultDTO> userResultDTOS = resultFacade.getResultsForLeague(leagueUUID).get().getGeneralResult();
        for (int i = 0; i < userResultDTOS.size() - 1; i++) {
            int current = userResultDTOS.get(i).getPointCounter();
            int next = userResultDTOS.get(i + 1).getPointCounter();
            assertTrue(current >= next);
        }
    }

    @Test
    public void everyRoundShouldHaveAllUsers() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID3), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID3), auth3.getToken());
        //when
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);
        resultFacade.updateLeagueResultsForMatch(matchUUID3);
        //then
        List<List<UserResultDTO>> roundResults = resultFacade.getResultsForLeague(leagueUUID).get().getRoundResults();
        roundResults
                .forEach(list -> assertEquals(3, list.size()));
    }

    @Test
    public void userShouldHaveCorrectResultInEveryRound() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID1 = leagueFacade.addMatchToLeague(new NewMatchDTO("host1", "guest1", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host3", "guest3", 2, leagueUUID, nextYear)).get();
        UUID matchUUID4 = leagueFacade.addMatchToLeague(new NewMatchDTO("host4", "guest4", 3, leagueUUID, nextYear)).get();
        UUID matchUUID5 = leagueFacade.addMatchToLeague(new NewMatchDTO("host5", "guest5", 3, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID1), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID4), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID5), auth.getToken());
        //when
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID1, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID4, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID5, MatchResult.DRAW);
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID1);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);
        resultFacade.updateLeagueResultsForMatch(matchUUID3);
        resultFacade.updateLeagueResultsForMatch(matchUUID4);
        resultFacade.updateLeagueResultsForMatch(matchUUID5);
        //then
        List<List<UserResultDTO>> roundResults = resultFacade.getResultsForLeague(leagueUUID).get().getRoundResults();
        assertEquals(2, roundResults.get(0).get(0).getPointCounter());
        assertEquals(1, roundResults.get(1).get(0).getPointCounter());
        assertEquals(0, roundResults.get(2).get(0).getPointCounter());
    }

    @Ignore
    @Test
    public void shouldAddPointsOnceEvenIfResultForMatchIsUpdatedMoreThenOnce() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        //when
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        //then
        UserResultDTO userResultDTO = resultFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        assertEquals(1, userResultDTO.getPointCounter());
    }

    @Test
    public void shouldNotUpdateResultsIfResultIsNotSetAndUpdateAfterResultIsSet() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        //when
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.updateLeagueResultsForMatch(matchUUID);
        //then
        UserResultDTO userResultDTO = resultFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        assertEquals(1, userResultDTO.getPointCounter());
    }

    @Test(expected = MatchNotFound.class)
    public void shouldNotUpdateNoExistingMatch() {
        resultFacade.updateLeagueResultsForMatch(UUID.randomUUID());
    }

    @Test
    public void shouldCreateResultsForEmptyLeague() {
        UUID uuid = leagueFacade.createLeague("new").get();

        Option<LeagueResultsDTO> results = resultFacade.getResultsForLeague(uuid);

        assertTrue(results.isDefined());
    }
}