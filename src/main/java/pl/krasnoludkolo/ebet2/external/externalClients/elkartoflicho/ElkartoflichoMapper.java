package pl.krasnoludkolo.ebet2.external.externalClients.elkartoflicho;

import io.vavr.collection.List;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

class ElkartoflichoMapper {
    public List<MatchInfo> mapToMatchInfoList(JSONArray allRounds) {
        List<MatchInfo> result = List.empty();
        for (int i = 0; i < allRounds.length(); i++) {
            JSONObject match = allRounds.getJSONObject(i);
            MatchInfo matchInfo = mapToMatchInfo(match);
            result = result.append(matchInfo);
        }
        return result;
    }

    private MatchInfo mapToMatchInfo(JSONObject match) {
        String hostName = match.getString("host_team");
        String guestName = match.getString("guest_team");
        int round = match.getInt("match_day");
        boolean finished = match.getString("status").equals("FINISHED");
        MatchResult result = MatchResult.NOT_SET;
        if (finished) {
            int hostGloas = match.getInt("host_result");
            int guestGoals = match.getInt("guest_result");
            result = MatchResult.fromResult(hostGloas, guestGoals);
        }
        return new MatchInfo(hostName, guestName, round, finished, result);
    }

}