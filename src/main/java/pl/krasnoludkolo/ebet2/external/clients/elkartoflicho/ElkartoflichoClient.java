package pl.krasnoludkolo.ebet2.external.clients.elkartoflicho;

import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;

public class ElkartoflichoClient implements ExternalSourceClient {

    private ElkartoflichoDownloader downloader;
    private ElkartoflichoMapper mapper;

    ElkartoflichoClient(ElkartoflichoDownloader downloader) {
        this.downloader = downloader;
        mapper = new ElkartoflichoMapper();
    }

    public ElkartoflichoClient() {
        downloader = new ElkartoflichoDownloader();
        mapper = new ElkartoflichoMapper();
    }


    @Override
    public Either<ExternalError, List<MatchInfo>> downloadAllRounds(ExternalSourceConfiguration config) {
        return downloader.downloadAllRounds(config)
                .map(mapper::mapToMatchInfoList);
    }

    @Override
    public String getShortcut() {
        return "elkartoflicho";
    }
}
