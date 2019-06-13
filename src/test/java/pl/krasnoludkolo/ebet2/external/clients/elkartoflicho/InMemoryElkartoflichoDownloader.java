package pl.krasnoludkolo.ebet2.external.clients.elkartoflicho;

import io.vavr.control.Either;
import org.json.JSONArray;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

final class InMemoryElkartoflichoDownloader extends ElkartoflichoDownloader {

    private JSONArray jsonArray;

    InMemoryElkartoflichoDownloader() {
        jsonArray = loadData();
    }

    private JSONArray loadData() {
        return new JSONArray(json);
    }

    @Override
    public Either<ExternalError, JSONArray> downloadAllRounds(ExternalSourceConfiguration config) {
        return Either.right(jsonArray);
    }

    private String json = "[\n" +
            "  {\n" +
            "    \"data\": \"2017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"1\",\n" +
            "    \"guest_team\": \"\\u015al\\u0105sk Wroc\\u0142aw\",\n" +
            "    \"host_result\": \"2\",\n" +
            "    \"host_team\": \"Bruk-Bet Termalica Nieciecza\",\n" +
            "    \"id\": 7458,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 20,\n" +
            "    \"status\": \"FINISHED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"2017-06-05T12:00:00\",\n" +
            "    \"guest_result\": \"4\",\n" +
            "    \"guest_team\": \"Wis\\u0142a Krak\\u00f3w\",\n" +
            "    \"host_result\": \"1\",\n" +
            "    \"host_team\": \"Cracovia\",\n" +
            "    \"id\": 7459,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 20,\n" +
            "    \"status\": \"FINISHED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"3017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"1\",\n" +
            "    \"guest_team\": \"Korona Kielce\",\n" +
            "    \"host_result\": \"5\",\n" +
            "    \"host_team\": \"Jagiellonia Bia\\u0142ystok\",\n" +
            "    \"id\": 7460,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 20,\n" +
            "    \"status\": \"FINISHED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"2017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"3\",\n" +
            "    \"guest_team\": \"Pogo\\u0144 Szczecin\",\n" +
            "    \"host_result\": \"1\",\n" +
            "    \"host_team\": \"Lechia Gda\\u0144sk\",\n" +
            "    \"id\": 7461,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 21,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"2017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"1\",\n" +
            "    \"guest_team\": \"Legia Warszawa\",\n" +
            "    \"host_result\": \"0\",\n" +
            "    \"host_team\": \"Piast Gliwice\",\n" +
            "    \"id\": 7462,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 21,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": \"3017-08-05T18:00:00\",\n" +
            "    \"guest_result\": \"2\",\n" +
            "    \"guest_team\": \"Sandecja Nowy S\\u0105cz\",\n" +
            "    \"host_result\": \"2\",\n" +
            "    \"host_team\": \"Wis\\u0142a P\\u0142ock\",\n" +
            "    \"id\": 7463,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 21,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"data\": null,\n" +
            "    \"guest_result\": \"NotSet\",\n" +
            "    \"guest_team\": \"Zag\\u0142\\u0119bie Lubin\",\n" +
            "    \"host_result\": \"NotSet\",\n" +
            "    \"host_team\": \"Mied\\u017a Legnica\",\n" +
            "    \"id\": 55920,\n" +
            "    \"league_id\": \"ekstraklasa\",\n" +
            "    \"match_day\": 7,\n" +
            "    \"status\": \"SCHEDULED\"\n" +
            "  }\n" +
            "]";

}
