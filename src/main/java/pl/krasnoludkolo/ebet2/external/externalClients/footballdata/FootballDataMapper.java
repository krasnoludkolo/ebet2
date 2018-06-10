package pl.krasnoludkolo.ebet2.external.externalClients.footballdata;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

class FootballDataMapper {

    List<MatchInfo> getMatchInfosFromJsonArray(JSONArray fixtures) {
        List<MatchInfo> matchInfos = List.empty();
        for (int i = 0; i < fixtures.length(); i++) {
            JSONObject fixture = fixtures.getJSONObject(i);
            Option<MatchInfo> matchInfo = createMatchInfo(fixture);
            if (matchInfo.isDefined()) {
                matchInfos = matchInfos.append(matchInfo.get());
            }
        }
        return matchInfos;
    }

    private Option<MatchInfo> createMatchInfo(JSONObject fixture) {
        String homeTeamName = fixture.getString("homeTeamName");
        String awayTeamName = fixture.getString("awayTeamName");
        int round = fixture.getInt("matchday");
        String status = fixture.getString("status");
        boolean finished = status.equals("FINISHED");
        LocalDateTime startDate = getDateFromFixtureJSONObject(fixture);
        MatchResult result = MatchResult.NOT_SET;
        if (finished) {
            JSONObject matchResult = fixture.getJSONObject("result");
            int goalsHomeTeam = matchResult.getInt("goalsHomeTeam");
            int goalsAwayTeam = matchResult.getInt("goalsAwayTeam");
            result = MatchResult.fromResult(goalsHomeTeam, goalsAwayTeam);
        }
        if (homeTeamName.equals("") || awayTeamName.equals("")) {
            return Option.none();
        }
        MatchInfo matchInfo = new MatchInfo(homeTeamName, awayTeamName, round, finished, result, startDate);
        return Option.of(matchInfo);
    }

    private LocalDateTime getDateFromFixtureJSONObject(JSONObject fixture) {
        return ZonedDateTime.parse(fixture.getString("date")).toLocalDateTime();
    }

}
