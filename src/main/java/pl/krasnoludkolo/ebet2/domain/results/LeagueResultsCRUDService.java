package pl.krasnoludkolo.ebet2.domain.results;

import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.domain.Repository;
import pl.krasnoludkolo.ebet2.domain.league.exceptions.LeagueNotFound;

import java.util.UUID;

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

    public Option<UserResult> getResultsFromLeagueToUser(UUID leagueUUID, final String user) {
        return repository.findOne(leagueUUID)
                .getOrElseThrow(LeagueNotFound::new)
                .getUserResultList()
                .filter(userResult -> userResult.getName().equals(user))
                .peekOption();
    }
}
