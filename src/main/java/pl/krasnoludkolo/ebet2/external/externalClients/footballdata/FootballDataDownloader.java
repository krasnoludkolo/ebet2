package pl.krasnoludkolo.ebet2.external.externalClients.footballdata;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

class FootballDataDownloader {

    private final static Logger LOGGER = Logger.getLogger(FootballDataDownloader.class.getName());

    private final String urlBeginning = "http://api.football-data.org/v1/";

    JSONArray downloadAllRounds(ExternalSourceConfiguration config) {
        try {
            String leagueId = config.getParameter("leagueId");
            String url = urlBeginning + "competitions/" + leagueId + "/fixtures";
            return getMatches(url);
        } catch (UnirestException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
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
