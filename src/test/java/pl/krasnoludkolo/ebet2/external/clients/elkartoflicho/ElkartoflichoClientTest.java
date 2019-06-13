package pl.krasnoludkolo.ebet2.external.clients.elkartoflicho;

import io.vavr.collection.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import static org.junit.Assert.assertEquals;

public class ElkartoflichoClientTest {


    private ElkartoflichoClient elkartoflichoClient;
    private ExternalSourceConfiguration config = new ExternalSourceConfiguration(null, "Mock");

    @Before
    public void init() {
        ElkartoflichoDownloader downloader = new InMemoryElkartoflichoDownloader();
        elkartoflichoClient = new ElkartoflichoClient(downloader);
    }

    @Test
    public void shouldDownloadSixMatches() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config).get();
        assertEquals(6, matchInfos.size());
    }

    @Test
    public void shouldBeThreeMatchesWithResult() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config).get()
                .filter(matchInfo -> matchInfo.getResult() != MatchResult.NOT_SET);
        assertEquals(3, matchInfos.size());
    }

    @Test
    public void shouldBeThreeMatchesWithoutResult() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config).get()
                .filter(matchInfo -> matchInfo.getResult() == MatchResult.NOT_SET);
        assertEquals(3, matchInfos.size());
    }

    @Test
    public void shouldBeThreeMatchesIn20thRound() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config).get()
                .filter(matchInfo -> matchInfo.getRound() == 20);
        assertEquals(3, matchInfos.size());
    }

    @Test
    public void shouldNotBeAnyNulls() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config).get();
        matchInfos.forEach(Assert::assertNotNull);
    }

    @Test
    public void shouldSkipMatchesWithoutDate() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config).get();
        assertEquals(6, matchInfos.size());
    }

}