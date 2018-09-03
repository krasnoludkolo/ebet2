package pl.krasnoludkolo.ebet2.external;

import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MissingConfigurationException;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ExternalFacadeTest {

    private ExternalFacade externalFacade;
    private LeagueFacade leagueFacade;
    private InMemorySystem system;

    @Before
    public void setUp() {
        system = new InMemorySystem();
        leagueFacade = system.leagueFacade();
        externalFacade = system.externalFacade();

    }

    @Test
    public void shouldImportLeague() {
        //given
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        //when
        UUID uuid = externalFacade.initializeLeague(config, "Mock", "testName").get();
        //then
        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(uuid).get();
        List<MatchDTO> matchDTOS = leagueDTO.getMatchDTOS();
        assertEquals(6, matchDTOS.size());
        assertEquals("testName", leagueDTO.getName());
    }

    @Test(expected = MissingConfigurationException.class)
    public void shouldNotImportLeagueBecauseOfMissingLeagueName() {
        ExternalSourceConfiguration config = new ExternalSourceConfiguration();
        config.getParameter("asd");
    }

}