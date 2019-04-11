package pl.krasnoludkolo.ebet2.external.clients.footballdata;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class FootballDataDownloader {

    private static final Logger LOGGER = Logger.getLogger(FootballDataDownloader.class.getName());

    private static final String URL_BEGINNING = "http://api.football-data.org/v2/";
    private static final String TOKEN = System.getenv("FOOTBALLDATA_TOKEN");
    private static final Map<String, String> headers = new HashMap<>();

    static {
        headers.put("accept", "application/json");
        headers.put("X-Auth-Token", TOKEN);
    }

    JSONArray downloadAllRounds(ExternalSourceConfiguration config) {
        try {
            String leagueId = config.getParameter("leagueId");
            String url = URL_BEGINNING + "competitions/" + leagueId + "/matches";
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
        JSONObject object = Unirest.get(url)
                .headers(headers)
                .asJson()
                .getBody()
                .getObject();
        return object
                .getJSONArray("matches");
    }
}
