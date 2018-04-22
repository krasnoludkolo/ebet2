package pl.krasnoludkolo.ebet2.external.externalClients.elkartoflicho;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

class ElkartoflichoDownloader {

    private final static Logger LOGGER = Logger.getLogger(ElkartoflichoDownloader.class.getName());

    private final String urlBeginning = "https://elkartoflicho.herokuapp.com/";


    public JSONArray downloadAllRounds(ExternalSourceConfiguration config) {
        try {
            String url = urlBeginning + config.getParameter("apiToken") + "/" + config.getParameter("name");
            return downloadAllMatchesFromLeague(url);
        } catch (UnirestException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return new JSONArray("[]");
    }


    private JSONArray downloadAllMatchesFromLeague(String url) throws UnirestException {
        return Unirest.get(url)
                .header("accept", "application/json")
                .asJson()
                .getBody()
                .getArray();
    }
}
