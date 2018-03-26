package pl.krasnoludkolo.ebet2.autoimport.footballdata;

import io.vavr.collection.List;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.autoimport.api.MatchInfo;

public class FootballDataClient implements ExternalSourceClient {

    private FootballDataDownloader downloader;
    private FootballDataMapper mapper;

    public static FootballDataClient create() {
        FootballDataDownloader downloader = new FootballDataDownloader();
        FootballDataMapper mapper = new FootballDataMapper();
        return new FootballDataClient(downloader, mapper);
    }

    public static FootballDataClient create(FootballDataDownloader downloader) {
        FootballDataMapper mapper = new FootballDataMapper();
        return new FootballDataClient(downloader, mapper);
    }

    private FootballDataClient(FootballDataDownloader downloader, FootballDataMapper mapper) {
        this.downloader = downloader;
        this.mapper = mapper;
    }

    @Override
    public List<MatchInfo> downloadRound(ExternalSourceConfiguration config, int round) {
        JSONArray fixtures = downloader.downloadRound(config, round);
        return mapper.getMatchInfosFromJsonArray(fixtures);
    }

    @Override
    public List<MatchInfo> downloadAllRounds(ExternalSourceConfiguration config) {
        JSONArray fixtures = downloader.downloadAllRounds(config);
        return mapper.getMatchInfosFromJsonArray(fixtures);
    }

    @Override
    public String getShortcut() {
        return "FootBallData";
    }
}
