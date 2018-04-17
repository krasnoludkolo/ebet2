package pl.krasnoludkolo.ebet2.external.externalClients.footballdata;

import io.vavr.collection.List;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

class FootballDataMapper {

    List<MatchInfo> getMatchInfosFromJsonArray(JSONArray fixtures) {
        List<MatchInfo> matchInfos = List.empty();
        for (int i = 0; i < fixtures.length(); i++) {
            JSONObject fixture = fixtures.getJSONObject(i);
            MatchInfo matchInfo = createMatchInfo(fixture);
            matchInfos = matchInfos.append(matchInfo);
        }
        return matchInfos;
    }

    private MatchInfo createMatchInfo(JSONObject fixture) {
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
        return new MatchInfo(homeTeamName, awayTeamName, round, finished, result);
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


}
