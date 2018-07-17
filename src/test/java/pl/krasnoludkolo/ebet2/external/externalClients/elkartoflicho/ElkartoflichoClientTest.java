package pl.krasnoludkolo.ebet2.external.externalClients.elkartoflicho;

import io.vavr.collection.List;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ElkartoflichoClientTest {

    private String fileName = "src/test/java/pl/krasnoludkolo/ebet2/external/externalClients/elkartoflicho/data.json";

    @Mock
    private ElkartoflichoDownloader downloader;

    private ElkartoflichoClient elkartoflichoClient;
    private ExternalSourceConfiguration config = new ExternalSourceConfiguration();


    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        elkartoflichoClient = new ElkartoflichoClient(downloader);
        JSONArray jsonArray = loadFromFile();
        when(downloader.downloadAllRounds(config)).thenReturn(jsonArray);
    }


    private JSONArray loadFromFile() throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file, "UTF-8");
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return new JSONArray(text);
    }

    @Test
    public void shouldDownloadSixMatches() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config);
        assertTrue(matchInfos.size() == 6);
    }

    @Test
    public void shouldBeThreeMatchesWithResult() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config)
                .filter(matchInfo -> matchInfo.getResult() != MatchResult.NOT_SET);
        assertTrue(matchInfos.size() == 3);
    }

    @Test
    public void shouldBeThreeMatchesWithoutResult() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config)
                .filter(matchInfo -> matchInfo.getResult() == MatchResult.NOT_SET);
        assertTrue(matchInfos.size() == 3);
    }

    @Test
    public void shouldBeThreeMatchesIn20thRound() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config)
                .filter(matchInfo -> matchInfo.getRound() == 20);
        assertTrue(matchInfos.size() == 3);
    }

    @Test
    public void shouldNotBeAnyNulls() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config);
        matchInfos.forEach(Assert::assertNotNull);
    }

    @Test
    public void shouldSkipMatchesWithoutDate() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config);
        assertEquals(6, matchInfos.size());
    }

}