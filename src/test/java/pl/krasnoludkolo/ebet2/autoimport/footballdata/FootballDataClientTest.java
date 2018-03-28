package pl.krasnoludkolo.ebet2.autoimport.footballdata;

import io.vavr.collection.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.autoimport.api.MatchInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class FootballDataClientTest {


    private String fileName = "src/test/java/pl/krasnoludkolo/ebet2/autoimport/footballdata/data.json";

    @Mock
    private FootballDataDownloader downloader;

    private FootballDataClient client;
    private ExternalSourceConfiguration config;


    @Before
    public void init() throws FileNotFoundException {
        MockitoAnnotations.initMocks(this);
        client = FootballDataClient.create(downloader);
        config = new ExternalSourceConfiguration();
        JSONArray fixedList = loadFromFile();
        when(downloader.downloadAllRounds(config)).thenReturn(fixedList);
    }

    private JSONArray loadFromFile() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName), "UTF-8");
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return new JSONObject(text).getJSONArray("fixtures");
    }

    @Test
    public void shouldMapDownloadedData() {
        //when
        List<MatchInfo> matchInfos = client.downloadAllRounds(config);
        //then
        assertEquals(6, matchInfos.size());
        for (MatchInfo info : matchInfos) {
            assertNotNull(info);
        }
    }
}