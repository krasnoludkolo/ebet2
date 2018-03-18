package pl.krasnoludkolo.ebet2.league;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface MatchRepository extends CrudRepository<Match, UUID> {
}
