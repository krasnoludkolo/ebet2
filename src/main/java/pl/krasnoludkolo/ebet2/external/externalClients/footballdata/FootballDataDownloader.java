package pl.krasnoludkolo.ebet2.external.externalClients.footballdata;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

class FootballDataDownloader {

    private final String urlBeginning = "http://api.football-data.org/v1/";

    JSONArray downloadAllRounds(ExternalSourceConfiguration config) {
        try {
            String leagueId = config.getParameter("leagueId");
            String url = urlBeginning + "competitions/" + leagueId + "/fixtures";
            return getMatches(url);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    JSONArray downloadRound(ExternalSourceConfiguration config, int round) {
        try {
            String leagueId = config.getParameter("leagueId");
            String url = urlBeginning + "competitions/" + leagueId + "/fixtures?matchday=" + round;
            return getMatches(url);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private JSONArray getMatches(String url) throws UnirestException {
        return getFixturesAsJsonArray(url);
    }

    private JSONArray getFixturesAsJsonArray(String url) throws UnirestException {
        return Unirest.get(url)
                .header("accept", "application/json")
                .asJson()
                .getBody()
                .getObject()
                .getJSONArray("fixtures");
    }
}
