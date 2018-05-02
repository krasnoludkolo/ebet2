package pl.krasnoludkolo.ebet2.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface UserSpringRepository extends CrudRepository<UserEntity, UUID> {

}
