package pl.krasnoludkolo.ebet2.external;

import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.bet.BetConfiguration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.external.api.MissingConfigurationException;
import pl.krasnoludkolo.ebet2.external.externalClients.mockclient.ExternalClientMock;
import pl.krasnoludkolo.ebet2.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.ResultConfiguration;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AutoImportFacadeTest {

    private io.vavr.collection.List<MatchInfo> matchList = io.vavr.collection.List.of(
            new MatchInfo("a", "b", 1, true, MatchResult.HOST_WON),
            new MatchInfo("c", "d", 1, true, MatchResult.GUEST_WON),
            new MatchInfo("a", "c", 2, false, MatchResult.NOT_SET),
            new MatchInfo("b", "d", 2, false, MatchResult.NOT_SET),
            new MatchInfo("a", "d", 3, false, MatchResult.NOT_SET),
            new MatchInfo("c", "b", 3, false, MatchResult.NOT_SET)
    );

    private AutoImportFacade autoImportFacade;
    private LeagueFacade leagueFacade;
    private ExternalClientMock externalSourceClient;

    @Before
    public void setUp() {
        BetFacade bet = new BetConfiguration().inMemoryBetFacade();
        ResultFacade result = new ResultConfiguration().inMemoryResult(bet);
        leagueFacade = new LeagueConfiguration().inMemoryLeagueFacade(result);
        externalSourceClient = new ExternalClientMock(matchList);
        autoImportFacade = new AutoImportConfiguration().inMemory(leagueFacade, externalSourceClient);
    }

    @Test
    public void shouldImportLeague() {
        //given
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        config.putParameter("name", "test");
        //when
        UUID uuid = autoImportFacade.initializeLeague(config, "Mock");
        //then
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(uuid).get();
        List<MatchDTO> matchDTOS = leagueDTO.getMatchDTOS();
        assertEquals(6, matchDTOS.size());
        assertEquals("test", leagueDTO.getName());
    }

    @Test(expected = MissingConfigurationException.class)
    public void shouldNotImportLeagueBecauseOfMissingLeagueName() {
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        autoImportFacade.initializeLeague(config, "Mock");
    }

    @Test
    public void shouldUpdateLeague() {
        //given
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        config.putParameter("name", "test");
        UUID uuid = autoImportFacade.initializeLeague(config, "Mock");
        MatchInfo m = externalSourceClient.getMatchList().get(2);
        MatchInfo m2 = externalSourceClient.getMatchList().get(3);
        MatchInfo nm = new MatchInfo(m.getHostName(), m.getGuestName(), m.getRound(), true, MatchResult.DRAW);
        MatchInfo nm2 = new MatchInfo(m2.getHostName(), m2.getGuestName(), m2.getRound(), true, MatchResult.DRAW);
        externalSourceClient.setMatchList(externalSourceClient.getMatchList().replace(m, nm));
        externalSourceClient.setMatchList(externalSourceClient.getMatchList().replace(m2, nm2));
        //when
        autoImportFacade.updateLeague(uuid);
        //then
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(uuid).get();
        int sum = leagueDTO.getMatchDTOS().stream().mapToInt(match -> match.getResult() == MatchResult.DRAW ? 1 : 0).sum();
        assertEquals(2, sum);
    }

}