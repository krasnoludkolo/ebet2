package pl.krasnoludkolo.ebet2.autoimport;

import java.util.Map;

interface DataImporter {

    LeagueDetails initializeLeague(Map<String, String> config);

    void updateLeagueResults(LeagueDetails leagueDetails);
}
