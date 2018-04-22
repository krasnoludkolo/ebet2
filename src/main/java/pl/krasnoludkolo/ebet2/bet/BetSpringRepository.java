package pl.krasnoludkolo.ebet2.bet;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface BetSpringRepository extends CrudRepository<BetEntity, UUID> {
}
