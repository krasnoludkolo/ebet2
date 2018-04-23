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
import pl.krasnoludkolo.ebet2.user.UserFacade;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MultiRoundsTest {

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
        UserFacade userFacade = system.userFacade();
        auth = userFacade.registerUser("user", "pas").get();
        auth2 = userFacade.registerUser("user2", "pas").get();
        auth3 = userFacade.registerUser("user3", "pas").get();

    }

    @Test
    public void fiveRoundThreeCorrect() {
        //when
        String user = "user";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID));
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID));
        UUID matchUUID4 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 4, leagueUUID));
        UUID matchUUID5 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 5, leagueUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID2), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID3), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID4), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user, matchUUID5), auth);
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID4, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID5, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        UserResultDTO userResult = dto.get();
        assertEquals(3, userResult.getPointCounter());
    }

    @Test
    public void ThreeRoundThreeUsers() {
        //when
        String user1 = "user";
        String user2 = "user2";
        String user3 = "user3";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 2, leagueUUID));
        UUID matchUUID3 = leagueFacade.addMatchToLeague(new NewMatchDTO("host2", "guest2", 3, leagueUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user1, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, user1, matchUUID2), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, user1, matchUUID3), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user2, matchUUID), auth2);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user2, matchUUID2), auth2);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, user2, matchUUID3), auth2);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user3, matchUUID), auth3);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user3, matchUUID2), auth3);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, user3, matchUUID3), auth3);
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);

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
