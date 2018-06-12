package pl.krasnoludkolo.ebet2.external.externalClients.elkartoflicho;

import io.vavr.collection.List;
import org.json.JSONArray;
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
    public List<MatchInfo> downloadAllRounds(ExternalSourceConfiguration config) {
        JSONArray allRounds = downloader.downloadAllRounds(config);
        return mapper.mapToMatchInfoList(allRounds);
    }

    @Override
    public String getShortcut() {
        return "elkartoflicho";
    }
}
