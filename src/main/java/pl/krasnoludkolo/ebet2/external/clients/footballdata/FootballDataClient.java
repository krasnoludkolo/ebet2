package pl.krasnoludkolo.ebet2.external.clients.footballdata;

import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;

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
    public Either<ExternalError, List<MatchInfo>> downloadAllRounds(ExternalSourceConfiguration config) {
        return downloader.downloadAllRounds(config)
                .map(mapper::getMatchInfosFromJsonArray);
    }

    @Override
    public String getShortcut() {
        return "FootBallData";
    }
}
