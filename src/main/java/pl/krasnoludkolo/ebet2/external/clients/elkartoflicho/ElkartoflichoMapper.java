package pl.krasnoludkolo.ebet2.external.clients.elkartoflicho;

import io.vavr.collection.List;
import io.vavr.control.Try;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;

import java.time.LocalDateTime;

class ElkartoflichoMapper {

    List<MatchInfo> mapToMatchInfoList(JSONArray allRounds) {
        List<MatchInfo> result = List.empty();
        for (int i = 0; i < allRounds.length(); i++) {
            JSONObject match = allRounds.getJSONObject(i);
            MatchInfo matchInfo = mapToMatchInfo(match);
            if (matchInfo.getMatchStartDate() == null) {
                continue;
            }
            result = result.append(matchInfo);
        }
        return result;
    }

    private MatchInfo mapToMatchInfo(JSONObject match) {
        String hostName = match.getString("host_team");
        String guestName = match.getString("guest_team");
        int round = match.getInt("match_day");
        boolean finished = match.getString("status").equals("FINISHED");
        LocalDateTime startDate = getDataFromMatchJSONObject(match);
        MatchResult result = MatchResult.NOT_SET;
        if (finished) {
            int hostGoals = match.getInt("host_result");
            int guestGoals = match.getInt("guest_result");
            result = MatchResult.fromResult(hostGoals, guestGoals);
        }
        return new MatchInfo(hostName, guestName, round, finished, result, startDate);
    }

    private LocalDateTime getDataFromMatchJSONObject(JSONObject match) {
        return Try.of(() -> parseDateFromJson(match)).getOrNull();
    }

    private LocalDateTime parseDateFromJson(JSONObject match) {
        return LocalDateTime.parse(match.getString("data"));
    }

}
