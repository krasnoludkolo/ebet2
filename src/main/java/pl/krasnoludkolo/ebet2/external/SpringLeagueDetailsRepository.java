package pl.krasnoludkolo.ebet2.external;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface SpringLeagueDetailsRepository extends CrudRepository<LeagueDetails, UUID> {
}
