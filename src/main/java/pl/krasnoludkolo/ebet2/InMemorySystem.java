package pl.krasnoludkolo.ebet2;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.bet.BetConfiguration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.external.AutoImportConfiguration;
import pl.krasnoludkolo.ebet2.external.AutoImportFacade;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.external.externalClients.mockclient.ExternalClientMock;
import pl.krasnoludkolo.ebet2.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.results.ResultConfiguration;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

public class InMemorySystem {

    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private AutoImportFacade autoImportFacade;
    private ExternalClientMock externalClientMock;

    public InMemorySystem() {
        externalClientMock = new ExternalClientMock(ExternalClientMock.SOME_MATCHES);
        betFacade = new BetConfiguration().inMemoryBetFacade();
        resultFacade = new ResultConfiguration().inMemoryResult(betFacade);
        leagueFacade = new LeagueConfiguration().inMemoryLeagueFacade(resultFacade);
        autoImportFacade = new AutoImportConfiguration().inMemory(leagueFacade, externalClientMock);
    }

    public LeagueFacade leagueFacade() {
        return leagueFacade;
    }

    public BetFacade betFacade() {
        return betFacade;
    }

    public ResultFacade resultFacade() {
        return resultFacade;
    }

    public AutoImportFacade autoImportFacade() {
        return autoImportFacade;
    }

    public void setExternalSourceMatchList(List<MatchInfo> list) {
        externalClientMock.setMatchList(list);
    }

    public List<MatchInfo> getExternalSourceMatchList() {
        return externalClientMock.getMatchList();
    }

}
