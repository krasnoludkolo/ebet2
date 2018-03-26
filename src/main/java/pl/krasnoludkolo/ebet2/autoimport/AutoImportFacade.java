package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

public class AutoImportFacade {

    private LeagueInitializer leagueInitializer;
    private Map<String, ExternalSourceClient> clientsMap;

    AutoImportFacade(LeagueFacade leagueFacade, List<ExternalSourceClient> clients) {
        leagueInitializer = new LeagueInitializer(leagueFacade);
        clientsMap = clients.toMap(client -> Tuple.of(client.getShortcut(), client));
    }

    public void initializeLeague(ExternalSourceConfiguration config, String clientShortcut) {
        ExternalSourceClient client = clientsMap.get(clientShortcut).getOrElseThrow(IllegalArgumentException::new);
        LeagueDetails leagueDetails = leagueInitializer.initializeLeague(client, config);
    }

    //TODO updateLeague
    public void updateLeague() {

    }

}
