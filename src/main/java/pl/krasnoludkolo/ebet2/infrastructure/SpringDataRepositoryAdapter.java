package pl.krasnoludkolo.ebet2.infrastructure;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;
import java.util.function.Function;

@AllArgsConstructor
public class SpringDataRepositoryAdapter<D, E> implements Repository<D> {

    private CrudRepository<E, UUID> repository;
    private Function<D, E> domainToEntityMapper;
    private Function<E, D> entityToDomainMapper;

    @Override
    public void save(UUID uuid, D d) {
        E entity = domainToEntityMapper.apply(d);
        repository.save(entity);
    }

    @Override
    public Option<D> findOne(UUID uuid) {
        Option<E> entity = Option.of(repository.findOne(uuid));
        Option<D> domain = entity.map(entityToDomainMapper);
        return domain;
    }

    @Override
    public List<D> findAll() {
        return List.ofAll(repository.findAll()).map(entityToDomainMapper);
    }

    @Override
    public void delete(UUID uuid) {
        repository.delete(uuid);
    }

    @Override
    public void update(UUID uuid, D d) {
        E entity = domainToEntityMapper.apply(d);
        repository.save(entity);
    }
}