package pl.krasnoludkolo.ebet2;

import pl.krasnoludkolo.ebet2.autoimport.AutoImportConfiguration;
import pl.krasnoludkolo.ebet2.autoimport.AutoImportFacade;
import pl.krasnoludkolo.ebet2.bet.BetConfiguration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.results.ResultConfiguration;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

public class InMemorySystem {

    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private AutoImportFacade autoImportFacade;

    public InMemorySystem() {
        betFacade = new BetConfiguration().inMemoryBetFacade();
        resultFacade = new ResultConfiguration().inMemoryResultController(betFacade);
        leagueFacade = new LeagueConfiguration().inMemoryLeagueFacade(resultFacade);
        autoImportFacade = new AutoImportConfiguration().inMemory(leagueFacade);
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
}
