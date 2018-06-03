package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

class LeagueResultsUpdater {

    private Repository<LeagueResults> resultsRepository;

    LeagueResultsUpdater(Repository<LeagueResults> resultsRepository) {
        this.resultsRepository = resultsRepository;
    }

    void updateResultsForMatchInLeague(LeagueResults resultsForLeague, MatchResult result, List<BetDTO> bets) {
        LeagueResults updatedResults = resultsForLeague.updateResults(result, bets);
        resultsRepository.update(resultsForLeague.getLeagueUUID(), updatedResults);
    }

}
