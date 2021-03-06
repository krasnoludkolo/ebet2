package pl.krasnoludkolo.ebet2.external.clients.footballdata;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

class FootballDataDownloader {

    private static final String URL_BEGINNING = "http://api.football-data.org/v2/";
    private static final String TOKEN = System.getenv("FOOTBALLDATA_TOKEN");
    private static final Map<String, String> headers = new HashMap<>();

    private static final Logger LOGGER = getLogger(FootballDataDownloader.class.getName());

    static {
        headers.put("accept", "application/json");
        headers.put("X-Auth-Token", TOKEN);
    }

    Either<ExternalError, JSONArray> downloadAllRounds(ExternalSourceConfiguration config) {
        String leagueId = config.getParameter("leagueId");
        String url = URL_BEGINNING + "competitions/" + leagueId + "/matches";
        return getMatches(url);
    }

    private Either<ExternalError, JSONArray> getMatches(String url) {
        return Try.of(() -> getFixturesAsJsonArray(url))
                .onFailure(throwable -> LOGGER.log(Level.SEVERE, throwable.getMessage()))
                .toEither()
                .mapLeft(x -> ExternalError.ERROR_DURING_DOWNLOADING);
    }

    private JSONArray getFixturesAsJsonArray(String url) throws UnirestException {
        return Unirest.get(url)
                .headers(headers)
                .asJson()
                .getBody()
                .getObject()
                .getJSONArray("matches");
    }
}
