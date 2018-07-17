package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;

class LeagueResultsManager {

    private final Repository<LeagueResults> repository;

    LeagueResultsManager(Repository<LeagueResults> repository) {
        this.repository = repository;
    }

    public LeagueResults getResultsForLeague(UUID leagueUUID) {
        return repository.findOne(leagueUUID).getOrElse(createResultsForLeague(leagueUUID));
    }

    private LeagueResults createResultsForLeague(UUID uuid) {
        LeagueResults leagueResults = LeagueResults.create(uuid);
        repository.save(uuid, leagueResults);
        return leagueResults;
    }

    public List<UserResult> getResultsFromLeagueToUser(UUID leagueUUID, final String username) {
        return repository
                .findOne(leagueUUID)
                .map(leagueResults -> leagueResults.getGeneralUserResult(username))
                .getOrElse(List.empty());
    }

}
