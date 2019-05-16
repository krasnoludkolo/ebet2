package pl.krasnoludkolo.ebet2.points;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface LeagueResultsRepository extends CrudRepository<LeagueResultEntity, UUID> {
}
