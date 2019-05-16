package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.external.api.MissingConfigurationException;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ExternalFacadeTest {

    private ExternalFacade externalFacade;

    @Before
    public void setUp() {
        InMemorySystem system = new InMemorySystem();
        externalFacade = system.externalFacade();

    }

    @Test
    public void shouldDownloadLeague() {
        //given
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty();
        //when
        UUID uuid = externalFacade.initializeLeagueConfiguration(config, "Mock", UUID.randomUUID()).get();
        //then
        List<MatchInfo> matchDTOS = externalFacade.downloadLeague(uuid).get();
        assertEquals(6, matchDTOS.size());
    }

    @Test
    public void shouldNotDownloadNonExistingLeague() {
        ExternalError error = externalFacade.downloadLeague(UUID.randomUUID()).getLeft();
        assertEquals(ExternalError.NO_LEAGUE_DETAILS, error);
    }

    @Test(expected = MissingConfigurationException.class)
    public void shouldNotImportLeagueBecauseOfMissingLeagueName() {
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty();
        config.getParameter("asd");
    }

}