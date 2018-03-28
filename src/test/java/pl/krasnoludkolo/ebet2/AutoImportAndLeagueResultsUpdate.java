package pl.krasnoludkolo.ebet2;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.autoimport.AutoImportFacade;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.autoimport.api.MatchInfo;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
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

    private AutoImportFacade autoImportFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private LeagueFacade leagueFacade;
    private InMemorySystem system;

    @Before
    public void init() {
        system = new InMemorySystem();
        autoImportFacade = system.autoImportFacade();
        betFacade = system.betFacade();
        resultFacade = system.resultFacade();
        leagueFacade = system.leagueFacade();
    }

    @Test
    public void shouldImportLeagueMakeBetAndUpdateResult() {
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        config.putParameter("name", "test");
        UUID leagueUUID = autoImportFacade.initializeLeague(config, "Mock");
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(leagueUUID).get();
        MatchDTO matchDTO = leagueDTO.getMatchDTOS().stream().filter(m -> m.getResult() == MatchResult.NOT_SET).findFirst().get();
        UUID matchUUID = matchDTO.getUuid();
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, "user1", matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, "user2", matchUUID));
        List<MatchInfo> list = getListWithNewResult();
        system.setExternalSourceMatchList(list);
        autoImportFacade.updateLeague(leagueUUID);
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
