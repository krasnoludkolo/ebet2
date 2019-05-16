package pl.krasnoludkolo.ebet2;

import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.points.PointsFacade;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MultiRoundsTest {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);


    private ResultFacade resultFacade;
    private PointsFacade pointsFacade;
    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private UserDetails auth;
    private UserDetails auth2;
    private UserDetails auth3;


    @Before
    public void setUp() {
        InMemorySystem system = new InMemorySystem();
        resultFacade = system.resultFacade();
        pointsFacade = system.pointsFacade();
        leagueFacade = system.leagueFacade();
        betFacade = system.betFacade();
        auth = system.getSampleUserDetailList().get(0);
        auth2 = system.getSampleUserDetailList().get(1);
        auth3 = system.getSampleUserDetailList().get(2);
    }

    @Test
    public void fiveRoundThreeCorrect() {
        //when
        String user = "user1";
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID4 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 4, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID5 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 5, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID4), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID5), auth.getToken());
        resultFacade.registerMatchResult(matchUUID, MatchResult.HOST_WON);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID3, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID4, MatchResult.HOST_WON);
        resultFacade.registerMatchResult(matchUUID5, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID());
        UserResultDTO userResult = dto.get();
        assertEquals(3, userResult.getPointCounter());
    }

    @Test
    public void ThreeRoundThreeUsers() {
        //when
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get().getUuid();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get().getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID2), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID3), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID3), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth3.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID3), auth3.getToken());
        resultFacade.registerMatchResult(matchUUID, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID2, MatchResult.DRAW);
        resultFacade.registerMatchResult(matchUUID3, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto1 = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID());
        Option<UserResultDTO> dto2 = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth2.getUserUUID());
        Option<UserResultDTO> dto3 = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth3.getUserUUID());
        UserResultDTO userResult1 = dto1.get();
        UserResultDTO userResult2 = dto2.get();
        UserResultDTO userResult3 = dto3.get();
        assertEquals(1, userResult1.getPointCounter());
        assertEquals(2, userResult2.getPointCounter());
        assertEquals(3, userResult3.getPointCounter());
    }


}
