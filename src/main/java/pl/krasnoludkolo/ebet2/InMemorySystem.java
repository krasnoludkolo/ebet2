package pl.krasnoludkolo.ebet2;

import pl.krasnoludkolo.ebet2.bet.domain.BetConfiguration;
import pl.krasnoludkolo.ebet2.bet.domain.BetFacade;
import pl.krasnoludkolo.ebet2.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.results.domain.ResultConfiguration;
import pl.krasnoludkolo.ebet2.results.domain.ResultFacade;

public class InMemorySystem {

    private LeagueFacade leagueFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;

    public InMemorySystem() {
        betFacade = new BetConfiguration().inMemoryBetFacade();
        resultFacade = new ResultConfiguration().inMemoryResultController(betFacade);
        leagueFacade = new LeagueConfiguration().inMemoryLeagueFacade(resultFacade);
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
