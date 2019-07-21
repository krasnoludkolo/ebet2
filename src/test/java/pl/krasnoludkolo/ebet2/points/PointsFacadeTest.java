package pl.krasnoludkolo.ebet2.points;

import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetType;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.dto.NewMatchDTO;
import pl.krasnoludkolo.ebet2.points.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.points.api.PointsError;
import pl.krasnoludkolo.ebet2.points.api.UserResultDTO;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class PointsFacadeTest {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);

    private PointsFacade pointsFacade;
    private ResultFacade resultFacade;
    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private UserDetails auth;
    private UserDetails auth2;
    private UserDetails auth3;
    private InMemorySystem system;

    @Before
    public void setUp() {
        system = new InMemorySystem();
        pointsFacade = system.pointsFacade();
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
        Option<LeagueResultsDTO> dto = pointsFacade.getResultsForLeague(leagueUUID);
        assertFalse(dto.isEmpty());
    }

    @Test
    public void shouldAddPointForCorrectBet() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());

        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);

        //then
        UserResultDTO userResult = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();

        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldNotAddPointForIncorrectBetButCreateUserResult() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());

        resultFacade.registerMatchResult(matchUUID, MatchResult.HOST_WON);

        //then
        UserResultDTO dto = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        assertEquals(0, dto.getPointCounter());
    }

    @Test
    public void shouldAddPointForCorrectBetAndNotForIncorrect() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get().getUuid();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth.getToken());
        resultFacade.registerMatchResult(matchUUID, MatchResult.HOST_WON);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);


        //then
        Option<UserResultDTO> dto = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID());
        UserResultDTO userResult = dto.get();
        assertEquals(1, userResult.getPointCounter());
    }

    @Test
    public void shouldAddDwoPointsToOneUser() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get().getUuid();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth.getToken());
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);


        //then
        UserResultDTO userResultDTO = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        assertEquals(2, userResultDTO.getPointCounter());
    }

    @Test
    public void shouldBeOnlyOneUserResultToOneUser() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear)).get().getUuid();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth.getToken());
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);



        //then
        int numberOfUserResults = pointsFacade.getResultsForLeague(leagueUUID).get().getGeneralResult().size();
        assertEquals(1, numberOfUserResults);
    }

    @Test
    public void resultsFromLeagueShouldBeInDecreasingOrder() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.HOST_WON, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.GUEST_WON, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.HOST_WON, matchUUID3), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID3), auth3.getToken());
        //when
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID3, MatchResult.DRAW);
        //then
        List<UserResultDTO> userResultDTOS = pointsFacade.getResultsForLeague(leagueUUID).get().getGeneralResult();
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
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.HOST_WON, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.GUEST_WON, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.HOST_WON, matchUUID3), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID3), auth3.getToken());
        //when
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID3, MatchResult.DRAW);
        //then
        List<List<UserResultDTO>> roundResults = pointsFacade.getResultsForLeague(leagueUUID).get().getRoundResults();
        roundResults
                .forEach(list -> assertEquals(3, list.size()));
    }

    @Test
    public void userShouldHaveCorrectResultInEveryRound() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID1 = leagueFacade.addMatchToLeague(new NewMatchDTO("host1", "guest1", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host3", "guest3", 2, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID4 = leagueFacade.addMatchToLeague(new NewMatchDTO("host4", "guest4", 3, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID5 = leagueFacade.addMatchToLeague(new NewMatchDTO("host5", "guest5", 3, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID1), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.GUEST_WON, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.GUEST_WON, matchUUID4), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetType.GUEST_WON, matchUUID5), auth.getToken());
        //when
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID1, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID3, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID4, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID5, MatchResult.DRAW);
        //then
        List<List<UserResultDTO>> roundResults = pointsFacade.getResultsForLeague(leagueUUID).get().getRoundResults();
        assertEquals(2, roundResults.get(0).get(0).getPointCounter());
        assertEquals(1, roundResults.get(1).get(0).getPointCounter());
        assertEquals(0, roundResults.get(2).get(0).getPointCounter());
    }

    @Ignore
    @Test
    public void shouldAddPointsOnceEvenIfResultForMatchIsUpdatedMoreThenOnce() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        //when
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        //then
        UserResultDTO userResultDTO = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        assertEquals(1, userResultDTO.getPointCounter());
    }

    @Test
    public void shouldNotRegisterNotSetResult() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetType.DRAW, matchUUID), auth.getToken());
        //then
        PointsError error = resultFacade.registerMatchResult(matchUUID, MatchResult.NOT_SET).getLeft();
        assertEquals(PointsError.SET_NOT_SET_RESULT, error);
    }

    @Test
    public void shouldNotUpdateNoExistingMatch() {
        PointsError error = resultFacade.registerMatchResult(UUID.randomUUID(), MatchResult.DRAW).getLeft();
        assertEquals(PointsError.MATCH_NOT_FOUND, error);
    }

    @Test
    public void shouldCreateResultsForEmptyLeague() {
        UUID uuid = leagueFacade.createLeague("new").get();

        Option<LeagueResultsDTO> results = pointsFacade.getResultsForLeague(uuid);

        assertTrue(results.isDefined());
    }

    @Test
    public void shouldRegisterResult() {
        //given
        UUID league = system.sampleLeagueUUID();
        UUID match = leagueFacade.addMatchToLeague(new NewMatchDTO("asd", "asd", 1, league, nextYear)).get().getUuid();

        //when
        resultFacade.registerMatchResult(match, MatchResult.DRAW);

        //then
        MatchResult result = leagueFacade.getMatchByUUID(match).get().getResult();
        assertEquals(MatchResult.DRAW, result);
    }
}