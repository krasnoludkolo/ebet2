package pl.krasnoludkolo.ebet2;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AutoImportAndLeagueResultsUpdate {

    private ExternalFacade externalFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private LeagueFacade leagueFacade;
    private InMemorySystem system;
    private String auth;
    private String auth2;

    @Before
    public void init() {
        system = new InMemorySystem();
        externalFacade = system.externalFacade();
        betFacade = system.betFacade();
        resultFacade = system.resultFacade();
        leagueFacade = system.leagueFacade();
        auth = system.getSampleUsersApiToken().get(0);
        auth2 = system.getSampleUsersApiToken().get(1);

    }

    @Test
    public void shouldImportLeagueMakeBetAndUpdateResult() {
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        UUID leagueUUID = externalFacade.initializeLeague(config, "Mock", "testName");
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(leagueUUID).get();
        MatchDTO matchDTO = leagueDTO.getMatchDTOS().stream().filter(m -> m.getResult() == MatchResult.NOT_SET).findFirst().get();
        UUID matchUUID = matchDTO.getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID), auth2);
        List<MatchInfo> list = getListWithNewResult();
        system.setExternalSourceMatchList(list);
        externalFacade.updateLeague(leagueUUID);
        UserResultDTO user1 = resultFacade.getResultsFromLeagueToUser(leagueUUID, "user1").get();
        Option<UserResultDTO> user2 = resultFacade.getResultsFromLeagueToUser(leagueUUID, "user2");
        assertEquals(1, user1.getPointCounter());
        assertTrue(user2.isEmpty());
    }


    private List<MatchInfo> getListWithNewResult() {
        List<MatchInfo> externalSourceMatchList = system.getExternalSourceMatchList();
        MatchInfo m = externalSourceMatchList.get(2);
        MatchInfo nm = new MatchInfo(m.getHostName(), m.getGuestName(), m.getRound(), true, MatchResult.DRAW);
        externalSourceMatchList = externalSourceMatchList.replace(m, nm);
        return externalSourceMatchList;
    }
}
