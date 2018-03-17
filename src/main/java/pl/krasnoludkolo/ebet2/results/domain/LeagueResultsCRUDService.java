package pl.krasnoludkolo.ebet2.results.domain;

import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;
import java.util.function.Function;

class LeagueResultsCRUDService {

    private final Repository<LeagueResults> repository;

    LeagueResultsCRUDService(Repository<LeagueResults> repository) {
        this.repository = repository;
    }


    public LeagueResults getResultsForLeague(UUID leagueUUID) {
        return repository.findOne(leagueUUID).getOrElseThrow(IllegalStateException::new);
    }

    public void createResultsForLeague(UUID uuid) {
        LeagueResults leagueResults = new LeagueResults(uuid);
        repository.save(uuid, leagueResults);
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
