package pl.krasnoludkolo.ebet2.external.clients.footballdata;

import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FootballDataClientTest {

    private FootballDataClient client;
    private ExternalSourceConfiguration config;

    @Before
    public void init() {
        FootballDataDownloader downloader = new StubFootballDataDownloader();
        client = FootballDataClient.create(downloader);
        config = ExternalSourceConfiguration.empty();
    }

    @Test
    public void shouldMapDownloadedData() {
        //when
        List<MatchInfo> matchInfos = client.downloadAllRounds(config);
        //then
        assertEquals(5, matchInfos.size());
        for (MatchInfo info : matchInfos) {
            assertNotNull(info);
        }
    }
}
