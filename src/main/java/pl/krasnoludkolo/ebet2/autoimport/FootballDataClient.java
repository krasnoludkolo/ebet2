package pl.krasnoludkolo.ebet2.autoimport;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vavr.collection.List;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

class FootballDataClient implements ExternalSourceClient {

    private final String urlBeginning = "http://api.football-data.org/v1/";

    @Override
    public List<MatchInfo> downloadAllRounds(ExternalSourceConfiguration config) {
        try {
            String leagueId = config.getParameter("leagueId");
            return createMatchesFromLeague(leagueId);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return List.empty();
    }

    private List<MatchInfo> createMatchesFromLeague(String leagueId) throws UnirestException {
        JSONArray fixtures = Unirest.get(urlBeginning + "competitions/" + leagueId + "/fixtures")
                .header("accept", "application/json")
                .asJson()
                .getBody()
                .getObject()
                .getJSONArray("fixtures");
        List<MatchInfo> matchInfos = List.empty();
        for (int i = 0; i < fixtures.length(); i++) {
            JSONObject fixture = fixtures.getJSONObject(i);
            String homeTeamName = fixture.getString("homeTeamName");
            String awayTeamName = fixture.getString("awayTeamName");
            int round = fixture.getInt("matchday");
            String status = fixture.getString("status");

            boolean finished = status.equals("FINISHED");
            MatchResult result = MatchResult.NOT_SET;
            if (finished) {
                JSONObject matchResult = fixture.getJSONObject("result");
                int goalsHomeTeam = matchResult.getInt("goalsHomeTeam");
                int goalsAwayTeam = matchResult.getInt("goalsAwayTeam");
                result = getMatchResult(goalsHomeTeam, goalsAwayTeam);
            }
            MatchInfo matchInfo = new MatchInfo(homeTeamName, awayTeamName, round, finished, result);
            matchInfos = matchInfos.append(matchInfo);
        }
        return matchInfos;
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

    //TODO downloadRound
    @Override
    public List<MatchInfo> downloadRound(ExternalSourceConfiguration config, int round) {
        return List.empty();
    }

    @Override
    public String getShortcut() {
        return "FootballData";
    }
}
