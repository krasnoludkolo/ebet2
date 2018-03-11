package pl.krasnoludkolo.ebet2.domain.results;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.domain.Repository;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.domain.league.api.MatchResult;

class LeagueResultsUpdater {

    private Repository<LeagueResults> resultsRepository;

    LeagueResultsUpdater(Repository<LeagueResults> resultsRepository) {
        this.resultsRepository = resultsRepository;
    }

    void updateResultsForMatchInLeague(LeagueResults resultsForLeague, MatchResult result, List<BetDTO> bets) {
        bets
                .filter(betDTO -> betDTO.getBetTyp().match(result))
                .map(BetDTO::getUsername)
                .toJavaStream()
                .forEach(resultsForLeague::addPointToUser);
        resultsRepository.update(resultsForLeague.getLeagueUUID(), resultsForLeague);
    }


}
