package pl.krasnoludkolo.ebet2.results;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface LeagueResultsRepository extends CrudRepository<RoundResultsEntity, UUID> {
}
