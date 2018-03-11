package pl.krasnoludkolo.ebet2;

import pl.krasnoludkolo.ebet2.domain.bet.BetConfiguration;
import pl.krasnoludkolo.ebet2.domain.bet.BetFacade;
import pl.krasnoludkolo.ebet2.domain.league.LeagueConfiguration;
import pl.krasnoludkolo.ebet2.domain.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.domain.results.ResultConfiguration;
import pl.krasnoludkolo.ebet2.domain.results.ResultFacade;

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
