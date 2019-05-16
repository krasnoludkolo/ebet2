package pl.krasnoludkolo.ebet2.external.clients.elkartoflicho;

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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ElkartoflichoClientTest {

    @Mock
    private ElkartoflichoDownloader downloader;

    private ElkartoflichoClient elkartoflichoClient;
    private ExternalSourceConfiguration config = new ExternalSourceConfiguration(null);

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        elkartoflichoClient = new ElkartoflichoClient(downloader);
        JSONArray jsonArray = loadData();
        when(downloader.downloadAllRounds(config)).thenReturn(jsonArray);
    }


    private JSONArray loadData() {
        return new JSONArray(json);
    }

    @Test
    public void shouldDownloadSixMatches() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config);
        assertEquals(6, matchInfos.size());
    }

    @Test
    public void shouldBeThreeMatchesWithResult() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config)
                .filter(matchInfo -> matchInfo.getResult() != MatchResult.NOT_SET);
        assertEquals(3, matchInfos.size());
    }

    @Test
    public void shouldBeThreeMatchesWithoutResult() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config)
                .filter(matchInfo -> matchInfo.getResult() == MatchResult.NOT_SET);
        assertEquals(3, matchInfos.size());
    }

    @Test
    public void shouldBeThreeMatchesIn20thRound() {
        List<MatchInfo> matchInfos = elkartoflichoClient.downloadAllRounds(config)
                .filter(matchInfo -> matchInfo.getRound() == 20);
        assertEquals(3, matchInfos.size());
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

    private String json = "[\n" +
            "  {\n" +
            "    \"data\": \"2017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"1\",\n" +
            "    \"guest_team\": \"\\u015al\\u0105sk Wroc\\u0142aw\",\n" +
            "    \"host_result\": \"2\",\n" +
            "    \"host_team\": \"Bruk-Bet Termalica Nieciecza\",\n" +
            "    \"id\": 7458,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 20,\n" +
            "    \"status\": \"FINISHED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"2017-06-05T12:00:00\",\n" +
            "    \"guest_result\": \"4\",\n" +
            "    \"guest_team\": \"Wis\\u0142a Krak\\u00f3w\",\n" +
            "    \"host_result\": \"1\",\n" +
            "    \"host_team\": \"Cracovia\",\n" +
            "    \"id\": 7459,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 20,\n" +
            "    \"status\": \"FINISHED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"3017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"1\",\n" +
            "    \"guest_team\": \"Korona Kielce\",\n" +
            "    \"host_result\": \"5\",\n" +
            "    \"host_team\": \"Jagiellonia Bia\\u0142ystok\",\n" +
            "    \"id\": 7460,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 20,\n" +
            "    \"status\": \"FINISHED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"2017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"3\",\n" +
            "    \"guest_team\": \"Pogo\\u0144 Szczecin\",\n" +
            "    \"host_result\": \"1\",\n" +
            "    \"host_team\": \"Lechia Gda\\u0144sk\",\n" +
            "    \"id\": 7461,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 21,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"2017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"1\",\n" +
            "    \"guest_team\": \"Legia Warszawa\",\n" +
            "    \"host_result\": \"0\",\n" +
            "    \"host_team\": \"Piast Gliwice\",\n" +
            "    \"id\": 7462,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 21,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"3017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"2\",\n" +
            "    \"guest_team\": \"Sandecja Nowy S\\u0105cz\",\n" +
            "    \"host_result\": \"2\",\n" +
            "    \"host_team\": \"Wis\\u0142a P\\u0142ock\",\n" +
            "    \"id\": 7463,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 21,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": null,\n" +
            "    \"guest_result\": \"NotSet\",\n" +
            "    \"guest_team\": \"Zag\\u0142\\u0119bie Lubin\",\n" +
            "    \"host_result\": \"NotSet\",\n" +
            "    \"host_team\": \"Mied\\u017a Legnica\",\n" +
            "    \"id\": 55920,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 7,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  }\n" +
            "]";

}