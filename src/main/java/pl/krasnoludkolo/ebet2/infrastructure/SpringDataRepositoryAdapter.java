package pl.krasnoludkolo.ebet2.infrastructure;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.UUID;
import java.util.function.Function;

@AllArgsConstructor
public class SpringDataRepositoryAdapter<D, E> implements Repository<D> {

    private CrudRepository<E, UUID> repository;
    private Function<D, E> domainToEntityMapper;
    private Function<E, D> entityToDomainMapper;
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void save(UUID uuid, D d) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        E entity = domainToEntityMapper.apply(d);
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    @Override
    public Option<D> findOne(UUID uuid) {
        Option<E> entity = Option.of(repository.findOne(uuid));
        return entity.map(entityToDomainMapper);
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
        save(uuid, d);
    }

}