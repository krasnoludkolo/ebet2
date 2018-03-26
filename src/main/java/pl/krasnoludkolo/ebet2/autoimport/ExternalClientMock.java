package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.autoimport.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

class ExternalClientMock implements ExternalSourceClient {


    static List<MatchInfo> SOME_MATCHES = List.of(

            new MatchInfo("a", "b", 1, true, MatchResult.HOST_WON),
            new MatchInfo("c", "d", 1, true, MatchResult.GUEST_WON),
            new MatchInfo("a", "c", 2, false, MatchResult.NOT_SET),
            new MatchInfo("b", "d", 2, false, MatchResult.NOT_SET),
            new MatchInfo("a", "d", 3, false, MatchResult.NOT_SET),
            new MatchInfo("c", "b", 3, false, MatchResult.NOT_SET)
    );

    private List<MatchInfo> matchList;

    ExternalClientMock(List<MatchInfo> matchList) {
        this.matchList = matchList;
    }


    @Override
    public List<MatchInfo> downloadRound(ExternalSourceConfiguration config, int round) {
        return matchList
                .filter(m -> m.getRound() == round);
    }

    @Override
    public List<MatchInfo> downloadAllRounds(ExternalSourceConfiguration config) {
        return matchList;
    }

    @Override
    public String getShortcut() {
        return "Mock";
    }

    List<MatchInfo> getMatchList() {
        return matchList;
    }

    void setMatchList(List<MatchInfo> matchList) {
        this.matchList = matchList;
    }

}
