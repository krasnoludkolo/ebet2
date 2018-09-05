package pl.krasnoludkolo.ebet2.external.clients.elkartoflicho;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

class ElkartoflichoDownloader {

    private static final Logger LOGGER = Logger.getLogger(ElkartoflichoDownloader.class.getName());

    private static final String URL_BEGINNING = "https://elkartoflicho.herokuapp.com/";


    public JSONArray downloadAllRounds(ExternalSourceConfiguration config) {
        try {
            String url = URL_BEGINNING + config.getParameter("apiToken") + "/" + config.getParameter("name");
            return downloadAllMatchesFromLeague(url);
        } catch (UnirestException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new IllegalStateException("Error when downloading");
        }
    }


    private JSONArray downloadAllMatchesFromLeague(String url) throws UnirestException {
        return Unirest.get(url)
                .header("accept", "application/json")
                .asJson()
                .getBody()
                .getArray();
    }
}
