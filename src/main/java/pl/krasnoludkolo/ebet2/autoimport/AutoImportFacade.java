package pl.krasnoludkolo.ebet2.autoimport;

import pl.krasnoludkolo.ebet2.league.LeagueFacade;

import java.util.HashMap;
import java.util.Map;

public class AutoImportFacade {

    AutoImportFacade(LeagueFacade leagueFacade) {
        FootballdataImporter importer = new FootballdataImporter(leagueFacade);
        Map<String, String> config = new HashMap<>();
        config.put("leagueId", "450");
        config.put("name", "test");
        importer.initializeLeague(config);
    }

}
