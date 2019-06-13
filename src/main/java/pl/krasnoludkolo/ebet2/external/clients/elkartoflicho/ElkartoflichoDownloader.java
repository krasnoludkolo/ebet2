package pl.krasnoludkolo.ebet2.external.clients.elkartoflicho;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

class ElkartoflichoDownloader {

    private static final Logger LOGGER = Logger.getLogger(ElkartoflichoDownloader.class.getName());

    private static final String URL_BEGINNING = "https://elkartoflicho.herokuapp.com/";


    public Either<ExternalError, JSONArray> downloadAllRounds(ExternalSourceConfiguration config) {
        String url = URL_BEGINNING + config.getParameter("apiToken") + "/" + config.getParameter("name");
        return getMatches(url);
    }

    private Either<ExternalError, JSONArray> getMatches(String url) {
        return Try.of(() -> downloadAllMatchesFromLeague(url))
                .onFailure(throwable -> LOGGER.log(Level.SEVERE, throwable.getMessage()))
                .toEither()
                .mapLeft(x -> ExternalError.ERROR_DURING_DOWNLOADING);
    }

    private JSONArray downloadAllMatchesFromLeague(String url) throws UnirestException {
        return Unirest.get(url)
                .header("accept", "application/json")
                .asJson()
                .getBody()
                .getArray();
    }
}
