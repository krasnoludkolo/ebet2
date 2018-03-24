package pl.krasnoludkolo.ebet2.autoimport;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vavr.control.Option;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.autoimport.api.MissingConfigurationException;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.util.Map;
import java.util.UUID;

class FootballdataImporter implements DataImporter {

    private LeagueFacade leagueFacade;
    private final String urlBeginning = "http://api.football-data.org/v1/";

    FootballdataImporter(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }


    @Override
    public LeagueDetails initializeLeague(Map<String, String> config) {
        String leagueId = Option.of(config.get("leagueId")).getOrElseThrow(() -> {
            throw new MissingConfigurationException("Missing leagueId");
        });
        String name = Option.of(config.get("name")).getOrElseThrow(() -> {
            throw new MissingConfigurationException("Missing name");
        });
        int id = Integer.parseInt(leagueId);
        UUID leagueUUID = leagueFacade.createLeague(name);

        try {
            createMatchesFromLeague(leagueId, leagueUUID);
        } catch (UnirestException e) {
            e.printStackTrace();
        }


        return new LeagueDetails(leagueUUID, id, 0);
    }

    private void createMatchesFromLeague(String leagueId, UUID leagueUUID) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(urlBeginning + "competitions/" + leagueId + "/fixtures")
                .header("accept", "application/json")
                .asJson();
        JSONArray fixtures = jsonResponse
                .getBody()
                .getObject()
                .getJSONArray("fixtures");
        for (int i = 0; i < fixtures.length(); i++) {
            JSONObject fixture = fixtures.getJSONObject(i);
            String homeTeamName = fixture.getString("homeTeamName");
            String awayTeamName = fixture.getString("awayTeamName");
            int round = fixture.getInt("matchday");
            String status = fixture.getString("status");

            NewMatchDTO dto = new NewMatchDTO(homeTeamName, awayTeamName, round, leagueUUID);
            UUID matchUUID = leagueFacade.addMatchToLeague(dto);

            if (status.equals("FINISHED")) {
                JSONObject result = fixture.getJSONObject("result");
                int goalsHomeTeam = result.getInt("goalsHomeTeam");
                int goalsAwayTeam = result.getInt("goalsAwayTeam");
                MatchResult matchResult = getMatchResult(goalsHomeTeam, goalsAwayTeam);
                leagueFacade.setMatchResult(matchUUID, matchResult);
            }
        }
    }

    private MatchResult getMatchResult(int g1, int g2) {
        if (g1 > g2) {
            return MatchResult.HOST_WON;
        } else if (g1 < g2) {
            return MatchResult.GUEST_WON;
        } else {
            return MatchResult.DRAW;
        }
    }

    @Override
    public void updateLeagueResults(LeagueDetails leagueDetails) {

    }
}
