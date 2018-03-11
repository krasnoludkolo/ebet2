package pl.krasnoludkolo.ebet2.domain;

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
import pl.krasnoludkolo.ebet2.domain.results.ResultFacade;
import pl.krasnoludkolo.ebet2.domain.results.api.UserResultDTO;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MultiRoundsTest {

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
    public void fiveRoundThreeCorrect() {
        //when
        String user = "user";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host2", "guest2", 2, leagueUUID));
        UUID matchUUID3 = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host2", "guest2", 3, leagueUUID));
        UUID matchUUID4 = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host2", "guest2", 4, leagueUUID));
        UUID matchUUID5 = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host2", "guest2", 5, leagueUUID));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, user));
        betFacade.addBetToMatch(matchUUID2, new NewBetDTO(BetTyp.DRAW, user));
        betFacade.addBetToMatch(matchUUID3, new NewBetDTO(BetTyp.DRAW, user));
        betFacade.addBetToMatch(matchUUID4, new NewBetDTO(BetTyp.DRAW, user));
        betFacade.addBetToMatch(matchUUID5, new NewBetDTO(BetTyp.DRAW, user));
        leagueFacade.setMatchResult(matchUUID, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID4, MatchResult.HOST_WON);
        leagueFacade.setMatchResult(matchUUID5, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        if (dto.isEmpty()) {
            fail();
        } else {
            UserResultDTO userResult = dto.get();
            assertEquals(3, userResult.getPointCounter());
        }
    }

    @Test
    public void ThreeRoundThreeUsers() {
        //when
        String user1 = "user";
        String user2 = "user2";
        String user3 = "user3";
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID matchUUID2 = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host2", "guest2", 2, leagueUUID));
        UUID matchUUID3 = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host2", "guest2", 3, leagueUUID));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, user1));
        betFacade.addBetToMatch(matchUUID2, new NewBetDTO(BetTyp.HOST_WON, user1));
        betFacade.addBetToMatch(matchUUID3, new NewBetDTO(BetTyp.GUEST_WON, user1));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, user2));
        betFacade.addBetToMatch(matchUUID2, new NewBetDTO(BetTyp.DRAW, user2));
        betFacade.addBetToMatch(matchUUID3, new NewBetDTO(BetTyp.HOST_WON, user2));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, user3));
        betFacade.addBetToMatch(matchUUID2, new NewBetDTO(BetTyp.DRAW, user3));
        betFacade.addBetToMatch(matchUUID3, new NewBetDTO(BetTyp.DRAW, user3));
        leagueFacade.setMatchResult(matchUUID, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID2, MatchResult.DRAW);
        leagueFacade.setMatchResult(matchUUID3, MatchResult.DRAW);

        //then
        Option<UserResultDTO> dto1 = resultFacade.getResultsFromLeagueToUser(leagueUUID, user1);
        Option<UserResultDTO> dto2 = resultFacade.getResultsFromLeagueToUser(leagueUUID, user2);
        Option<UserResultDTO> dto3 = resultFacade.getResultsFromLeagueToUser(leagueUUID, user3);
        if (dto1.isEmpty() || dto2.isEmpty() || dto3.isEmpty()) {
            fail();
        } else {
            UserResultDTO userResult1 = dto1.get();
            UserResultDTO userResult2 = dto2.get();
            UserResultDTO userResult3 = dto3.get();
            assertEquals(1, userResult1.getPointCounter());
            assertEquals(2, userResult2.getPointCounter());
            assertEquals(3, userResult3.getPointCounter());
        }
    }


}
