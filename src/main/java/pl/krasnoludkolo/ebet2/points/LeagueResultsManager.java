package pl.krasnoludkolo.ebet2.points;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;

class LeagueResultsManager {

    private final Repository<LeagueResults> repository;

    LeagueResultsManager(Repository<LeagueResults> repository) {
        this.repository = repository;
    }

    LeagueResults getResultsForLeague(UUID leagueUUID) {
        return repository.findOne(leagueUUID).getOrElse(createResultsForLeague(leagueUUID));
    }

    private LeagueResults createResultsForLeague(UUID uuid) {
        LeagueResults leagueResults = LeagueResults.create(uuid);
        repository.save(uuid, leagueResults);
        return leagueResults;
    }

    List<UserResult> getResultsFromLeagueToUser(UUID leagueUUID, UUID uuid) {
        return repository
                .findOne(leagueUUID)
                .map(leagueResults -> leagueResults.getGeneralUserResult(uuid))
                .getOrElse(List.empty());
    }

    LeagueResults updateLeagueResult(LeagueResults leagueResults) {
        repository.update(leagueResults.getLeagueUUID(), leagueResults);
        return leagueResults;
    }

}
