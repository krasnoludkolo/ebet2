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
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MultiRoundsTest {

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
    public void fiveRoundThreeCorrect() {
        //when
        String user = "user1";
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear)).get();
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID, nextYear)).get();
        UUID matchUUID4 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 4, leagueUUID, nextYear)).get();
        UUID matchUUID5 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 5, leagueUUID, nextYear)).get();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID2), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID3), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID4), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID5), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID4, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID5, MatchResult.DRAW);

        resultFacade.updateLeagueResultsForMatch(matchUUID);
        resultFacade.updateLeagueResultsForMatch(matchUUID2);
        resultFacade.updateLeagueResultsForMatch(matchUUID3);
        resultFacade.updateLeagueResultsForMatch(matchUUID4);
        resultFacade.updateLeagueResultsForMatch(matchUUID5);
        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        UserResultDTO userResult = dto.get();
        assertEquals(3, userResult.getPointCounter());
    }

    @Test
    public void ThreeRoundThreeUsers() {
        //when
        String user1 = "user1";
        String user2 = "user2";
        String user3 = "user3";
        UUID leagueUUID = leagueFacade.createLeague("new").get();
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
        Option<UserResultDTO> dto1 = resultFacade.getResultsFromLeagueToUser(leagueUUID, user1);
        Option<UserResultDTO> dto2 = resultFacade.getResultsFromLeagueToUser(leagueUUID, user2);
        Option<UserResultDTO> dto3 = resultFacade.getResultsFromLeagueToUser(leagueUUID, user3);
        UserResultDTO userResult1 = dto1.get();
        UserResultDTO userResult2 = dto2.get();
        UserResultDTO userResult3 = dto3.get();
        assertEquals(1, userResult1.getPointCounter());
        assertEquals(2, userResult2.getPointCounter());
        assertEquals(3, userResult3.getPointCounter());
    }


}
