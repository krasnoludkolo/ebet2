package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.data.repository.CrudRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;

class LeagueResultSpringDataAdapter implements Repository<LeagueResults> {

    private final CrudRepository<RoundResultsEntity, UUID> springRepository;

    LeagueResultSpringDataAdapter(CrudRepository<RoundResultsEntity, UUID> roundResultsEntityRepository) {
        this.springRepository = roundResultsEntityRepository;
    }


    @Override
    public void save(UUID uuid, LeagueResults leagueResults) {
        springRepository.save(leagueResults.toEntity());
    }

    @Override
    public Option<LeagueResults> findOne(UUID uuid) {
        List<RoundResultsEntity> entities = List.ofAll(springRepository.findAll())
                .filter(e -> e.getLeagueUUID().equals(uuid));
        if (entities.isEmpty()) {
            return Option.none();
        }
        return Option.of(LeagueResults.fromEntity(entities));
    }

    @Override
    public List<LeagueResults> findAll() {
        return List.ofAll(springRepository.findAll())
                .groupBy(RoundResultsEntity::getLeagueUUID)
                .map(t -> LeagueResults.fromEntity(t._2))
                .toList();
    }

    @Override
    public void delete(UUID uuid) {
        springRepository.delete(uuid);
    }

    @Override
    public void update(UUID uuid, LeagueResults leagueResults) {
        springRepository.save(leagueResults.toEntity());
    }
}
