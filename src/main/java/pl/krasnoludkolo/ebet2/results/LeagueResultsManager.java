package pl.krasnoludkolo.ebet2.results;

import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;
import java.util.function.Function;

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

    public Option<UserResult> getResultsFromLeagueToUser(UUID leagueUUID, final String username) {
        return repository
                .findOne(leagueUUID)
                .flatMap(toUserResultWithName(username));
    }

    private Function<LeagueResults, Option<UserResult>> toUserResultWithName(String username) {
        return leagueResults -> leagueResults.getUserResultForName(username);
    }

}
